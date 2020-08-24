package com.miniblog.miniblog.models;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "post")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id")
    private int id;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "id", nullable = true)
    private User user;

    public Status(){ }

    public Status(String aBody) {
        this.body = aBody;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String status) {
        this.body = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
