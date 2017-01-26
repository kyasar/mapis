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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "campaigns");
    }

    @Test
    public void t2_createCampaign() throws ParseException {


    }

    @Test
    public void t3_getCampaign() {
        System.out.println("Campaign created: " + campaignId);
        Campaign c = campaignService.findOne(campaignId);
        System.out.println("Products in campaign:");
        for (Product p : c.getProducts()) {
            System.out.println(p.getId() + " " + p.getPrice().toString());
            Assert.assertEquals(false, c.isActive());
        }
    }


    @Test
    public void t3_updateCampaign() throws ParseException {
        Product product = productRepository.findByNameContaining("run3").get(0);
        Campaign c = campaignService.findOne(campaignId);

        Assert.assertEquals(2, c.getProducts().size());
        Assert.assertEquals(false, campaignService.removeProduct(product.getId() + "x", campaignId + "x"));
        Assert.assertEquals(false, campaignService.removeProduct(product.getId() + "x", campaignId));
        Assert.assertEquals(false, campaignService.removeProduct(product.getId(), campaignId + "x"));
        Assert.assertEquals(true, campaignService.removeProduct(product.getId(), campaignId));

        c = campaignService.findOne(campaignId);
        Assert.assertEquals(1, c.getProducts().size());
        Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9, 99)), campaignId));
        Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(19, 99)), campaignId));
        c = campaignService.findOne(campaignId);
        Assert.assertEquals(2, c.getProducts().size());

        Assert.assertEquals(false, c.isActive());
        c = campaignService.activate(c.getId(), true);
        Assert.assertEquals(true, c.isActive());
        c = campaignService.activate(c.getId(), false);
        Assert.assertEquals(false, c.isActive());
    }

    @Test
    public void t4_testDate() throws ParseException {
        Assert.assertEquals(2, campaignService.findAll(sdf.parse("08/01/2017")).size());
        Assert.assertEquals(1, campaignService.findAll(sdf.parse("11/01/2017")).size());
        Assert.assertEquals(0, campaignService.findAll(sdf.parse("13/01/2017")).size());
    }
}
