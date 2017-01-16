package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.repository.CustomerRepository;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketServiceImpl implements MarketService {

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
    public boolean addFollower(Customer customer, String mid) {
        Market m = marketRepository.findOne(mid);
        if (m == null) {
            return false;
        }

        m.getFollowers().add(customer);
        marketRepository.save(m);
        return true;
    }

    @Override
    public boolean removeFollower(Customer customer, String mid) {
        Market m = marketRepository.findOne(mid);
        if (m == null) {
            return false;
        }

        m.getFollowers().remove(customer);
        marketRepository.save(m);
        return true;
    }

}
