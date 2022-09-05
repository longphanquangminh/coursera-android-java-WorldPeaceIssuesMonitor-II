//
// Issue.java
//
// This is the object representation of the objects in our database: an Issue
//
// This file is part of the course "Build a Firebase Android Application"
//
// Written by Harrison Kong @ coursera.org
//

package com.phanquangminhlong.worldpeaceissuesmonitor;

import java.util.Arrays;

public class Issue {

    private String key;
    private String severity;
    private String resolved;
    private String description;

    // constructors

    public Issue(String newSeverity, String newResolved, String newDescription) {
        setKey("");
        setSeverity(newSeverity);
        setResolved(newResolved);
        setDescription(newDescription);
    }

    public Issue(String newKey, String newSeverity, String newResolved, String newDescription) {
        setKey(newKey);
        setSeverity(newSeverity);
        setResolved(newResolved);
        setDescription(newDescription);
    }

    // getters

    public String myGetKey() {   // by naming it differently Firebase will not call it
        return key;
    }

    public String getSeverity() {
        return severity;
    }

    public String getResolved() {
        return resolved;
    }

    public String getDescription() {
        return description;
    }

    // setters

    public void setKey(String newKey) {
        if (newKey == null) { newKey = ""; }
        key = newKey;
    }

    public void setSeverity(String newSeverity) {

        if (newSeverity == null) { newSeverity = "?"; }

        newSeverity.trim();

        String[] validSeverityValues = { "minor", "moderate", "major", "?" };
        if (!Arrays.asList(validSeverityValues).contains(newSeverity)) { newSeverity = "?"; }

        severity = newSeverity;
    }

    public void setResolved(String newResolved) {

        if (newResolved == null) { newResolved = "?"; }

        newResolved.trim();

        String[] validResolvedValues = { "yes", "no", "?" };
        if (!Arrays.asList(validResolvedValues).contains(resolved)) { resolved = "?"; };

        resolved = newResolved;
    }

    public void setDescription(String newDescription) {

        if (newDescription == null || newDescription.trim().equals("")) { newDescription = "< description missing >"; };

        this.description = newDescription.trim();
    }

}
