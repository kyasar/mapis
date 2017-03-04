package com.cherchy.markod.service;

import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.model.Product;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;

import java.util.List;

public interface ProductService {

    Product findOne(String id);

    List<Product> findAll();

    List<Product> findAll(String name);

    List<Product> findAll(Category category);

    List<Product> findByLocationNear(Product p, Point location, Distance distance);

    Product create(Product p);

    Product update(Product p);

    void delete(String id);
}
