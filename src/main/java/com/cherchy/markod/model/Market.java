package com.cherchy.markod.model;

import com.sun.istack.internal.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /*
    Products which the market sells
     */
    private Set<Product> products;

    public Market(String name, String address, Point location) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.products = new HashSet<>();
    }

    public Market(String id) {
        this.id = id;
    }

    public Market() { }

    // Used to return in product search results as copy constructor
    public Market(Market market) {
        this.id = market.getId();
        this.name = market.getName();
        this.address = market.getAddress();
        this.location = market.getLocation();
        this.products = new HashSet<>();
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

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Market market = (Market) o;

        return id.equals(market.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
