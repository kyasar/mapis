package com.cherchy.markod;

import com.cherchy.markod.model.*;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CategoryService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CategoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryService productService;

    @Test
    public void t0_setUp() {
        System.out.println("Setting up before tests..");
        mongoTemplate.remove(new Query(), "products");
        mongoTemplate.remove(new Query(), "categories");
    }

    @Test
    public void t1_createNewCategory() {
        Category c1 = categoryService.create(new Category("Meyve", null));
        Assert.assertNotNull(c1);
        Category c2 = categoryService.create(new Category("Sebze", null));
        Assert.assertNotNull(c2);
        Category c3 = categoryService.create(new Category("Cikolata", null));
        Assert.assertNotNull(c3);

        Category c4 = categoryService.create(new Category("armut", c1.getId()));
        Assert.assertNotNull(c4);
        Category c5 = categoryService.create(new Category("Deveci", c4.getId()));
        Assert.assertNotNull(c5);
        Category c6 = categoryService.create(new Category("Antalya", c4.getId()));
        Assert.assertNotNull(c6);
        Category c7 = categoryService.create(new Category("Ankara", c4.getId()));
        Assert.assertNotNull(c7);
    }

    @Test
    public void t2_getAllCategories() {
        // Categories under Meyve
        Category category = categoryService.findOne("Meyve");
        Assert.assertNotNull(category);
        Assert.assertEquals(4, categoryService.findAll(category.getId()).size());

        // Categories under Sebze
        category = categoryService.findOne("Sebze");
        Assert.assertNotNull(category);
        Assert.assertEquals(0, categoryService.findAll(category.getId()).size());

        // Number of Main Categories
        Assert.assertEquals(3, categoryService.findAll(null).size());

        // All categories
        Assert.assertEquals(7, categoryService.findAll().size());
    }

    @Test
    public void t3_removeCategory() {
        Category category = categoryService.findOne("Meyve");
        Assert.assertNotNull(category);
        Assert.assertEquals(7, categoryService.findAll().size());

        categoryService.delete(category.getId());
        Assert.assertEquals(2, categoryService.findAll().size());
    }

    @Test
    public void t4_clear() {
        System.out.println("All tests are done.");
    }
}
