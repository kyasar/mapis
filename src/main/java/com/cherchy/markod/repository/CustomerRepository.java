package com.cherchy.markod.repository;

import com.cherchy.markod.model.Campaign;
import com.cherchy.markod.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
}
