package com.cherchy.markod.service;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;
import com.cherchy.markod.model.Product;

import java.util.List;

public interface MarketService {

    List<Market> findAll();

    Market findOne(String id);

    boolean exists(String id);

    /*
    Creates a standalone Market without association of a Customer
     */
    Market create(Market market);

    /*
    Creates a Market associated with a Customer
     */
    Market create(String cid, Market market);

    /*
    Associates Market with a Customer
     */
    Market associate(String cid, String mid);

    Market update(Market market);

    void delete(String id);

    Market activate(String id, boolean state);

    List<Customer> getFollowers(String id);

    Product addProductToShelf(String mid, Product p);

    Product removeProductToShelf(String mid, Product p);

}
