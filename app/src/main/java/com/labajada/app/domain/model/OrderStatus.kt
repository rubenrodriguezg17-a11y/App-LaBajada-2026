package com.labajada.app.domain.model

enum class OrderStatus {
    ENVIADO,        // El comprador confirmó el pedido. Notifica al restaurante.
    PREPARACION,    // El restaurante aceptó y está cocinando.
    EN_CAMINO,      // Solo si es delivery: el repartidor va en ruta.
    LISTO_RECOJO,   // Solo si es retiro en local: el pedido espera en el mostrador.
    ENTREGADO       // Fin del ciclo.
}