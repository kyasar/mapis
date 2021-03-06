package com.cherchy.markod.service;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Product;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface CampaignService {

    List<Campaign> findAll();

    List<Campaign> findAll(String mid);

    List<Campaign> findAll(Date date);

    Campaign findOne(String id);

    Campaign create(String mid, Campaign campaign);

    Campaign create(String mid, String cid, Campaign campaign);

    Campaign update(Campaign campaign);

    Campaign delete(String id);

    Campaign addProduct(Product product, String cid);

    Campaign removeProduct(String pid, String cid);

}
