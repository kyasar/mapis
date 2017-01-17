package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Product;
import com.cherchy.markod.repository.CampaignRepository;
import com.cherchy.markod.repository.ProductRepository;
import com.cherchy.markod.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ProductRepository productRepository;

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
    public boolean addProduct(Product product, String cid) {
        Product p = productRepository.findOne(product.getId());
        if (p == null)
            return false;
        Campaign campaign = campaignRepository.findOne(cid);
        if (campaign == null)
            return false;
        campaign.getProducts().add(new Product(product.getId(), product.getPrice()));

        if (campaignRepository.save(campaign) == null)
            return false;
        else
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
        campaign.getProducts().removeIf(p -> p.getId().equals(product.getId()));

        if (campaignRepository.save(campaign) == null)
            return false;
        else
            return true;
    }
}
