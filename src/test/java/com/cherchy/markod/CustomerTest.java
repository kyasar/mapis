package com.cherchy.markod;

import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
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
    private MarketRepository marketRepository;

    
    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "customers");
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
        GeoResults<Market> markets =  marketRepository.findByLocationNear(
                new Point(39.893927, 32.802670), new Distance(1000, Metrics.KILOMETERS));
        Assert.assertTrue(markets.getContent().size() >= 2);
        Market m1 = markets.getContent().get(0).getContent();
        Market m2 = markets.getContent().get(1).getContent();
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
}
