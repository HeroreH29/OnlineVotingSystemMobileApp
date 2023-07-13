package com.example.onlinevotingapp;

import com.google.firebase.firestore.Exclude;

public class AdminCandidateItem {
    private String imageURL;
    private String candidateFullName;
    private String candidatePosition;
    private String candidateYearSection;
    private String candidatePersonalQualities;
    private String candidateBackground;
    private String candidateAchievements;
    private String registrationStatus;
    private String userType;
    private String documentID;

    public AdminCandidateItem () {
        //empty constructor needed
    }

    public AdminCandidateItem(String imageURL, String candidateFullName, String candidatePosition, String candidateYearSection, String candidatePersonalQualities, String candidateBackground, String candidateAchievements, String registrationStatus, String userType, String documentID) {
        this.imageURL = imageURL;
        this.candidateFullName = candidateFullName;
        this.candidatePosition = candidatePosition;
        this.candidateYearSection = candidateYearSection;
        this.candidatePersonalQualities = candidatePersonalQualities;
        this.candidateBackground = candidateBackground;
        this.candidateAchievements = candidateAchievements;
        this.registrationStatus = registrationStatus;
        this.userType = userType;
        this.documentID = documentID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCandidateFullName() {
        return candidateFullName;
    }

    public void setCandidateFullName(String candidateFullName) {
        this.candidateFullName = candidateFullName;
    }

    public String getCandidatePosition() {
        return candidatePosition;
    }

    public void setCandidatePosition(String candidatePosition) {
        this.candidatePosition = candidatePosition;
    }

    public String getCandidateYearSection() {
        return candidateYearSection;
    }

    public void setCandidateYearSection(String candidateYearSection) {
        this.candidateYearSection = candidateYearSection;
    }

    public String getCandidatePersonalQualities() {
        return candidatePersonalQualities;
    }

    public void setCandidatePersonalQualities(String candidatePersonalQualities) {
        this.candidatePersonalQualities = candidatePersonalQualities;
    }

    public String getCandidateBackground() {
        return candidateBackground;
    }

    public void setCandidateBackground(String candidateBackground) {
        this.candidateBackground = candidateBackground;
    }

    public String getCandidateAchievements() {
        return candidateAchievements;
    }

    public void setCandidateAchievements(String candidateAchievements) {
        this.candidateAchievements = candidateAchievements;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
