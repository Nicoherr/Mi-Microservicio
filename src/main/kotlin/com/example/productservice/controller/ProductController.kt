package com.example.productservice.controller

import com.example.productservice.dto.CreateProductRequest
import com.example.productservice.dto.ProductResponse
import com.example.productservice.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        return ResponseEntity.ok(productService.getAllProducts())
    }

    @GetMapping("/search")
    fun searchProductsByName(@RequestParam name: String): ResponseEntity<List<ProductResponse>> {
        return ResponseEntity.ok(productService.searchProductsByName(name))
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        return ResponseEntity.ok(productService.getProductById(id))
    }

    @PostMapping
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
        val created = productService.createProduct(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateProductRequest
    ): ResponseEntity<ProductResponse> {
        return ResponseEntity.ok(productService.updateProduct(id, request))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}
