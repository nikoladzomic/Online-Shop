package com.onlineshop.Online.shop.Repository;


import com.onlineshop.Online.shop.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository< Product, Long> {
    List<Product> findByNameContaining(String name);  // Custom pretraga proizvoda preko cene i naziva
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
}
