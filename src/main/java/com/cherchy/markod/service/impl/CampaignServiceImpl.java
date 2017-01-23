package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.CampaignRepository;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    @Override
    public Campaign findOne(String cid) {
        return campaignRepository.findOne(cid);
    }

    @Override
    public Campaign create(Campaign campaign) {
        if (campaign.getId() != null) {
            return null;
        }
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign update(Campaign campaign) {
        Campaign present = campaignRepository.findOne(campaign.getId());
        if (present == null) {
            // Cannot update
            return null;
        }

        // Save product list
        campaign.setProducts(present.getProducts());
        return campaignRepository.save(campaign);
    }

    @Override
    public void delete(String id) {
        campaignRepository.delete(id);
    }

    @Override
    public Campaign activate(String id) {
        Campaign campaign = campaignRepository.findOne(id);
        if (campaign == null)
            return null;
        // toggle activation
        campaign.setActive(!campaign.isActive());
        return campaignRepository.save(campaign);
    }

    @Override
    public boolean addProduct(Product product, String cid) {
        Product p = productRepository.findOne(product.getId());
        if (p == null)
            return false;

        Campaign campaign = campaignRepository.findOne(cid);
        if (campaign == null)
            return false;

        if (campaign.getProducts().contains(product)) {
            Product productExisted = campaign.getProducts().get(campaign.getProducts().indexOf(product));
            productExisted.setPrice(product.getPrice());
        }
        else {
            campaign.getProducts().add(new Product(p.getId(), product.getPrice()));
        }

        campaignRepository.save(campaign);
        /*
        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(cid)),
                new Update().addToSet("products", new Product(product.getId(), product.getPrice())),
                Campaign.class);
        if (res.getN() == 0)
            return false;
        */

        return true;
    }

    @Override
    public boolean removeProduct(String pid, String cid) {
        Product product = productRepository.findOne(pid);
        if (product == null)
            return false;

        Campaign campaign = campaignRepository.findOne(cid);
        if (campaign == null)
            return false;

        System.out.println("Removing product: " + product.getId());
        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(cid)),
                new Update().pull("products", new BasicDBObject("_id", product.getId())),
                Campaign.class);

        if (res.getN() == 0)
            return false;
        return true;
    }

    @Override
    public List<Campaign> findAll(Date date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("startDate").gte(date));
        return mongoTemplate.find(query, Campaign.class);
    }
}
