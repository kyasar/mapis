package com.cherchy.markod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "customers")
public class Customer {

    @Id
    private String id;

    @NotNull
    private String name;

    private String surname;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private int points;

    @JsonIgnore
    private Set<String> roles;

    @DBRef
    private Set<Market> markets;

    private List<Market> followingMarkets;

    public Customer(
            String name,
            String surname,
            String email,
            String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.points = 0;
        this.roles = new HashSet<String>();
        this.roles.add("ROLE_USER");
        this.followingMarkets = new ArrayList<>();
    }

    public Customer(String id) {
        this.id = id;
    }

    public Customer() { }

    public String getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(Set<Market> markets) {
        this.markets = markets;
    }

    public List<Market> getFollowingMarkets() {
        return followingMarkets;
    }

    public void setFollowingMarkets(List<Market> followingMarkets) {
        this.followingMarkets = followingMarkets;
    }

}
