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
                new Market("Migros", "address 2", new Point(39.893329, 32.797990)),
                new Market("Cagdas", "address 3", new Point(39.892827, 32.799082)),
                new Market("Kiler", "address 4", new Point(39.895, 32.797837)),
                new Market("Makro", "address 5", new Point(39.88944557, 32.8070426))
        );

        Market altunbilekler = marketService.create(markets.get(0));
        Market migros = marketService.create(markets.get(1));
        Market cagdas = marketService.create(markets.get(2));
        Market kiler = marketService.create(markets.get(3));
        Market makro = marketService.create(markets.get(4));

        Assert.assertEquals(5, marketService.findAll().size());

        // Altunbilekler - 3 products
        marketService.addProductToShelf(altunbilekler.getId(), new Product(p1.getId(), new Price(10, 50)));
        marketService.addProductToShelf(altunbilekler.getId(), new Product(p1.getId(), new Price(9, 50)));
        marketService.addProductToShelf(altunbilekler.getId(), new Product(p2.getId(), new Price(5, 50)));
        marketService.addProductToShelf(altunbilekler.getId(), new Product(p3.getId(), new Price(20, 0)));

        // Migros
        marketService.addProductToShelf(migros.getId(), new Product(p1.getId(), new Price(8, 50)));
        marketService.addProductToShelf(migros.getId(), new Product(p3.getId(), new Price(6, 50)));

        // Makro
        marketService.addProductToShelf(makro.getId(), new Product(p1.getId(), new Price(10, 0)));
        marketService.addProductToShelf(makro.getId(), new Product(p2.getId(), new Price(5, 20)));
        marketService.addProductToShelf(makro.getId(), new Product(p2.getId(), new Price(5, 25)));

        Assert.assertEquals(3, marketService.findOne(altunbilekler.getId()).getProducts().size());
        Assert.assertEquals(2, marketService.findOne(migros.getId()).getProducts().size());
        Assert.assertEquals(2, marketService.findOne(makro.getId()).getProducts().size());
    }

    @Test
    public void t1_findMarketsByLocation() {

        Product product1 = productService.findAll("Urun1").get(0);
        Assert.assertNotNull(product1);
        Product product2 = productService.findAll("Urun2").get(0);
        Assert.assertNotNull(product2);
        Point me = new Point(39.893750, 32.802022);

        List<Market> results = productService.findByLocationNear(product1, me, new Distance(0.49, Metrics.KILOMETERS));
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        for (Market market : results) {
            System.out.println(market.getName() + " " + market.getLocation().toString());
            for (Product product : market.getProducts()) {
                System.out.println("    -> " + product.getName() + " " + product.getPrice().toString());
            }
        }

        results = productService.findByLocationNear(product1, me, new Distance(0.5, Metrics.KILOMETERS));
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        for (Market market : results) {
            System.out.println(market.getName() + " " + market.getLocation().toString());
            for (Product product : market.getProducts()) {
                System.out.println("    -> " + product.getName() + " " + product.getPrice().toString());
            }
        }

        results = productService.findByLocationNear(Arrays.asList(product1, product2), me, new Distance(1.5, Metrics.KILOMETERS));
        Assert.assertNotNull(results);
        Assert.assertEquals(3, results.size());
        for (Market market : results) {
            System.out.println(market.getName() + " " + market.getLocation().toString());
            for (Product product : market.getProducts()) {
                System.out.println("    -> " + product.getName() + " " + product.getPrice().toString());
            }
        }
    }
}
