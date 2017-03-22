package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CategoryService;
import com.cherchy.markod.service.MarketService;
import com.cherchy.markod.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MarketService marketService;


    @Override
    public Product findOne(String id)
    {
        return productRepository.findOne(id);
    }

    @Override
    public List<Product> findAll()
    {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAll(String name)
    {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findAll(Category category)
    {
        List<Product> products = new ArrayList<>();
        List<Category> categories = categoryService.findAll(category);
        categories.add(category);

        for (Category cat : categories) {
            Query query = new Query();
            query.addCriteria(Criteria.where("categoryId").is(cat.getId()));
            products.addAll(mongoTemplate.find(query, Product.class));
        }
        return products;
    }

    @Override
    public List<Market> findByLocationNear(Product product, Point location, Distance distance)
    {
        List<Market> nearbyMarkets = marketService.findByLocationNear(location, distance);
        List<Market> searchResults = new ArrayList<>();

        if (nearbyMarkets.size() == 0)
            return searchResults;

        for (Market market : nearbyMarkets)
        {
            if (market.getProducts().contains(product))
            {
                Market marketHasProduct = new Market(market);
                market.getProducts().stream().forEach(p -> {
                    if (p.equals(product)) {
                        marketHasProduct.getProducts().add(p);
                    }
                });
                searchResults.add(marketHasProduct);
            }
        }

        return searchResults;
    }

    @Override
    public List<Market> findByLocationNear(List<Product> products, Point location, Distance distance)
    {
        List<Market> nearbyMarkets = marketService.findByLocationNear(location, distance);
        List<Market> searchResults = new ArrayList<>();

        if (nearbyMarkets.size() == 0)
            return searchResults;

        for (Market market : nearbyMarkets)
        {
            Market marketHasProduct = new Market(market);

            for (Product product : products)
            {
                if (market.getProducts().contains(product)) {
                    market.getProducts().stream().forEach(p -> {
                        if (p.equals(product)) {
                            marketHasProduct.getProducts().add(p);
                        }
                    });
                }
            }
            // any of search products found in this market ?
            if (marketHasProduct.getProducts().size() > 0) {
                searchResults.add(marketHasProduct);
            }
        }

        return searchResults;
    }

    @Override
    public Product create(Product product)
    {
        if (product.getId() != null) {
            return null;
        }
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product)
    {
        Product productPresent = productRepository.findOne(product.getId());
        if (productPresent == null) {
            // Cannot update
            return null;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(product.getId()));
        Update update = new Update();
        update.set("barcode", product.getBarcode());
        update.set("name", product.getName());
        update.set("categoryId", product.getCategoryId());

        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Product.class);
    }

    @Override
    public Product delete(String id)
    {
        return mongoTemplate.findAndRemove(
                new Query().addCriteria(Criteria.where("_id").is(id)),
                Product.class);
    }
}
