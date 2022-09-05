//
// DatabaseHelper.java
//
// This object encapsulates all the Firebase functionality of our application
//
// This file is part of the course "Build a Firebase Android Application"
//
// Written by Harrison Kong @ coursera.org
//

package com.phanquangminhlong.worldpeaceissuesmonitor;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public final class DatabaseHelper {

    // helper method to open the database error dialog box
    private static void alertError(String errorMessage) {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.context)
                .setTitle("A Firebase Error Occurred")
                .setMessage(errorMessage)
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    // Task 7 Step 1: add get or add user role

    public static final String getOrAssignUserRole(final UserRoleListener listener) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference usersRootRef = FirebaseDatabase.getInstance().getReference("/users");

        // have learner type this? it will auto fill

        usersRootRef.child(uid + "/role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String newRole = snapshot.getValue(String.class);

                if (newRole == null) {
                    // does not exists, let's set it to member, and tell the listener
                    snapshot.getRef().setValue("member")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.onUserRoleChange("member");
                        }
                    });
                }
                else {
                    // already exists, tell the listener what the role in the database is
                    listener.onUserRoleChange(newRole);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertError(error.getMessage());
            }
        });

//         function will return asychronously before the above functions are called!

        return "member";  // until one of the callback functions are called

    }

    public static final void buildIssuesList(@NonNull DataSnapshot snapshot, ArrayList<Issue> issuesList) {

        issuesList.clear();  // clear the old data

        for (DataSnapshot issue : snapshot.getChildren()) {

            // retrieve the id
            String key = issue.getKey();
            // retrieve the severity
            String severity = issue.child("severity").getValue(String.class);
            // retrieve the resolved status
            String resolved = issue.child("resolved").getValue(String.class);
            // retrieve the description
            String description = issue.child("description").getValue(String.class);

            // make a new Issue object and add it to the ArrayList
            issuesList.add(new Issue(key, severity, resolved, description));
        }

    }

    public static final void addNewIssue(Issue newIssue) {

        // use the reference to root node /issues
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/issues");
        // to push a new child node and get the reference to it
        DatabaseReference newIssueRef = rootRef.push();
        // set the children nodes of this new reference to our object's properties
        newIssueRef.setValue(newIssue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.context, "Added successfully!", Toast.LENGTH_LONG ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        alertError(e.getLocalizedMessage());
                    }
                });
    }

    public static final void deleteIssue(String keyToDelete) {

        // get a reference to the issue child node to be deleted
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/issues/" + keyToDelete);
        // remove the child node and its children
        issueRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.context, "Deleted successfully!", Toast.LENGTH_LONG ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        alertError(e.getLocalizedMessage());
                    }
                });
    }

    public static final void updateIssue(String issueKey, String newStatus) {

        // get a reference to the child node to be updated and to the "resolved" child node
        DatabaseReference issueRef = FirebaseDatabase.getInstance()
                        .getReference("/issues/" + issueKey + "/resolved");
        // set the value of the node
        issueRef.setValue(newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.context, "Updated successfully!", Toast.LENGTH_LONG ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        alertError(e.getLocalizedMessage());
                    }
                });
        }
}
