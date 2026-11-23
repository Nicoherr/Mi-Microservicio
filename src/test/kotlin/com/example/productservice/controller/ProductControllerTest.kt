package com.example.productservice.controller

import com.example.productservice.dto.CreateProductRequest
import com.example.productservice.dto.ProductResponse
import com.example.productservice.service.ProductService
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
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class ProductControllerTest {

    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var productController: ProductController

    private val sampleResponse = ProductResponse(
        id = 1L,
        name = "Teclado Mecánico",
        description = "Teclado RGB switch blue",
        price = BigDecimal("89.99"),
        stock = 15
    )

    private val createRequest = CreateProductRequest(
        name = "Teclado Mecánico",
        description = "Teclado RGB switch blue",
        price = BigDecimal("89.99"),
        stock = 15
    )

    @Test
    fun `getAllProducts debe retornar lista de productos y status 200 OK`() {
        every { productService.getAllProducts() } returns listOf(sampleResponse)

        val response = productController.getAllProducts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(1, response.body?.size)
        verify(exactly = 1) { productService.getAllProducts() }
    }

    @Test
    fun `searchProductsByName debe retornar productos filtrados y status 200 OK`() {
        every { productService.searchProductsByName("Teclado") } returns listOf(sampleResponse)

        val response = productController.searchProductsByName("Teclado")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, response.body?.size)
        assertEquals("Teclado Mecánico", response.body?.get(0)?.name)
        verify(exactly = 1) { productService.searchProductsByName("Teclado") }
    }

    @Test
    fun `getProductById debe retornar el producto y status 200 OK`() {
        every { productService.getProductById(1L) } returns sampleResponse

        val response = productController.getProductById(1L)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1L, response.body?.id)
        verify(exactly = 1) { productService.getProductById(1L) }
    }

    @Test
    fun `createProduct debe crear producto y retornar status 201 Created`() {
        every { productService.createProduct(createRequest) } returns sampleResponse

        val response = productController.createProduct(createRequest)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(1L, response.body?.id)
        verify(exactly = 1) { productService.createProduct(createRequest) }
    }

    @Test
    fun `updateProduct debe actualizar producto y retornar status 200 OK`() {
        every { productService.updateProduct(1L, createRequest) } returns sampleResponse

        val response = productController.updateProduct(1L, createRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1L, response.body?.id)
        verify(exactly = 1) { productService.updateProduct(1L, createRequest) }
    }

    @Test
    fun `deleteProduct debe eliminar producto y retornar status 204 No Content`() {
        every { productService.deleteProduct(1L) } just runs

        val response = productController.deleteProduct(1L)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(exactly = 1) { productService.deleteProduct(1L) }
    }
}
