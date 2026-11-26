package com.example.productservice.exception

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handleProductNotFound debe retornar status 404 NOT FOUND`() {
        val ex = ProductNotFoundException(5L)

        val response = handler.handleProductNotFound(ex)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("Producto con ID 5 no fue encontrado", response.body?.message)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `handleProductNotFound con NoSuchElementException debe retornar status 404 NOT FOUND`() {
        val ex = NoSuchElementException("Elemento ausente")

        val response = handler.handleProductNotFound(ex)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("Elemento ausente", response.body?.message)
    }

    @Test
    fun `handleProductNotFound con mensaje nulo debe retornar mensaje por defecto`() {
        val ex = Exception(null as String?)

        val response = handler.handleProductNotFound(ex)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("Recurso no encontrado", response.body?.message)
    }

    @Test
    fun `handleValidationErrors debe retornar status 400 BAD REQUEST con detalles de errores`() {
        val bindingResult = mockk<BindingResult>()
        val fieldError = FieldError("createProductRequest", "name", "El nombre no puede estar vacío")
        every { bindingResult.fieldErrors } returns listOf(fieldError)

        val ex = MethodArgumentNotValidException(mockk(relaxed = true), bindingResult)

        val response = handler.handleValidationErrors(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(400, response.body?.status)
        assertEquals("name: El nombre no puede estar vacío", response.body?.message)
    }

    @Test
    fun `handleGenericException debe retornar status 500 INTERNAL SERVER ERROR`() {
        val ex = RuntimeException("Error inesperado en BD")

        val response = handler.handleGenericException(ex)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(500, response.body?.status)
        assertEquals("Ha ocurrido un error interno en el servidor", response.body?.message)
    }
}
