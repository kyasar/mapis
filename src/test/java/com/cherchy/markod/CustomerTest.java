package com.cherchy.markod;

import com.cherchy.markod.model.Category;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.service.CategoryService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.cherchy.markod.service.ProductService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MarketService marketService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "customers");
        mongoTemplate.remove(new Query(), "products");
        mongoTemplate.remove(new Query(), "categories");

        Category c1 = categoryService.create(new Category("Meyve", null));
        Assert.assertNotNull(c1);
        Assert.assertNotNull(categoryService.create(new Category("Armut", c1.getId())));
        Assert.assertNotNull(categoryService.create(new Category("Elma", c1.getId())));
        Assert.assertNotNull(categoryService.create(new Category("Muz", c1.getId())));

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
    public void t1_insertUsers() {

        Customer c1 = new Customer("Kadir", "Yasar", "ayasar@gmail.com", "12345");
        Customer c2 = new Customer("Kadir2", "Yasar", "yasar@gmail.com", "111");
        Customer c3 = new Customer("Kadir", "Yasar", "kadir.mail@gmail.com", "112233");

        Assert.assertNotEquals(null, customerService.create(c1).getId());
        Assert.assertNotEquals(null, customerService.create(c2));
        Assert.assertNotEquals(null, customerService.create(c3).getId());
    }

    static String cid;
    static String mid1, mid2;

    @Test
    public void t2_followMarket()
    {
        List<Market> markets =  marketService.findByLocationNear(
                new Point(39.893927, 32.802670), new Distance(1000, Metrics.KILOMETERS));
        Assert.assertTrue(markets.size() >= 2);
        Market m1 = markets.get(0);
        Market m2 = markets.get(1);
        mid1 = m1.getId();
        mid2 = m2.getId();

        System.out.println(mid1 + "  " + mid2);

        Assert.assertNotEquals(null, m1);
        Assert.assertNotEquals(null, m2);

        Assert.assertEquals(false, customerService.followMarket("wrongCID", "wrongMID"));
        Assert.assertEquals(false, customerService.followMarket(cid, "wrongMID"));
        Assert.assertEquals(false, customerService.followMarket("wrongCID", mid1));
        for (Customer c : customerService.findAll()) {
            Assert.assertEquals(true, customerService.followMarket(c.getId(), mid1));
            cid = c.getId();
        }
        Assert.assertEquals(true, customerService.followMarket(cid, mid2));

        Assert.assertEquals(3, marketService.getFollowers(mid1).size());
        Assert.assertEquals(1, marketService.getFollowers(mid2).size());
    }

    @Test
    public void t3_unfollowMarket()
    {
        Assert.assertEquals(false, customerService.unfollowMarket("wrongCID", "wrongMID"));
        Assert.assertEquals(false, customerService.unfollowMarket(cid, "wrongMID"));
        Assert.assertEquals(false, customerService.unfollowMarket("wrongCID", mid1));
        Assert.assertEquals(true, customerService.unfollowMarket(cid, mid1));

        Assert.assertEquals(2, marketService.getFollowers(mid1).size());
        Assert.assertEquals(1, marketService.getFollowers(mid2).size());
    }

    @Test
    public void t4_followProducts()
    {
        Customer customer = customerService.findByEmail("yasar@gmail.com");
        Assert.assertNotNull(customer);
        List<Product> products = productService.findAll("Deveci Armut");
        Assert.assertNotEquals(products.size(), 0);

        Assert.assertEquals(customer.getFollowProducts().size(), 0);
        customer = customerService.addProductToWishList(customer.getId(), products.get(0).getId());
        Assert.assertEquals(customer.getFollowProducts().size(), 1);
        customer = customerService.addProductToWishList(customer.getId(), products.get(0).getId());
        Assert.assertEquals(customer.getFollowProducts().size(), 1);
        customer = customerService.removeProductFromWishList(customer.getId(), products.get(0).getId());
        Assert.assertEquals(customer.getFollowProducts().size(), 0);
    }
}
