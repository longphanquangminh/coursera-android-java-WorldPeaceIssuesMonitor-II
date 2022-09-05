//
// AddIssueActivity.java
//
// This is the code for the add new issue dialog box
//
// This file is part of the course "Build a Firebase Android Application"
//
// Written by Harrison Kong @ coursera.org
//

package com.phanquangminhlong.worldpeaceissuesmonitor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

public class AddIssueActivity extends AppCompatActivity {

    private AlertDialog alertDialog;

    int oldSeverity = 1;
    int oldResolved = 0;
    String oldDescription = "";

    int newSeverity = 1;
    int newResolved = 0;
    String newDescription = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        setFinishOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {

        if (!dataChanged()) { finish(); }

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Unsaved data will be lost.")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int button) {
                        finish();
                    }
                } )
                .setNegativeButton("Cancel", null)
                .setIcon(R.drawable.red_cross_circle)
                .setCancelable(false)
                .show();
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

    private void getEnteredData() {

        SeekBar severitySeekBar = (SeekBar)findViewById(R.id.severitySeekBar);
        newSeverity = severitySeekBar.getProgress();

        SeekBar resolvedSeekBar = (SeekBar)findViewById(R.id.resolvedSeekBar);
        newResolved = resolvedSeekBar.getProgress();

        newDescription = ((EditText)findViewById(R.id.descriptionEditText)).getText().toString();

    }

    private boolean dataChanged() {

        getEnteredData();

        return (oldSeverity != newSeverity
                || oldResolved != newResolved
                || !oldDescription.equals(newDescription));

    }

    private boolean validateInput() {

        getEnteredData();
        return newDescription.length() > 0;
    }

    private boolean addNewIssueToFirebase(int severity, int resolved, String description) {

        final String[] severityStrings = new String[] { "minor", "moderate", "major" };
        final String[] resolvedStrings = new String[] { "no", "yes" };

        if (severity < 0 || severity > 2) { return false; }  // 0, 1 or 2
        if (resolved < 0 || resolved > 1) { return false; }  // 0 or 1
        if (description.length() <= 0) { return false; }     // must not be an empty string

        String newSeverityString = severityStrings[severity];
        String newResolvedString = resolvedStrings[resolved];

        // build a new Issue object
        Issue newIssue = new Issue(newSeverityString, newResolvedString, description);

        // Use the DatabaseHelper object to save it to Firebase
        DatabaseHelper.addNewIssue(newIssue);

        return true;
    }

    public void addIssueButtonClicked(View view) {

        if (validateInput()) {
            addNewIssueToFirebase(newSeverity, newResolved, newDescription);
            finish();
        }
        else {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Required Data Missing")
                    .setMessage("You must enter a description.")
                    .setPositiveButton("OK", null)
                    .setIcon(R.drawable.red_cross_circle)
                    .setCancelable(false)
                    .show();
        }
    }
}