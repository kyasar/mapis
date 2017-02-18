package com.cherchy.markod.service;

import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Product;
import org.bson.types.ObjectId;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    List<Product> findAll(String name);

    List<Product> findAll(Category category);

    Product findOne(String id);

    Product create(Product p);

    Product update(Product p);

    void delete(String id);
}
