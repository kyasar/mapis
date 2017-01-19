package com.cherchy.markod.model;

import com.sun.istack.internal.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "markets")
public class Market {

    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private boolean active;

    @NotNull
    private String address;

    @NotNull
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Point location;

    // list of ids of follower customers
    private List<String> followers;

    @DBRef
    private List<Campaign> campaigns = new ArrayList<>();

    public Market(String name, String address, Point location) {
        this.name = name;
        this.address = address;
        this.location = location;
    }

    public Market(String id) {
        this.id = id;
    }

    public Market() { }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public void addToFollowers(String cid) {
        if (this.followers == null)
            this.followers = new ArrayList<>();
        this.followers.add(cid);
    }

    public void removeFromFollowers(String cid) {
        if (this.followers == null)
            return;
        this.followers.removeIf(id -> id.equals(cid));
    }
}
