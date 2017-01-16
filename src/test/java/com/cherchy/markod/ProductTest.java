package com.cherchy.markod;

import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.ProductService;
import org.json.JSONObject;
import org.junit.Assert;
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
	private ProductRepository productRepository;

	@Test
	public void t0_setUp() {
		mongoTemplate.remove(new Query(), "products");
	}

	@Test
	public void t1_insertProduct() {
		Product p1 = new Product("Urun1", "12341");
		Product p2 = new Product("Urun2", "12342");
		Product p3 = new Product("Urun3", "12343");

		productService.create(p1).getId();
		productService.create(p2).getId();
		productService.create(p3).getId();
	}
}
