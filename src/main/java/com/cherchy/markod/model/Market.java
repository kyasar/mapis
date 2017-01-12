package com.cherchy.markod.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "markets")
public class Market {

    @Id
    private String id;

    private String name;

    private String address;

    @GeoSpatialIndexed
    private Point location;
}
