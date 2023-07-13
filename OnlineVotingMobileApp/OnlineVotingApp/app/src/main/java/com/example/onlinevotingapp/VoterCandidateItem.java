package com.example.onlinevotingapp;

import com.google.firebase.firestore.Exclude;

import java.util.Comparator;
import java.util.Map;

public class VoterCandidateItem {
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
    private Map<String, Boolean> Voters;
    private Integer posOrder;

    public Integer getPosOrder() {
        return posOrder;
    }

    public void setPosOrder(Integer posOrder) {
        this.posOrder = posOrder;
    }

    public Map<String, Boolean> getVoters() {
        return Voters;
    }

    public void setVoters(Map<String, Boolean> voters) {
        Voters = voters;
    }

    public VoterCandidateItem() {
        //empty constructor needed
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public VoterCandidateItem(String imageURL, String candidateFullName, String candidatePosition, String candidateYearSection, String candidatePersonalQualities, String candidateBackground, String candidateAchievements, String registrationStatus, String userType, String documentID, Map<String, Boolean> voters, Integer posOrder) {
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
        Voters = voters;
        this.posOrder = posOrder;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCandidateFullName() {
        return candidateFullName;
    }

    public String getCandidatePosition() {
        return candidatePosition;
    }

    public String getCandidateYearSection() {
        return candidateYearSection;
    }

    public String getCandidatePersonalQualities() {
        return candidatePersonalQualities;
    }

    public String getCandidateBackground() {
        return candidateBackground;
    }

    public String getCandidateAchievements() {
        return candidateAchievements;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
