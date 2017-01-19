package com.cherchy.markod;

import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
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

    @Test
    public void t0_setUp() {
        mongoTemplate.remove(new Query(), "customers");
    }

    @Test
    public void t1_insertMarkets() {

        List<Customer> customers = Arrays.asList(
            new Customer("Kadir", "Yasar", "ayasar@gmail.com", "12345"),
            new Customer("Kadir2", "Yasar", "kyasar@gmail.com", "111"),
            new Customer("Kadir", "Yasar", "kadir.mail@gmail.com", "112233")
        );

        for (Customer c : customers) {
            Customer created = customerService.create(c);
            if (created != null) {
                System.out.println("Customer created: " + created.getId());
            } else {
                System.err.println("Customer NOT created: " + c.getId());
            }
        }
    }
}
