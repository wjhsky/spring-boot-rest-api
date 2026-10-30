package com.wjhsky.demo.repository;

import com.wjhsky.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 商品 JPA Repository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
