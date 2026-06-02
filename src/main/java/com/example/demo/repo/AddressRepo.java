package com.example.demo.repo;

import com.example.demo.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}