package com.cherchy.markod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @JsonIgnore
    private Set<String> roles;

    @DBRef
    private List<Market> markets;

    @DBRef
    private List<Market> followingMarkets;

    @DBRef
    private List<Campaign> campaigns;

    public Customer(
            String name,
            String surname,
            String email,
            String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<String>();
        this.roles.add("ROLE_USER");
    }

    public String getId() {
        return id;
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

    public List<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }

    public List<Market> getFollowingMarkets() {
        return followingMarkets;
    }

    public void setFollowingMarkets(List<Market> followingMarkets) {
        this.followingMarkets = followingMarkets;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }
}