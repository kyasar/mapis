package com.cherchy.markod.repository;

import com.cherchy.markod.model.Campaign;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends MongoRepository<Campaign, String> {

    List<Campaign> findByMarketId(String marketId);
}
