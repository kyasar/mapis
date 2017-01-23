package com.cherchy.markod.service;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Product;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface CampaignService {

    List<Campaign> findAll();

    Campaign findOne(String id);

    Campaign create(Campaign campaign);

    Campaign update(Campaign campaign);

    void delete(String id);

    Campaign activate(String id);

    boolean addProduct(Product product, String cid);

    boolean removeProduct(String pid, String cid);

    List<Campaign> findAll(Date date);
}
