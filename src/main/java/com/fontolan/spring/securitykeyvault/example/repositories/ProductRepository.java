package com.fontolan.spring.securitykeyvault.example.repositories;

import com.fontolan.spring.securitykeyvault.example.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}