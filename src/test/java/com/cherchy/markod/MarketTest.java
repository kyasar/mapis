package com.cherchy.markod;

import com.cherchy.markod.model.*;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.gen5.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarketTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MarketService marketService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CategoryService categoryService;

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "markets");
        mongoTemplate.remove(new Query(), "campaigns");
        mongoTemplate.remove(new Query(), "customers");
        mongoTemplate.remove(new Query(), "products");

        Category category1 = categoryService.create(new Category("Category-1", null));
        Assert.assertNotNull(category1);

        Product p1= productService.create(new Product("Urun1", "2222", category1.getId()));
        Product p2= productService.create(new Product("Urun2", "3333", category1.getId()));
        Product p3= productService.create(new Product("Urun3", "4444", category1.getId()));

        Assert.assertEquals(3, productService.findAll().size());

        List<Market> markets = Arrays.asList(
                new Market("Altunbilekler", "address 1", new Point(39.894177, 32.801571)),
                new Market("Migros", "address 2", new Point(39.893387, 32.79788)),
                new Market("Cagdas", "address 3", new Point(39.892827, 32.799082)),
                new Market("Kiler", "address 4", new Point(39.895, 32.797837)),
                new Market("Makro", "address 5", new Point(39.889732, 32.800627))
        );

        for (Market m : markets) {
            Market created = marketService.create(m);
            if (created != null) {
                System.out.println("Market created: " + created.getId());
            } else {
                System.err.println("Market NOT created: " + m.getId());
            }
        }
        Assert.assertEquals(5, marketService.findAll().size());

        List<Market> markets1 = marketService.findAll();
        Market m1 = markets1.get(0);
        Market m2 = markets1.get(1);
        Market m3 = markets1.get(2);

        marketService.addProductToShelf(m1.getId(), new Product(p1.getId(), new Price(10, 50)));
        marketService.addProductToShelf(m2.getId(), new Product(p1.getId(), new Price(9, 50)));

        marketService.addProductToShelf(m1.getId(), new Product(p2.getId(), new Price(5, 50)));
        marketService.addProductToShelf(m1.getId(), new Product(p3.getId(), new Price(20, 0)));

        Assert.assertEquals(3, marketService.findOne(m1.getId()).getProducts().size());
        Assert.assertEquals(1, marketService.findOne(m2.getId()).getProducts().size());
        Assert.assertEquals(0, marketService.findOne(m3.getId()).getProducts().size());
    }

    @Test
    public void t1_findMarketsByLocation() {

        Product productSearch = productService.findAll("Urun1").get(0);
        Assert.assertNotNull(productSearch);

        List<Product> urun1Results = productService.findByLocationNear(productSearch,
                new Point(39.893387, 32.79788), new Distance(0.2, Metrics.KILOMETERS));

        for (Product p : urun1Results) {
            System.out.println(p.getName() + " " + p.getPrice().toString());
        }
    }
}
