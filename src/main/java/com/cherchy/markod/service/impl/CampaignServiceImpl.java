package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.repository.CampaignRepository;
import com.cherchy.markod.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Override
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    @Override
    public Campaign findOne(String id) {
        return campaignRepository.findOne(id);
    }

    @Override
    public Campaign create(Campaign p) {
        if (p.getId() != null) {
            return null;
        }
        return campaignRepository.save(p);
    }

    @Override
    public Campaign update(Campaign p) {
        Campaign present = campaignRepository.findOne(p.getId());
        if (present == null) {
            // Cannot update
            return null;
        }

        return campaignRepository.save(p);
    }

    @Override
    public void delete(String id) {
        campaignRepository.delete(id);
    }
}
