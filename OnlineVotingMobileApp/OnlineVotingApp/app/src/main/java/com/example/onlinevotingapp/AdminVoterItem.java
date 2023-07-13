package com.example.onlinevotingapp;

import com.google.firebase.firestore.Exclude;

import java.util.Comparator;
import java.util.Map;

public class AdminVoterItem {
    private String userType, voterCollege, voterFirstName, voterLastName, voterUID, voterYearSection, documentID;
    Boolean hasVoted;

    public AdminVoterItem () {
        //Needed by Firestore
    }

    public AdminVoterItem(String userType, String voterCollege, String voterFirstName, String voterLastName, String voterUID, String voterYearSection, String documentID, Boolean hasVoted) {
        this.userType = userType;
        this.voterCollege = voterCollege;
        this.voterFirstName = voterFirstName;
        this.voterLastName = voterLastName;
        this.voterUID = voterUID;
        this.voterYearSection = voterYearSection;
        this.hasVoted = hasVoted;
        this.documentID = documentID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getVoterCollege() {
        return voterCollege;
    }

    public void setVoterCollege(String voterCollege) {
        this.voterCollege = voterCollege;
    }

    public String getVoterFirstName() {
        return voterFirstName;
    }

    public void setVoterFirstName(String voterFirstName) {
        this.voterFirstName = voterFirstName;
    }

    public String getVoterLastName() {
        return voterLastName;
    }

    public void setVoterLastName(String voterLastName) {
        this.voterLastName = voterLastName;
    }

    public String getVoterUID() {
        return voterUID;
    }

    public void setVoterUID(String voterUID) {
        this.voterUID = voterUID;
    }

    public String getVoterYearSection() {
        return voterYearSection;
    }

    public void setVoterYearSection(String voterYearSection) {
        this.voterYearSection = voterYearSection;
    }

    public Boolean getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
