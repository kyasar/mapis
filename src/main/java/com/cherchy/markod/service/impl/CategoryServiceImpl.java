package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.model.type.CampaignType;
import com.cherchy.markod.repository.CampaignRepository;
import com.cherchy.markod.repository.CategoryRepository;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CategoryService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@PropertySource("classpath:markod.properties")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll()
    {
        return categoryRepository.findAll();
    }

    private void findAllSubCategories(String id, List<Category> categories)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("parentCategoryId").is(id));
        List<Category> subCategories = mongoTemplate.find(query, Category.class);
        categories.addAll(subCategories);

        if (id == null)
            return;

        for (Category category : subCategories) {
            findAllSubCategories(category.getId(), categories);
        }
    }

    @Override
    public List<Category> findAll(Category category)
    {
        List<Category> categories = new ArrayList<>();
        findAllSubCategories(category.getId(), categories);
        return categories;
    }

    @Override
    public List<Category> findAll(String name)
    {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Category findOne(String id)
    {
        return categoryRepository.findOne(id);
    }

    @Override
    public Category create(Category category)
    {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category)
    {
        return null;
    }

    @Override
    public void delete(String id)
    {
        Category categoryToDelete = categoryRepository.findOne(id);
        if (categoryToDelete == null)
            return;

        List<Category> subCategories = findAll(categoryToDelete);
        for (Category category : subCategories)
            delete(category.getId());
        categoryRepository.delete(id);
    }
}
