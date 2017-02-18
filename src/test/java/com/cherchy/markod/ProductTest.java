package com.cherchy.markod;

import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CategoryService;
import com.cherchy.markod.service.ProductService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Test
	public void t0_setUp() {
		mongoTemplate.remove(new Query(), "products");
        mongoTemplate.remove(new Query(), "categories");
		Category c1 = categoryService.create(new Category("Meyve", null));
		Assert.assertNotNull(c1);
		Assert.assertNotNull(categoryService.create(new Category("Armut", c1.getId())));
		Assert.assertNotNull(categoryService.create(new Category("Elma", c1.getId())));
		Assert.assertNotNull(categoryService.create(new Category("Muz", c1.getId())));
	}

	@Test
    public void t1_addProducts() {
		Category category = categoryService.findOne("Armut");
		Assert.assertNotNull(category);

		productService.create(new Product("Deveci Armut", "91222", category.getId()));
        productService.create(new Product("Antalya Armut", "91223", category.getId()));
        productService.create(new Product("Deveci Armut", "91224", category.getId()));

        category = categoryService.findOne("Elma");
        Assert.assertNotNull(category);
        productService.create(new Product("StarKing Elma", "78001", category.getId()));
        productService.create(new Product("GreenSmith Elma", "78001", category.getId()));

        Assert.assertEquals(5, productService.findAll().size());
	}

	@Test
    public void t2_findByCategory() {
        Category category = categoryService.findOne("Meyve");
        Assert.assertNotNull(category);

        List<Product> armuts = productService.findAll(category);
        Assert.assertNotNull(armuts);
        Assert.assertEquals(5, armuts.size());
    }
}
