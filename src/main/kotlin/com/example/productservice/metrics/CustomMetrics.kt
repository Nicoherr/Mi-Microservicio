package com.example.productservice.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class CustomMetrics(meterRegistry: MeterRegistry) {

    val productCreatedCounter: Counter = Counter.builder("products_created_total")
        .description("Número total de productos creados en el catálogo")
        .tag("component", "ProductService")
        .register(meterRegistry)

    val productNotFoundCounter: Counter = Counter.builder("product_not_found_errors_total")
        .description("Número total de errores por producto no encontrado")
        .tag("component", "ProductService")
        .register(meterRegistry)
}
