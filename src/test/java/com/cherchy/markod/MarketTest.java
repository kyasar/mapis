package com.cherchy.markod;

import com.cherchy.markod.model.*;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.cherchy.markod.service.ProductService;
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
    private ProductRepository productRepository;

    @Autowired
    private CustomerService customerService;

    private static String campaignId;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static String marketId;

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "markets");
        mongoTemplate.remove(new Query(), "campaigns");
        mongoTemplate.remove(new Query(), "customers");


        Customer c1 = new Customer("Kadir", "Yasar", "ayasar@gmail.com", "12345");
        Customer c2 = new Customer("Kadir2", "Yasar", "yasar@gmail.com", "111");
        Customer c3 = new Customer("Kadir", "Yasar", "kadir.mail@gmail.com", "112233");

        Assert.assertNotEquals(null, customerService.create(c1).getId());
        Assert.assertNotEquals(null, customerService.create(c2));
        Assert.assertNotEquals(null, customerService.create(c3).getId());
    }

    @Test
    public void t1_insertMarkets() {

        Customer customer = customerService.findAll().get(0);
        System.out.println(customer.getId() + " " + customer.getName());

        List<Market> markets = Arrays.asList(
            new Market("Altunbilekler", "address 1", new Point(39.894177, 32.801571)),
            new Market("Migros", "address 2", new Point(39.893387, 32.79788)),
            new Market("Cagdas", "address 3", new Point(39.892827, 32.799082)),
            new Market("Kiler", "address 4", new Point(39.895, 32.797837))
        );

        for (Market m : markets) {
            Market created = marketService.create(customer.getId(), m);
            if (created != null) {
                System.out.println("Market created: " + created.getId());
            } else {
                System.err.println("Market NOT created: " + m.getId());
            }
        }

        Market m = marketService.create(new Market("Makro", "address 5", new Point(39.889732, 32.800627)));
        Assert.assertNotEquals(null, m);
        marketId = m.getId();
        System.out.println(m.getId() + " " + m.getName());
    }

    @Test
    public void t2_associateWithACustomer()
    {
        Customer c = customerService.findByEmail("yasar@gmail.com");
        Assert.assertNotEquals(null, c);
        Market marketAssociated = marketService.associate(c.getId(), marketId);
        Assert.assertNotEquals(null, marketAssociated);
        Market marketAssociated2 = marketService.associate(c.getId(), marketId);
        Assert.assertNotEquals(null, marketAssociated2);
    }

    @Test
    public void t2_getMarkets() {

        List<Market> markets = marketService.findAll();
        for (Market m : markets) {
            System.out.println(m.getId() + " " + m.getName());
            marketId = m.getId();
        }

        Assert.assertEquals(false, marketService.findOne(marketId).isActive());
        marketService.activate(marketId, true);
        Assert.assertEquals(true, marketService.findOne(marketId).isActive());
    }

    @Test
    public void t3_createCampaign() throws ParseException {

        List<Product> products = productRepository.findByNameContaining("run");
        Assert.assertEquals(3, products.size());

        Market market = marketService.findAll().get(0);
        Assert.assertNotEquals(null, market);
        marketId = market.getId();

        Campaign c1 = new Campaign("Campaign1", sdf.parse("10/01/2017"), sdf.parse("14/01/2017"));
        Campaign campaign1 = campaignService.create(market.getId(), c1);
        Assert.assertNotEquals(null, campaign1);
        Assert.assertEquals(1, marketService.getCampaigns(marketId).size());
        System.out.println("Campaign created: " + campaign1.getId());
        campaignId = campaign1.getId();

        int  i =0;
        for (Product product : products) {
            Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9 + i++, 99)), campaign1.getId()));
        }

        products.removeIf(e -> e.getName().endsWith("run2"));
        c1 = new Campaign("Campaign2", sdf.parse("12/01/2017"), sdf.parse("17/01/2017"));
        Campaign campaign2 = campaignService.create(market.getId(), c1);
        Assert.assertNotEquals(null, campaign2);
        Assert.assertEquals(2, marketService.getCampaigns(marketId).size());
        System.out.println("Campaign created: " + campaign2.getId());

        i = 10;
        for (Product product : products) {
            Assert.assertEquals(true, campaignService.addProduct(new Product(product.getId(), new Price(9 + i, 99)), campaign2.getId()));
        }
    }


    @Test
    public void t4_removeCampaign() {
        //Campaign c1 = campaignService.findAll().get(0);
        System.out.println("Removing campaign id: " + campaignId);
        Assert.assertEquals(2, marketService.getCampaigns(marketId).size());
        Assert.assertEquals(false, marketService.removeCampaign(campaignId, "wrongId"));
        Assert.assertEquals(false, marketService.removeCampaign(campaignId + "a", marketId));
        Assert.assertEquals(2, marketService.getCampaigns(marketId).size());
        Assert.assertEquals(null, marketService.getCampaigns(marketId + "1"));
        Assert.assertEquals(2, campaignService.findAll().size());
        campaignService.delete(campaignId);
        Assert.assertEquals(1, campaignService.findAll().size());
        Assert.assertEquals(1, marketService.getCampaigns(marketId).size());
    }
}
