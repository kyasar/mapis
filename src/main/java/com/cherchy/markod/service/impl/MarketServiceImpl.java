package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.repository.CustomerRepository;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.service.MarketService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class MarketServiceImpl implements MarketService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Market> findAll() {
        return marketRepository.findAll();
    }

    @Override
    public Market findOne(String id) {
        return marketRepository.findOne(id);
    }

    @Override
    public Market create(Market m) {
        if (m.getId() != null) {
            return null;
        }
        return marketRepository.save(m);
    }

    @Override
    public Market update(Market m) {
        Market present = marketRepository.findOne(m.getId());
        if (present == null) {
            // Cannot update
            return null;
        }

        m.setCampaigns(present.getCampaigns());
        m.setFollowers(present.getFollowers());

        return marketRepository.save(m);
    }

    @Override
    public void delete(String id) {
        marketRepository.delete(id);
    }

    @Override
    public boolean addCampaign(Campaign campaign, String mid) {
        Market m = marketRepository.findOne(mid);
        if (m == null) {
            return false;
        }

        m.getCampaigns().add(campaign);
        marketRepository.save(m);
        return true;
    }

    @Override
    public boolean removeCampaign(Campaign campaign, String mid) {
        Market m = marketRepository.findOne(mid);
        if (m == null) {
            return false;
        }

        m.getCampaigns().removeIf(c -> c.getId().equals(campaign.getId()));
        marketRepository.save(m);
        return true;
    }

    @Override
    public boolean addFollower(String fid, String mid) {
        Market market = marketRepository.findOne(mid);
        if (market == null) {
            return false;
        }

        Customer customer = customerRepository.findOne(fid);
        if (customer == null) {
            return false;
        }

        /*
        market.addToFollowers(fid);
        if (marketRepository.save(market) == null)
            return false;*/

        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(mid)),
                new Update().addToSet("followers", fid),
                Market.class);

        customer.followMarket(market);
        if (customerRepository.save(customer) == null)
            return false;

        return true;
    }

    @Override
    public boolean removeFollower(String fid, String mid) {
        Market market = marketRepository.findOne(mid);
        if (market == null) {
            return false;
        }

        Customer customer = customerRepository.findOne(fid);
        if (customer == null) {
            return false;
        }

        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(mid)),
                new Update().pull("followers", fid),
                Market.class);

        customer.unfollowMarket(mid);
        if (customerRepository.save(customer) == null)
            return false;

        return true;
    }
}
