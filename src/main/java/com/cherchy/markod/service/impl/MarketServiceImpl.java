package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.CustomerRepository;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class MarketServiceImpl implements MarketService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CustomerService customerService;

    @Override
    public List<Market> findAll() {
        return marketRepository.findAll();
    }

    @Override
    public Market findOne(String id) {
        return marketRepository.findOne(id);
    }

    @Override
    public List<Market> findByLocationNear(Point location, Distance distance) {
        Query query = new Query();
        Circle circle = new Circle(location, distance);
        query.addCriteria(Criteria.where("location").withinSphere(circle));
        query.with(new Sort(Sort.Direction.DESC, "location"));
        return mongoTemplate.find(query, Market.class);
    }

    @Override
    public boolean exists(String id) {
        List<Market> results =  mongoTemplate.find(new Query(where("_id").is(id)).limit(1), Market.class);
        if (results.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public Market create(Market market) {
        if (market.getId() != null) {
            return null;
        }
        return marketRepository.save(market);
    }

    @Override
    public Market create(String cid, Market market) {
        Customer customer = customerService.findOne(cid);
        if (customer == null)
            return null;
        if (market.getId() != null) {
            return null;
        }

        Market created = marketRepository.save(market);
        if (created == null)
            return null;

        if (customer.getMarkets() == null) {
            customer.setMarkets(new HashSet<Market>());
        }
        customer.getMarkets().add(market);

        customerService.update(customer);

        return created;
    }

    @Override
    public Market associate(String cid, String mid) {
        Customer customer = customerService.findOne(cid);
        if (customer == null)
            return null;
        Market market = marketRepository.findOne(mid);
        if (market == null)
            return null;
        if (customer.getMarkets() == null) {
            customer.setMarkets(new HashSet<Market>());
        }

        customer.getMarkets().add(market);
        customerService.update(customer);
        return market;
    }

    @Override
    public Market update(Market m) {
        Market present = marketRepository.findOne(m.getId());
        if (present == null) {
            // Cannot update
            return null;
        }

        return marketRepository.save(m);
    }

    @Override
    public void delete(String id) {
        marketRepository.delete(id);
    }

    @Override
    public Market activate(String id, boolean state) {
        Market market = marketRepository.findOne(id);
        if (market == null)
            return null;
        market.setActive(state);
        return marketRepository.save(market);
    }

    @Override
    public List<Customer> getFollowers(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("followingMarkets").in(id));
        return mongoTemplate.find(query, Customer.class);
    }

    @Override
    public Product addProductToShelf(String mid, Product p) {
        Market market = marketRepository.findOne(mid);
        if (market == null)
            return null;

        if (market.getProducts().contains(p)) {
            market.getProducts().remove(p);
        }
        market.getProducts().add(p);
        if (marketRepository.save(market) == null)
            return null;
        return p;
    }

    @Override
    public Product removeProductToShelf(String mid, Product p) {
        Market market = marketRepository.findOne(mid);
        if (market == null)
            return null;

        if (market.getProducts().contains(p)) {
            market.getProducts().remove(p);
        }
        if (marketRepository.save(market) == null)
            return null;
        return p;
    }
}
