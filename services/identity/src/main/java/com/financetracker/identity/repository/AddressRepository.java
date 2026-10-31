package com.financetracker.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financetracker.identity.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
}
