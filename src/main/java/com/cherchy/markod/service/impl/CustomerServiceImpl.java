package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.repository.CustomerRepository;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MarketService marketService;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findOne(String id) {
        return customerRepository.findOne(id);
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public boolean exists(String id) {
        List<Customer> results =  mongoTemplate.find(new Query(where("_id").is(id)).limit(1), Customer.class);
        if (results.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public Customer create(Customer c) {
        if (c.getEmail() == null) {
            return null;
        }
        // Exists already?
        if (customerRepository.findByEmail(c.getEmail()) != null)
            return null;
        return customerRepository.save(c);
    }

    @Override
    public Customer update(Customer c) {
        Customer present = customerRepository.findOne(c.getId());
        if (present == null) {
            // Cannot update
            return null;
        }

        return customerRepository.save(c);
    }

    @Override
    public void delete(String id) {
        customerRepository.delete(id);
    }

    @Override
    public boolean followMarket(String cid, String mid)
    {
        if (!marketService.exists(mid))
            return false;

        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(cid)),
                new Update().addToSet("followingMarkets", mid),
                Customer.class);
        if (res.getN() == 0)
            return false;
        return true;
    }

    @Override
    public boolean unfollowMarket(String cid, String mid)
    {
        if (!marketService.exists(mid))
            return false;

        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(cid)),
                new Update().pull("followingMarkets", mid),
                Customer.class);
        if (res.getN() == 0)
            return false;
        return true;
    }
}
