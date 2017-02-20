package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CategoryService;
import com.cherchy.markod.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAll(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findAll(Category category) {
        List<Product> products = new ArrayList<>();

        for (Category cat : categoryService.findAll(category.getId())) {
            Query query = new Query();
            query.addCriteria(Criteria.where("categoryId").is(cat.getId()));
            products.addAll(mongoTemplate.find(query, Product.class));
        }
        return products;
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
