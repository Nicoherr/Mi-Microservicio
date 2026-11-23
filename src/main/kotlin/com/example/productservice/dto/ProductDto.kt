package com.example.productservice.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank(message = "El nombre del producto no puede estar vacío")
    val name: String,

    @field:NotBlank(message = "La descripción no puede estar vacía")
    val description: String,

    @field:NotNull(message = "El precio es obligatorio")
    @field:Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    val price: BigDecimal,

    @field:NotNull(message = "El stock es obligatorio")
    @field:Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    val stock: Int
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stock: Int
)
