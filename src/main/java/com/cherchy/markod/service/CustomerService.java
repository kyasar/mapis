package com.cherchy.markod.service;

import com.cherchy.markod.model.Customer;
import com.cherchy.markod.model.Market;

import javax.management.relation.Role;
import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findOne(String id);

    Customer create(Customer p);

    Customer update(Customer p);

    void delete(String id);

}
