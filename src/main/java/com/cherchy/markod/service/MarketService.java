package com.cherchy.markod.service;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;

import java.util.List;

public interface MarketService {

    List<Market> findAll();

    Market findOne(String id);

    Market create(Market p);

    Market update(Market p);

    void delete(String id);

    boolean addCampaign(Campaign campaign, String mid);

    boolean removeCampaign(Campaign campaign, String mid);

    boolean addFollower(Customer customer, String mid);

    boolean removeFollower(Customer customer, String mid);

}
