package com.miniblog.miniblog.models;


import com.sun.istack.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;


    private String username;
    private String password;
    private boolean active;
    private String roles;

    public User(){}

    public User(String username, String password, String roles)  {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.roles = "USER";
        this.active = true;
    }

//
//    public Status getBody() {
//        return body;
//    }

//    public void setBody(Status body) {
//        this.body = body;
//    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id= id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
