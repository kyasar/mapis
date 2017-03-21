package com.cherchy.markod.repository;

import com.cherchy.markod.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByNameContainingIgnoreCase(String name);
}
