package com.example.productservice.service

import com.example.productservice.dto.CreateProductRequest
import com.example.productservice.dto.ProductResponse
import com.example.productservice.exception.ProductNotFoundException
import com.example.productservice.metrics.CustomMetrics
import com.example.productservice.model.Product
import com.example.productservice.repository.ProductRepository
import org.slf.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val customMetrics: CustomMetrics
) {
    private val log = LoggerFactory.getLogger(ProductService::class.java)

    @Transactional(readOnly = true)
    fun getAllProducts(): List<ProductResponse> {
        log.info("Obteniendo todos los productos del catálogo")
        return productRepository.findAll().map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getProductById(id: Long): ProductResponse {
        log.info("Buscando producto con id: {}", id)
        val product = productRepository.findById(id).orElseThrow {
            customMetrics.productNotFoundCounter.increment()
            log.warn("Producto no encontrado con id: {}", id)
            ProductNotFoundException(id)
        }
        return product.toResponse()
    }

    @Transactional
    fun createProduct(request: CreateProductRequest): ProductResponse {
        log.info("Creando nuevo producto: {}", request.name)
        val product = Product(
            name = request.name,
            description = request.description,
            price = request.price,
            stock = request.stock
        )
        val savedProduct = productRepository.save(product)
        customMetrics.productCreatedCounter.increment()
        log.info("Producto creado exitosamente con id: {}", savedProduct.id)
        return savedProduct.toResponse()
    }

    @Transactional
    fun updateProduct(id: Long, request: CreateProductRequest): ProductResponse {
        log.info("Actualizando producto con id: {}", id)
        val existingProduct = productRepository.findById(id).orElseThrow {
            customMetrics.productNotFoundCounter.increment()
            log.warn("Error al actualizar: Producto no encontrado con id: {}", id)
            ProductNotFoundException(id)
        }
        val updatedProduct = existingProduct.copy(
            name = request.name,
            description = request.description,
            price = request.price,
            stock = request.stock
        )
        val saved = productRepository.save(updatedProduct)
        log.info("Producto actualizado exitosamente con id: {}", saved.id)
        return saved.toResponse()
    }

    @Transactional
    fun deleteProduct(id: Long) {
        log.info("Eliminando producto con id: {}", id)
        if (!productRepository.existsById(id)) {
            customMetrics.productNotFoundCounter.increment()
            log.warn("Error al eliminar: Producto no encontrado con id: {}", id)
            throw ProductNotFoundException(id)
        }
        productRepository.deleteById(id)
        log.info("Producto eliminado exitosamente con id: {}", id)
    }

    private fun Product.toResponse() = ProductResponse(
        id = requireNotNull(id) { "ID no puede ser nulo al mapear a respuesta" },
        name = name,
        description = description,
        price = price,
        stock = stock
    )
}
