package com.cherchy.markod.service;

import com.cherchy.markod.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Product findOne(String _id);

    Product create(Product p);

    Product update(Product p);

    void delete(String _id);
}
