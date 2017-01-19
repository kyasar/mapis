package com.cherchy.markod;

import com.cherchy.markod.model.*;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

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
    private CustomerService customerService;

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "markets");
    }

    @Test
    public void t1_insertMarkets() {

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
    }

    static String marketId;

    @Test
    public void t2_getMarkets() {

        List<Market> markets = marketService.findAll();
        for (Market m : markets) {
            System.out.println(m.getId() + " " + m.getName());
            marketId = m.getId();
        }
    }

    @Test
    public void t3_updateMarket() {
        Customer customer = customerService.findAll().get(0);
        System.out.println("Customer id: " + customer.getId() + " going to follow market id: " + marketId);

        Assert.assertEquals(true, marketService.addFollower(customer.getId(), marketId));

        Assert.assertEquals(customer.getId(), marketService.findOne(marketId).getFollowers().get(0));
    }
}
