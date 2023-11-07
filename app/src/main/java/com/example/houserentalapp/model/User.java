package com.example.houserentalapp.model;

public class User {

    private String name;
    private String email;
    private String image;
    private String password;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getname(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

}
