package com.cherchy.markod;

import ch.qos.logback.core.net.SyslogOutputStream;
import ch.qos.logback.core.pattern.color.MagentaCompositeConverter;
import com.cherchy.markod.model.*;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
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
    private MarketService marketService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductRepository productRepository;

    private static String campaignId;
    private static String marketId;
    private static int numCampaigns = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "campaigns");
    }

    @Test
    public void t1_createPublicCampaign() throws ParseException {

        List<Product> products = productRepository.findByNameContaining("run");
        Assert.assertEquals(3, products.size());

        Market market = marketService.findAll().get(0);
        Customer customer = customerService.findByEmail("kadir.mail@gmail.com");
        Assert.assertNotEquals(null, market);
        Assert.assertNotEquals(null, customer);

        Campaign campaign = new Campaign("Campaign1", sdf.parse("10/01/2017"), sdf.parse("14/01/2017"));

        Campaign createdCampaign = campaignService.create(market.getId(), customer.getId(), campaign);
        Assert.assertNotEquals(null, createdCampaign);
        campaignId = createdCampaign.getId();

        int  i =0;
        for (Product product : products) {
            Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9 + i++, 99)), createdCampaign.getId()));
        }

        market = marketService.findOne(market.getId());
        marketId = market.getId();
        System.out.print(marketId);
    }

    @Test
    public void t2_createPrivateCampaign() throws ParseException {

        List<Product> products = productRepository.findByNameContaining("run");
        Assert.assertEquals(3, products.size());

        Market market = marketService.findOne(marketId);
        Assert.assertNotEquals(null, market);

        Campaign campaign = new Campaign("Campaign2", sdf.parse("10/01/2017"), sdf.parse("14/01/2017"));

        Campaign createdCampaign = campaignService.create(market.getId(), campaign);
        Assert.assertNotEquals(null, createdCampaign);

        int  i = 10;
        for (Product product : products) {
            Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9 + i++, 99)), campaignId));
        }

        Assert.assertEquals(2, campaignService.findAll().size());
        Assert.assertEquals(0, campaignService.findAll(market.getId() + " ").size());
        Assert.assertEquals(2, campaignService.findAll(market.getId()).size());
    }

    @Test
    public void t3_getCampaign() {
        System.out.println("Campaign created: " + campaignId);
        Campaign c = campaignService.findOne(campaignId);
        System.out.println("Products in campaign:");
        for (Product p : c.getProducts()) {
            System.out.println(p.getId() + " " + p.getPrice().toString());
            //Assert.assertEquals(false, c.isActive());
        }
    }

    @Test
    public void t4_updateCampaign() throws ParseException {
        Product product = productRepository.findByNameContaining("run3").get(0);
        Campaign c = campaignService.findOne(campaignId);

        Assert.assertEquals(3, c.getProducts().size());
        Assert.assertEquals(false, campaignService.removeProduct(product.getId() + "x", campaignId + "x"));
        Assert.assertEquals(false, campaignService.removeProduct(product.getId() + "x", campaignId));
        Assert.assertEquals(false, campaignService.removeProduct(product.getId(), campaignId + "x"));
        Assert.assertEquals(true, campaignService.removeProduct(product.getId(), campaignId));

        c = campaignService.findOne(campaignId);
        Assert.assertEquals(2, c.getProducts().size());
        Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9, 99)), campaignId));
        Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(19, 99)), campaignId));
        c = campaignService.findOne(campaignId);
        Assert.assertEquals(3, c.getProducts().size());

        int initialPoints = customerService.findOne(c.getCustomerId()).getPoints();
        Assert.assertEquals(initialPoints, customerService.findOne(c.getCustomerId()).getPoints());
        Assert.assertEquals(false, c.isActive());
        c = campaignService.activate(c.getId(), true);
        Assert.assertEquals(true, c.isActive());
        Assert.assertEquals(5 + initialPoints, customerService.findOne(c.getCustomerId()).getPoints());
    }

}
