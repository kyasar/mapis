package com.cherchy.markod.model;

import com.sun.istack.internal.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="products")
public class Product {

    @Id
    private String id;

    @NotNull
    private String name;

    private Price price;

    @NotNull
    @Indexed
    private String barcode;

    @NotNull
    private String categoryId;

    public Product() {
    }

    // Market or Campaign custom price relationship
    public Product(String id, Price price) {
        this.id = id;
        this.price = price;
    }

    public Product(String name, String barcode, String categoryId) {
        this.name = name;
        this.barcode = barcode;
        this.categoryId = categoryId;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object p) {
        if (this.getId().equals(((Product) p).getId()))
            return true;
        else
            return false;
    }
}
