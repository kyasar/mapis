package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.*;
import com.cherchy.markod.model.type.CampaignType;
import com.cherchy.markod.repository.CampaignRepository;
import com.cherchy.markod.repository.MarketRepository;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import com.cherchy.markod.service.CustomerService;
import com.cherchy.markod.service.MarketService;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@PropertySource("classpath:markod.properties")
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MarketService marketService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${points.public.campaign}")
    private int POINTS_PUBLIC_CAMPAIGN;

    @Override
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    @Override
    public List<Campaign> findAll(String mid) {
        return campaignRepository.findByMarketId(mid);
    }

    @Override
    public Campaign findOne(String cid) {
        return campaignRepository.findOne(cid);
    }

    @Override
    public Campaign create(String mid, Campaign campaign)
    {
        if (!marketService.exists(mid))
            return null;

        // This campaign is officially created by Market
        campaign.setType(CampaignType.PRIVATE);

        // Set relationship to market where the campaign resides
        campaign.setMarketId(mid);
        campaign.setActive(true);   // already approved

        Campaign created = campaignRepository.save(campaign);
        if (created == null)
            return null;
        return created;
    }

    @Override
    public Campaign create(String mid, String cid, Campaign campaign)
    {
        if (!marketService.exists(mid))
            return null;
        if (!customerService.exists(cid))
            return null;

        // This campaign is publicly published/announced by a user
        campaign.setType(CampaignType.PUBLIC);

        // Set relationship to market where the campaign resides
        campaign.setMarketId(mid);
        campaign.setCustomerId(cid);

        Campaign created = campaignRepository.save(campaign);
        if (created == null)
            return null;
        return created;
    }

    @Override
    public Campaign update(Campaign campaign)
    {
        if (campaignRepository.findOne(campaign.getId()) == null) {
            return null;
        }

        // Update selected fields only
        Update update = new Update();
        update.set("title", campaign.getTitle());
        update.set("active", campaign.isActive());
        update.set("startDate", campaign.getStartDate());
        update.set("endDate", campaign.getEndDate());

        return mongoTemplate.findAndModify(new Query(Criteria.where("_id").is(campaign.getId())), update,
                        FindAndModifyOptions.options().returnNew(true), Campaign.class);
    }

    @Override
    public Campaign delete(String id)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findAndRemove(query, Campaign.class);
    }

    @Override
    public Campaign addProduct(Product product, String cid)
    {
        Product p = productRepository.findOne(product.getId());
        if (p == null)
            return null;

        Campaign campaign = campaignRepository.findOne(cid);
        if (campaign == null)
            return null;

        // Not allow duplicate products
        if (campaign.getProducts().contains(product))
        {
            Product productExisted = campaign.getProducts().get(campaign.getProducts().indexOf(product));
            productExisted.setPrice(product.getPrice());
        }
        else {
            campaign.getProducts().add(new Product(p.getId(), product.getPrice()));
        }
        /*
        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(cid)),
                new Update().addToSet("products", new Product(product.getId(), product.getPrice())),
                Campaign.class);
        if (res.getN() == 0)
            return false;
        */
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign removeProduct(String pid, String cid) {
        Product product = productRepository.findOne(pid);
        if (product == null)
            return null;

        Campaign campaign = campaignRepository.findOne(cid);
        if (campaign == null)
            return null;

        System.out.println("Removing product: " + product.getId());
        WriteResult res = mongoTemplate.updateFirst(
                new Query(where("_id").is(cid)),
                new Update().pull("products", new BasicDBObject("_id", product.getId())),
                Campaign.class);

        return campaignRepository.findOne(cid);
    }

    @Override
    public List<Campaign> findAll(Date date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("startDate").gte(date));
        return mongoTemplate.find(query, Campaign.class);
    }
}
