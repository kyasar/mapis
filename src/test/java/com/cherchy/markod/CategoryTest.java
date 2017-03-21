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

        Category domates = categoryService.create(new Category("Domates", c2.getId()));
        Assert.assertNotNull(domates);
        Category biber = categoryService.create(new Category("Biber", c2.getId()));
        Assert.assertNotNull(biber);
    }

    @Test
    public void t2_getAllCategories() {
        // Categories under Meyve
        List<Category> categories = categoryService.findAll("Meyve");
        Assert.assertNotNull(categories);
        Category meyveCat = categories.get(0);

        // Categories under Sebze
        categories = categoryService.findAll("Sebze");
        Assert.assertNotNull(categories);
        Category sebzeCat = categories.get(0);

        Assert.assertEquals(4, categoryService.findAll(meyveCat).size());
        Assert.assertEquals(2, categoryService.findAll(sebzeCat).size());
    }

    @Test
    public void t3_removeCategory() {
        List<Category> categories = categoryService.findAll("Meyve");
        Assert.assertNotNull(categories);
        Assert.assertNotEquals(0, categories.size());
        Category meyveCat = categories.get(0);

        Assert.assertEquals(9, categoryService.findAll().size());
        categoryService.delete(meyveCat.getId());
        Assert.assertEquals(4, categoryService.findAll().size());
    }

    @Test
    public void t4_clear() {
        System.out.println("All tests are done.");
    }
}
