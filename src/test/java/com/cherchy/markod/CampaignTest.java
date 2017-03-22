package com.cherchy.markod;

import com.cherchy.markod.model.*;
import com.cherchy.markod.service.*;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.gen5.api.BeforeEach;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private static String campaignId;
    private static String marketId;
    private static int numCampaigns = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    public void t0_setUp()
    {
        mongoTemplate.remove(new Query(), "campaigns");
        mongoTemplate.remove(new Query(), "products");
        mongoTemplate.remove(new Query(), "categories");
        Category c1 = categoryService.create(new Category("Meyve", null));
        Assert.assertNotNull(c1);
        Assert.assertNotNull(categoryService.create(new Category("Armut", c1.getId())));
        Assert.assertNotNull(categoryService.create(new Category("Elma", c1.getId())));
        Assert.assertNotNull(categoryService.create(new Category("Muz", c1.getId())));

        Category category = categoryService.findAll("Armut").get(0);
        Assert.assertNotNull(category);

        productService.create(new Product("Deveci Armut", "91222", category.getId()));
        productService.create(new Product("Antalya Armut", "91223", category.getId()));
        productService.create(new Product("Yayla Armut", "91224", category.getId()));

        category = categoryService.findAll("Elma").get(0);
        Assert.assertNotNull(category);
        productService.create(new Product("StarKing Elma", "78001", category.getId()));
        productService.create(new Product("GreenSmith Elma", "78002", category.getId()));
        Assert.assertEquals(5, productService.findAll().size());

        System.out.println("SET UP");
    }

    @Test
    public void t1_createPublicCampaign() throws ParseException
    {
        Category category = categoryService.findAll("Armut").get(0);
        Assert.assertNotNull(category);
        List<Product> armuts = productService.findAll(category);
        Assert.assertEquals(3, armuts.size());

        Market market = marketService.findAll().get(0);
        Customer customer = customerService.findByEmail("kadir.mail@gmail.com");
        Assert.assertNotEquals(null, market);
        Assert.assertNotEquals(null, customer);

        Campaign campaign = new Campaign("Campaign1", sdf.parse("10/01/2017"), sdf.parse("14/01/2017"));

        Campaign createdCampaign = campaignService.create(market.getId(), customer.getId(), campaign);
        Assert.assertNotEquals(null, createdCampaign);

        int i=0;
        for (Product product : armuts) {
            campaign = campaignService.addProduct(
                    new Product(product.getId(), new Price(9 + i++, 99)), createdCampaign.getId());
            Assert.assertNotNull(campaign);
            Assert.assertEquals(i, campaign.getProducts().size());
        }

        market = marketService.findOne(market.getId());
        marketId = market.getId();
        campaignId = campaign.getId();
        System.out.print(marketId);
    }

    @Test
    public void t2_createPrivateCampaign() throws ParseException
    {
        Category category = categoryService.findAll("Armut").get(0);
        Assert.assertNotNull(category);
        List<Product> products = productService.findAll(category);
        Assert.assertEquals(3, products.size());

        Market market = marketService.findOne(marketId);
        Assert.assertNotEquals(null, market);

        Campaign campaign = new Campaign("Campaign2", sdf.parse("10/01/2017"), sdf.parse("14/01/2017"));
        Campaign createdCampaign = campaignService.create(market.getId(), campaign);
        Assert.assertNotEquals(null, createdCampaign);

        int  i = 10;
        for (Product product : products) {
            Assert.assertNotNull(campaignService.addProduct(
                    new Product(product.getId(), new Price(9 + i++, 99)), campaign.getId()));
            // Same product insertion will make update
            Assert.assertNotNull(campaignService.addProduct(
                    new Product(product.getId(), new Price(9 + i, 99)), campaign.getId()));
        }
        Assert.assertEquals(3, campaignService.findOne(campaign.getId()).getProducts().size());

        Assert.assertEquals(2, campaignService.findAll().size());
        Assert.assertEquals(0, campaignService.findAll(market.getId() + " ").size());
        Assert.assertEquals(2, campaignService.findAll(market.getId()).size());
    }

    @Test
    public void t3_getCampaign()
    {
        System.out.println("Campaign created: " + campaignId);
        Campaign c = campaignService.findOne(campaignId);
        System.out.println("Products in campaign:");
        for (Product p : c.getProducts()) {
            System.out.println(p.getId() + " " + p.getPrice().toString());
            Assert.assertEquals(false, c.isActive());
        }
    }

    @Test
    public void t4_updateCampaign() throws ParseException
    {
        Category category = categoryService.findAll("Armut").get(0);
        Assert.assertNotNull(category);
        List<Product> products = productService.findAll(category);
        Assert.assertEquals(3, products.size());
        Product product = products.get(0);

        Campaign c = campaignService.findOne(campaignId);

        Assert.assertEquals(3, c.getProducts().size());
        Assert.assertNull(campaignService.removeProduct(product.getId() + "x", campaignId + "x"));
        Assert.assertNull(campaignService.removeProduct(product.getId() + "x", campaignId));
        Assert.assertNull(campaignService.removeProduct(product.getId(), campaignId + "x"));
        c = campaignService.removeProduct(product.getId(), campaignId);
        Assert.assertNotNull(c);

        c = campaignService.findOne(campaignId);
        Assert.assertEquals(2, c.getProducts().size());
        Assert.assertNotNull(campaignService.addProduct(new Product(product.getId(), new Price(9, 99)), campaignId));
        Assert.assertNotNull(campaignService.addProduct(new Product(product.getId(), new Price(19, 99)), campaignId));
        c = campaignService.findOne(campaignId);
        Assert.assertEquals(3, c.getProducts().size());

        int initialPoints = customerService.findOne(c.getCustomerId()).getPoints();
        Assert.assertEquals(initialPoints, customerService.findOne(c.getCustomerId()).getPoints());
        Assert.assertEquals(false, c.isActive());
        c.setActive(true);
        c = campaignService.update(c);
        Assert.assertEquals(true, c.isActive());
        //Assert.assertEquals(5 + initialPoints, customerService.findOne(c.getCustomerId()).getPoints());
    }

}
