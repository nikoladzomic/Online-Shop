package com.onlineshop.Online.shop.Service;


import com.onlineshop.Online.shop.Model.Product;
import com.onlineshop.Online.shop.Repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        logger.info("Fetching product with id : {}", id);
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        logger.info("Saving product: {}", product);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }

}
