//
// MainActivity.java
//
// This is the code for the main screen
//
// This file is part of the course "Build a Firebase Android Application"
//
// Written by Harrison Kong @ coursera.org
//

package com.phanquangminhlong.worldpeaceissuesmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Task 4 Step 3: implement FirebaseAuth.AuthStateListener interface
// Task 7 Step 4: implement UserRoleListener interface

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, UserRoleListener {

    public static Context context;

    // Task 7 Step 3: add userRole as private static property (String), default it to "member"
    private String userRole = "member";


    // Class properties --------------------------------------------------------------

    private AlertDialog alertDialog = null;   // holds the reference to any open dialog box

    private ArrayList<Issue> issueList = new ArrayList<Issue>();   // holds the issues

    // this adaptor is responsible to render the objects in our ArrayList to the screen
    private IssueListAdaptor issueListAdaptor = new IssueListAdaptor(this, issueList);

    private static final int RC_SIGN_IN = 123;

    private ValueEventListener valueEventListener = new ValueEventListener() {

        // Task 4 Step 1: uncomment method and copy the data subscription logic from onCreate to here
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            // this event is fired every time anything under /issues changes
            // so we reconstruct the ArrayList and tell the adapter to update the screen
            DatabaseHelper.buildIssuesList(snapshot, issueList);
            issueListAdaptor.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            alertFirebaseFailure(error.getMessage());
            error.toException();
        }
    };

    // Class methods -------------------------------------------------------------------

    // helper method to open the database error dialog box
    public void alertFirebaseFailure(String errorMessage) {

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("A Firebase Error Occurred")
                .setMessage(errorMessage)
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    // Task 5 Step 1: if user abort the sign in process, bring them right back

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {  // it is from the sign in activity

            WPIMApp.isSignInUIRunning = false;

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode != RESULT_OK) {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response == null) {
                    // User hit the back button, show sign in activity again
                    startSignInUI();
                }
                else {
                    // Show the error message
                    alertFirebaseFailure(response.getError().getLocalizedMessage());
                    // show sign in activity again
                        startSignInUI();
                }
            }
        }
    }

    // Task 4 Step 4: add auth state change listener to implement the interface FirebaseAuth.AuthStateListener

    @Override
    public void onAuthStateChanged(FirebaseAuth auth) {

        if (auth.getCurrentUser() != null) {      // authenticated, signed in

            // Task 7 Step 6: add or create user role
            DatabaseHelper.getOrAssignUserRole(this);

            // Task 4 Step 4: subscribe to data change
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/issues");
            dbRef.addValueEventListener(valueEventListener);

        } else {   // not authenticated or signed out

            // Task 4 Step 4: unsubscribe to data change
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/issues");
            dbRef.removeEventListener(valueEventListener);

            // clear arraylist
            issueList.clear();

            // update screen
            issueListAdaptor.notifyDataSetChanged();

            // Task 7 Step 6: clear toolbar (user role change won't trigger automatically)
            updateToolbar();

            // Task 4 Step 4: show sign in UI
            startSignInUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

        context = this;

        // Task 4 Step 5: set itself as the auth state change listener
        FirebaseAuth.getInstance().addAuthStateListener(this);


        ListView myListView = (ListView) findViewById(R.id.issuesListView);

        myListView.setAdapter(issueListAdaptor);

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Task 8: only allow managers to delete

                if(userRole.equals("manager")) {
                    openConfirmDeleteDialog(i);
                }
                return true;
            }
        });

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/issues");

        // Task 4 Step 1: move the data subscription logic here (only the two callbacks)
        //                to the class property valueEventListener
        //                them comment this block out

//        dbRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                // this event is fired every time anything under /issues changes
//                // so we reconstruct the ArrayList and tell the adapter to update the screen
//                DatabaseHelper.buildIssuesList(snapshot, issueList);
//                issueListAdaptor.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                alertFirebaseFailure(error.getMessage());
//                error.toException();
//            }
//        });

        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddIssueDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Task 5 Step 2: provide sign-out function

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sign_out_menu_item:

                alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // clear the array and update the screen
                            issueList.clear();
                            issueListAdaptor.notifyDataSetChanged();
                            // Task 5 Step 2: add sign out code here
                            FirebaseAuth.getInstance().signOut();
                        }})
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        if(alertDialog != null) {
            try {   // there is a chance the property is not null even though the dialog box has
                    // been dismissed, such as using the back button

                alertDialog.dismiss();  // dismiss prior to rotation to avoid memory leak
            } finally {
                alertDialog = null;
            }

        }
    }

    // Task 7 Step 5: add a user role change listener to implement the UserRoleListener interface

    public void onUserRoleChange(String newRole) {

        userRole = newRole;
        updateToolbar();
    }

    // helper method to open the add new issue dialog box
    public void openAddIssueDialog() {

        Intent intent = new Intent(this, AddIssueActivity.class);
        startActivity(intent);
    }

    // helper method to open the confirm delete dialog box
    private void openConfirmDeleteDialog(final int i) {

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this entry?\n\n" +
                        issueList.get(i).getDescription())
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int button) {

                        // use the DatabaseHelper object to delete it
                        DatabaseHelper.deleteIssue(issueList.get(i).myGetKey());
                    }
                } )
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.red_cross_circle)
                .setCancelable(false)
                .show();
    }

    // helper method to open the confirm status toggle dialog box
    public void openStatusToggleDialog(final int i) {

        String oldStatus = issueList.get(i).getResolved();
        final String newStatus = (oldStatus.equals("no") ? "yes" : "no");

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Status Update")
                .setMessage("Update the resolved status this entry:\n\n" +
                        issueList.get(i).getDescription() + "\n\nfrom '" +
                        oldStatus + "' to '" + newStatus + "'?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int button) {

                        // use the DatabaseHelper object to update the resolved status
                        DatabaseHelper.updateIssue(issueList.get(i).myGetKey(), newStatus);
                    }
                } )
                .setNegativeButton("Cancel", null)
                .setIcon(newStatus == "yes" ? R.drawable.green_check_mark : R.drawable.red_hourglass)
                .setCancelable(false)
                .show();
    }

    // Task 4 Step 2: function to start the sign in UI

    private void startSignInUI() {

        if (WPIMApp.isSignInUIRunning || FirebaseAuth.getInstance().getCurrentUser() != null) { return; }

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        WPIMApp.isSignInUIRunning = true;

    }

    // Task 7 Step 2: add a function to set the toolbar to either World Peace Issues Monitor
    //                or the user's name and role

    private void updateToolbar() {

        String title = "World Peace Issues Monitor";
        String displayRole = "";

        // if signed in, get display name and role in parentheses

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // signed in, attempt to get name

            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

            // there is a bug, for new users, the displayName does not get updated immediately
            // so we need go get it later

            if (displayName == null) {

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateToolbar();
                    }
                }, 1000);

            } else {
                title = displayName;
            }

            displayRole = " (" + userRole + ")";
        }

        ((Toolbar)findViewById(R.id.app_toolbar)).setTitle(title + displayRole);
    }

}