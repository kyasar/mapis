package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findOne(String id) {
        return productRepository.findOne(id);
    }

    @Override
    public Product create(Product p) {
        if (p.getId() != null) {
            return null;
        }
        return productRepository.save(p);
    }

    @Override
    public Product update(Product p) {
        Product pPresent = productRepository.findOne(p.getId().toString());
        if (pPresent == null) {
            // Cannot update
            return null;
        }

        return productRepository.save(p);
    }

    @Override
    public void delete(String id) {
        productRepository.delete(id);
    }
}
