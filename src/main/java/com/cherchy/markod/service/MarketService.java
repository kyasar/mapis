package com.cherchy.markod.service;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;

import java.util.List;

public interface MarketService {

    List<Market> findAll();

    Market findOne(String id);

    boolean exists(String id);

    Market create(Market market);

    Market create(String cid, Market market);

    Market update(Market p);

    void delete(String id);

    Market activate(String id, boolean state);

    boolean addCampaign(Campaign campaign, String mid);

    boolean removeCampaign(String campaignId, String mid);

    List<Customer> getFollowers(String id);

    List<Campaign> getCampaigns(String id);
}
