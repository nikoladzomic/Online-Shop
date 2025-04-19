package com.onlineshop.Online.shop.Service;

import com.onlineshop.Online.shop.Model.Order;
import com.onlineshop.Online.shop.Model.Product;
import com.onlineshop.Online.shop.Repository.ProductRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private List<Product> productList;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        //given
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Ime");
        testProduct.setDescription("Opis");
        testProduct.setPrice(100);
        testProduct.setStock(10);

        productList = new ArrayList<>();
        productList.add(testProduct);

        //when
        when(productRepository.findAll()).thenReturn(productList);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {

        //when
        List<Product> result = productService.getAllProducts();

        //then
        assertEquals(1, result.size());
        verify(productRepository,times(1)).findAll();
    }

    @Test
    void getProductById_ShouldReturnProductById() {
        //when
        Product result = productService.getProductById(1L);

        //then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository,times(1)).findById(1L);

    }

    @Test
    void getProductById_WithWrongId_ShouldNotReturnProductById() {
        //given
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        //when
        Product result = productService.getProductById(100L);

        //then
        assertNull(result);
        verify(productRepository,times(1)).findById(100L);
    }

    @Test
    void saveProduct_ShouldReturnSavedProduct() {
        //when
        Product result = productService.saveProduct(testProduct);

        //then
        assertNotNull(result);
        assertEquals("Ime", result.getName());
        verify(productRepository,times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldDeleteById() {
        //when
        productService.deleteProduct(1L);

        //then
        verify(productRepository,times(1)).deleteById(1L);
    }
}