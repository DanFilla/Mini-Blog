package com.miniblog.miniblog.models;

import javax.persistence.*;

@Entity
@Table(name = "UserRelationship")
public class UserRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @OneToOne
    private User userIdOne;
    @OneToOne
    private User userIdTwo;

    private int statusCode;
    private int actionUserId;

    public UserRelationship(){};

    public UserRelationship(User userIdOne, User userIdTwo, int statusCode, int actionUserId) {
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.statusCode = statusCode;
        this.actionUserId = actionUserId;
    }

    public int getId() {
        return id;
    }

    public User getUserIdOne() {
        return userIdOne;
    }

    public void setUserIdOne(User userIdOne) {
        this.userIdOne = userIdOne;
    }

    public User getUserIdTwo() {
        return userIdTwo;
    }

    public void setUserIdTwo(User userIdTwo) {
        this.userIdTwo = userIdTwo;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(int actionUserId) {
        this.actionUserId = actionUserId;
    }
}
