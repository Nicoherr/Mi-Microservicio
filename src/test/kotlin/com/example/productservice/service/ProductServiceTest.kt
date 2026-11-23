package com.example.productservice.service

import com.example.productservice.dto.CreateProductRequest
import com.example.productservice.exception.ProductNotFoundException
import com.example.productservice.metrics.CustomMetrics
import com.example.productservice.model.Product
import com.example.productservice.repository.ProductRepository
import io.micrometer.core.instrument.Counter
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockKExtension::class)
class ProductServiceTest {

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var customMetrics: CustomMetrics

    @MockK
    private lateinit var productCreatedCounter: Counter

    @MockK
    private lateinit var productNotFoundCounter: Counter

    @InjectMockKs
    private lateinit var productService: ProductService

    private val sampleProduct = Product(
        id = 1L,
        name = "Laptop Gaming",
        description = "Laptop para desarrollo y gaming",
        price = BigDecimal("1500.00"),
        stock = 10
    )

    private val createRequest = CreateProductRequest(
        name = "Laptop Gaming",
        description = "Laptop para desarrollo y gaming",
        price = BigDecimal("1500.00"),
        stock = 10
    )

    @Test
    fun `getAllProducts debe retornar la lista de productos`() {
        every { productRepository.findAll() } returns listOf(sampleProduct)

        val result = productService.getAllProducts()

        assertEquals(1, result.size)
        assertEquals("Laptop Gaming", result[0].name)
        verify(exactly = 1) { productRepository.findAll() }
    }

    @Test
    fun `getProductById cuando el producto existe debe retornar el DTO`() {
        every { productRepository.findById(1L) } returns Optional.of(sampleProduct)

        val result = productService.getProductById(1L)

        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Laptop Gaming", result.name)
        verify(exactly = 1) { productRepository.findById(1L) }
    }

    @Test
    fun `getProductById cuando el producto no existe debe lanzar ProductNotFoundException e incrementar metrica`() {
        every { productRepository.findById(99L) } returns Optional.empty()
        every { customMetrics.productNotFoundCounter } returns productNotFoundCounter
        every { productNotFoundCounter.increment() } just runs

        assertThrows<ProductNotFoundException> {
            productService.getProductById(99L)
        }

        verify(exactly = 1) { productNotFoundCounter.increment() }
    }

    @Test
    fun `createProduct debe guardar el producto e incrementar la metrica`() {
        every { productRepository.save(any()) } returns sampleProduct
        every { customMetrics.productCreatedCounter } returns productCreatedCounter
        every { productCreatedCounter.increment() } just runs

        val result = productService.createProduct(createRequest)

        assertEquals(1L, result.id)
        assertEquals("Laptop Gaming", result.name)
        verify(exactly = 1) { productRepository.save(any()) }
        verify(exactly = 1) { productCreatedCounter.increment() }
    }

    @Test
    fun `updateProduct cuando el producto existe debe retornar producto actualizado`() {
        val updatedProduct = sampleProduct.copy(name = "Laptop Pro")
        every { productRepository.findById(1L) } returns Optional.of(sampleProduct)
        every { productRepository.save(any()) } returns updatedProduct

        val result = productService.updateProduct(1L, createRequest.copy(name = "Laptop Pro"))

        assertEquals("Laptop Pro", result.name)
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `updateProduct cuando el producto no existe debe lanzar excepcion e incrementar metrica`() {
        every { productRepository.findById(99L) } returns Optional.empty()
        every { customMetrics.productNotFoundCounter } returns productNotFoundCounter
        every { productNotFoundCounter.increment() } just runs

        assertThrows<ProductNotFoundException> {
            productService.updateProduct(99L, createRequest)
        }

        verify(exactly = 1) { productNotFoundCounter.increment() }
    }

    @Test
    fun `deleteProduct cuando el producto existe debe eliminar exitosamente`() {
        every { productRepository.existsById(1L) } returns true
        every { productRepository.deleteById(1L) } just runs

        productService.deleteProduct(1L)

        verify(exactly = 1) { productRepository.deleteById(1L) }
    }

    @Test
    fun `deleteProduct cuando el producto no existe debe lanzar excepcion e incrementar metrica`() {
        every { productRepository.existsById(99L) } returns false
        every { customMetrics.productNotFoundCounter } returns productNotFoundCounter
        every { productNotFoundCounter.increment() } just runs

        assertThrows<ProductNotFoundException> {
            productService.deleteProduct(99L)
        }

        verify(exactly = 1) { productNotFoundCounter.increment() }
    }
}
