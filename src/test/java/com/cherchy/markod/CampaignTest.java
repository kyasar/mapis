package com.cherchy.markod;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Price;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.service.CampaignService;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
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

    private static String campaignId;

    @Test
    public void t0_setUp() {
        for (String col : mongoTemplate.getCollectionNames()) {
            if (!col.startsWith("system")) {
                System.out.println("Remove all collection: " + col);
                mongoTemplate.remove(new Query(), col);
            }
        }
    }

    @Test
    public void t1_insertProduct() {

        final String uri = "http://localhost:8080" + "/product";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity;
        ResponseEntity<String> result;

        JSONObject body = new JSONObject();
        body.put("name", "Urun1");
        body.put("barcode", "11111");
        headers.setContentType(MediaType.APPLICATION_JSON);
        entity = new HttpEntity<String>(body.toString(), headers);

        result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        System.out.println("Product created id: " + new JSONObject(result.getBody()).get("id"));
        assertEquals(201, result.getStatusCode().value());

        body.put("name", "Urun2");
        body.put("barcode", "22222");
        headers.setContentType(MediaType.APPLICATION_JSON);
        entity = new HttpEntity<String>(body.toString(), headers);

        result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        System.out.println("Product created id: " + new JSONObject(result.getBody()).get("id"));
        assertEquals(201, result.getStatusCode().value());

        body.put("name", "Urun3");
        body.put("barcode", "33333");
        headers.setContentType(MediaType.APPLICATION_JSON);
        entity = new HttpEntity<String>(body.toString(), headers);

        result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        System.out.println("Product created id: " + new JSONObject(result.getBody()).get("id"));
        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    public void t2_createCampaign() {

        final String uri = "http://localhost:8080" + "/product";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        assertEquals(200, result.getStatusCode().value());
        System.out.println(result.getBody());

        List<Product> products = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(result.getBody());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonobject = jsonArray.getJSONObject(i);
            String id = jsonobject.getString("id");
            System.out.println("Adding product: " + id);
            products.add(new Product(id, new Price(10, 4)));
        }

        Campaign c1 = new Campaign("Campaign in a Market 1", products);
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
            System.out.println(p.getId() + " " + p.getName());
        }
    }
}
