package com.cherchy.markod.service;

import com.cherchy.markod.model.Market;

import java.util.List;

public interface MarketService {

    List<Market> findAll();

    Market findOne(String id);

    Market create(Market p);

    Market update(Market p);

    void delete(String id);
}
