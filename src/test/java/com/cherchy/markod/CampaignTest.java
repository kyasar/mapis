package com.cherchy.markod;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Price;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import org.bson.types.ObjectId;
import org.json.JSONArray;
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
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CampaignTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private ProductRepository productRepository;

    private static String campaignId;

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "campaigns");
    }

    @Test
    public void t2_createCampaign() {

        List<Product> products = productRepository.findByNameContaining("run");
        List<Product> campaignProducts = new ArrayList<>();

        int  i =0;
        for (Product p : products) {
            campaignProducts.add(new Product(p.getId(), new Price(10 + (i++), 5)));
        }

        Campaign c1 = new Campaign("Campaign1", campaignProducts);
        campaignService.create(c1);
        campaignId = c1.getId();
        System.out.println("Campaign created: " + campaignId + " " + campaignProducts.size());

        campaignProducts.removeIf(e -> e.getPrice().getLeft() == 10);
        c1 = new Campaign("Campaign2", campaignProducts);
        campaignService.create(c1);
        campaignId = c1.getId();
        System.out.println("Campaign created: " + campaignId);
    }

    @Test
    public void t3_getCampaign() {
        System.out.println("Campaign created: " + campaignId);
        Campaign c = campaignService.findOne(campaignId);
        System.out.println("Products in campaign:");
        for (Product p : c.getProducts()) {
            System.out.println(p.getId() + " " + p.getPrice().toString());
        }
    }

    /*
    @Test
    public void t3_updateCampaign() {
        Product product = productRepository.findByNameContaining("run2").get(0);
        Assert.assertEquals(true, campaignService.removeProduct(product.getId(), campaignId));
        Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9, 99)), campaignId));
        System.out.println("Products in campaign:");
        Campaign c = campaignService.findOne(campaignId);
        for (Product p : c.getProducts()) {
            System.out.println(p.getId() + " " + p.getPrice().toString());
        }
    }
    */
}
