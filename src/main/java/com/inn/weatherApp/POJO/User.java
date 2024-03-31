package com.inn.weatherApp.POJO;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.*;

import java.io.Serializable;


@NamedQuery(name = "User.findByEmail", query = "select u from User u where u.email=:email")


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="user")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="first_name")
    private String first_name;

    @Column(name="last_name")
    private String last_name;

    @Column(name="email")
    private String email;

    @Column(name="user_password")
    private String user_password;

    @Column(name="subscription")
    private boolean subscription;



}
