package com.cherchy.markod.model;

import com.sun.istack.internal.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection="campaigns")
public class Campaign {

    @Id
    private String id;

    @NotNull
    private String title;

    @NotNull
    private boolean active;

    //@DBRef(lazy =  -> ref is not a solution relation contains price
    private List<Product> products = new ArrayList<>();

    public Campaign(String title, List<Product> products) {
        this.title = title;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
