package com.areena.merchant.Model;

public class TutionDataModel {
    String city;
    String subLocality;
    String address;
    String classs;
    String email;
    String fee;
    String gender;
    String locality;
    String name;
    String phoneNumber;
    String state;
    String studentName;
    String subject;
    long timestamp;
    String uid;
    String status;
    String id;

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getClasss() {
        return classs;
    }

    public String getEmail() {
        return email;
    }

    public String getFee() {
        return fee;
    }

    public String getGender() {
        return gender;
    }

    public String getLocality() {
        return locality;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getState() {
        return state;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public String getCity() {
        return city;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public TutionDataModel(String city) {
        this.city = city;
    }
}
