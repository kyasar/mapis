package com.cherchy.markod;

import com.cherchy.markod.model.Product;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void t1_insertProduct() {

		final String uri = "http://localhost:8080" + "/product";

		RestTemplate restTemplate = new RestTemplate();
		JSONObject body = new JSONObject();
		body.put("name", "Urun1");
		body.put("barcode", "11111");
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		JSONObject jsonObject = new JSONObject(result.getBody());
		System.out.println(jsonObject.get("id").toString());
		assertEquals(201, result.getStatusCode().value());

		result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		System.out.println(result.getBody());
		assertEquals(200, result.getStatusCode().value());

		body.put("name", "Urun XX");
		headers.setContentType(MediaType.APPLICATION_JSON);
		entity = new HttpEntity<String>(body.toString(), headers);

		result = restTemplate.exchange(uri + "/" + jsonObject.get("id").toString(), HttpMethod.PUT, entity, String.class);
		jsonObject = new JSONObject(result.getBody());
		System.out.println(jsonObject.get("id").toString());
		assertEquals(200, result.getStatusCode().value());

		result = restTemplate.exchange(uri + "/" + jsonObject.get("id").toString(), HttpMethod.GET, entity, String.class);
		System.out.println(result.getBody());
		assertEquals(200, result.getStatusCode().value());

		result = restTemplate.exchange(uri + "/" + jsonObject.get("id").toString(), HttpMethod.DELETE, entity, String.class);
		assertEquals(200, result.getStatusCode().value());
	}
}
