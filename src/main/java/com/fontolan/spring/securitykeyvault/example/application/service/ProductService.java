package com.fontolan.spring.securitykeyvault.example.application.service;

import com.fontolan.spring.securitykeyvault.example.domain.model.Product;
import com.fontolan.spring.securitykeyvault.example.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}

