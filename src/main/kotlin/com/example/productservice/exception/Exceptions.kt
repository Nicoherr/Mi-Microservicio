package com.example.productservice.exception

class ProductNotFoundException(id: Long) : RuntimeException("Producto con ID $id no fue encontrado")
