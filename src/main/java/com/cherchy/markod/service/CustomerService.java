package com.cherchy.markod.service;

import com.cherchy.markod.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findOne(String id);

    Customer findByEmail(String email);

    boolean exists(String id);

    Customer create(Customer p);

    Customer update(Customer p);

    void delete(String id);

    boolean followMarket(String cid, String mid);

    boolean unfollowMarket(String cid, String mid);

    Customer addProductToWishList(String cid, String pid);

    Customer removeProductFromWishList(String cid, String pid);

    Customer addCategoryToWishList(String customerId, String categoryId);

    Customer removeCategoryFromWishList(String customerId, String categoryId);

    Customer addPoints(String cid, int points);

    Customer deletePoints(String cid, int points);

}
