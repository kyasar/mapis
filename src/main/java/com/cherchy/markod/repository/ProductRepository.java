package com.cherchy.markod.repository;

import com.cherchy.markod.model.Product;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    // there must be location variable in Product type
    // look at Query Builder mechanism
    //GeoResults<Product> findByLocationNear(Point location, Distance distance);

    //List<Product> findByLocationWithin(Polygon polygon);

    List<Product> findByNameContainingIgnoreCase(String name);
}
