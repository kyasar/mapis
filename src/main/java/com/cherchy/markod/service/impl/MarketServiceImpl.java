package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.repository.CustomerRepository;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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
    private CampaignService campaignService;

    @Override
    public List<Market> findAll() {
        return marketRepository.findAll();
    }

    @Override
    public Market findOne(String id) {
        return marketRepository.findOne(id);
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
    public boolean addCampaign(String campaignId, String mid)
    {
        if (!marketRepository.exists(mid))
            return false;

        Campaign campaign = campaignService.findOne(campaignId);
        if (campaign == null)
            return false;

        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(mid)),
                new Update().addToSet("campaigns", campaign),
                Market.class);

        if (res.getN() == 0)
            return false;
        return true;
    }

    @Override
    public boolean removeCampaign(String campaignId, String mid) {
        if (!marketRepository.exists(mid))
            return false;

        Campaign campaign = campaignService.findOne(campaignId);
        if (campaign == null)
            return false;

        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(mid)),
                new Update().pull("campaigns", campaign),
                Market.class);

        if (res.getN() == 0)
            return false;
        return true;
    }

    @Override
    public List<Customer> getFollowers(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("followingMarkets").in(id));
        return mongoTemplate.find(query, Customer.class);
    }

    @Override
    public List<Campaign> getCampaigns(String id) {
        Market market = marketRepository.findOne(id);
        if (market != null) {
            return market.getCampaigns();
        }
        return null;
    }
}
