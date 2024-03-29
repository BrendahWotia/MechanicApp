package com.example.mobilemechanic.Model;

import com.google.firebase.database.Exclude;

public class MechanicModel {
    private String name;
    private String phone;
    private String location;
    private String email;
    private String imageUrl;
    private String id;
    private String speciality;
    private String longitude;
    private String latitude;

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public MechanicModel(String name, String phone, String location, String email, String imageUrl, String speciality,
                         String longitude, String latitude) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.email = email;
        this.imageUrl = imageUrl;
        this.speciality = speciality;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public MechanicModel(String name, String phone, String location, String email, String imageUrl) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.email = email;
        this.imageUrl = imageUrl;
    }


    public MechanicModel() {
    }

    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    //    public MechanicModel(String nameMech, String phoneMech, String emailMech, String sImage) {
//
//    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
