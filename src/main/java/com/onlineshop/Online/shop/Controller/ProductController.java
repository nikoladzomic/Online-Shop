package com.onlineshop.Online.shop.Controller;


import com.onlineshop.Online.shop.Model.Product;
import com.onlineshop.Online.shop.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5174")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @Operation(summary = "Create a new product", description = "Creates a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @Operation(summary = "Update a product", description = "Updates an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,@Valid @RequestBody Product product) {
        product.setId(id);
        return productService.saveProduct(product);
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
