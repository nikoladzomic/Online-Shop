package com.onlineshop.Online.shop.Controller;

import com.onlineshop.Online.shop.Model.Product;
import com.onlineshop.Online.shop.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private List<Product> productList;


    @BeforeEach
    void setUp() {
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
        when(productService.getAllProducts()).thenReturn(productList);
        when(productService.getProductById(1L)).thenReturn(testProduct);
        when(productService.saveProduct(testProduct)).thenReturn(testProduct);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        //when
        List<Product> result = productController.getAllProducts();


        //then
        assertEquals(1, result.size());
        assertEquals("Ime", result.get(0).getName());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProductsById() {
        //when
        Product result = productController.getProductById(1L);

        //then
        assertEquals(1L, result.getId());
        assertEquals("Ime", result.getName());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        //given
        Product newProduct = new Product();
        newProduct.setName("Ime2");
        newProduct.setDescription("Opis2");
        newProduct.setPrice(200);
        newProduct.setStock(20);

        when(productService.saveProduct(newProduct)).thenReturn(newProduct);

        //when
        Product result = productController.createProduct(newProduct);

        //then
        assertNotNull(result);
        assertEquals("Ime2", result.getName());
        assertEquals("Opis2", result.getDescription());
        verify(productService, times(1)).saveProduct(newProduct);
    }

    @Test
    void updateProduct_ShouldUpdateProductById() {
        //given
        Long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setName("Ime2");
        updatedProduct.setId(productId);

        when(productService.saveProduct(updatedProduct)).thenReturn(updatedProduct);

        //when
        Product result = productController.updateProduct(productId, updatedProduct);

        //then
        assertEquals("Ime2", result.getName());
        verify(productService).saveProduct(updatedProduct);
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        //when
        productController.deleteProduct(1L);

        //then
        verify(productService).deleteProduct(1L);
    }
}