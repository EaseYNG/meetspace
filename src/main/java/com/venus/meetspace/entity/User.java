package com.venus.meetspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String nickname;
    @Column(nullable=false)
    private String username;
    @Column(nullable=false)
    private String password;

    // 登陆后属性(profile)
    private int age;
    private String gender;
    private String email;
    private String firstname;
    private String lastname;

    public User() {}

    public User(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String toString() {
        return String.format("id: %l, username: %s", id, username);
    }
}
