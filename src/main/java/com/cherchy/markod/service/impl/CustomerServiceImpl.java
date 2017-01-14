package com.cherchy.markod.service.impl;

import com.cherchy.markod.model.Customer;
import com.cherchy.markod.repository.CustomerRepository;
import com.cherchy.markod.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findOne(String id) {
        return customerRepository.findOne(id);
    }

    @Override
    public Customer create(Customer c) {
        if (c.getId() != null) {
            return null;
        }
        return customerRepository.save(c);
    }

    @Override
    public Customer update(Customer c) {
        Customer present = customerRepository.findOne(c.getId());
        if (present == null) {
            // Cannot update
            return null;
        }

        return customerRepository.save(c);
    }

    @Override
    public void delete(String id) {
        customerRepository.delete(id);
    }
}
