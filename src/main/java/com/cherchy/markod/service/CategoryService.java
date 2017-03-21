package com.cherchy.markod.service;

import com.cherchy.markod.model.Category;

import java.util.List;

/**
 * Created by kadir on 16.02.2017.
 */
public interface CategoryService {

    List<Category> findAll();

    List<Category> findAll(Category category);

    List<Category> findAll(String name);

    Category findOne(String id);

    Category create(Category category);

    Category update(Category category);

    void delete(String id);
}
