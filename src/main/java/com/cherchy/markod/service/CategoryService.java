package com.cherchy.markod.service;

import com.cherchy.markod.model.Category;

import java.util.List;

/**
 * Created by kadir on 16.02.2017.
 */
public interface CategoryService {

    List<Category> findAll();

    List<Category> findAll(String parentCategoryId);

    Category create(Category category, String parentCategoryId);

    void delete(String id);

}
