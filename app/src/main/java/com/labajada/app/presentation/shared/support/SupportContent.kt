package com.labajada.app.presentation.shared.support

data class FaqEntry(val question: String, val answer: String)

object SupportContent {
    val buyerFaq = listOf(
        FaqEntry(
            "¿Cómo hago un pedido?",
            "Busca un platillo o explora los huariques en tu radar, elige lo que quieras y confirma tu pedido desde el carrito."
        ),
        FaqEntry(
            "¿Puedo cancelar mi pedido?",
            "Sí, siempre que el restaurante aún no lo haya marcado como 'en preparación'. Contáctalo directamente para coordinar."
        ),
        FaqEntry(
            "¿Cómo sé si un restaurante está abierto?",
            "En el radar, los restaurantes cerrados aparecen atenuados con la etiqueta 'Cerrado'."
        ),
        FaqEntry(
            "¿Cómo desactivo mi cuenta?",
            "Ve a Configuración y Soporte → Cuenta y Seguridad → Desactivar cuenta."
        )
    )

    val sellerFaq = listOf(
        FaqEntry(
            "¿Por qué es importante subir mis documentos?",
            "Verificar tu local con fotos y tu permiso municipal genera más confianza en los compradores y te da una insignia visible junto al nombre de tu negocio. No es obligatorio para vender, pero los restaurantes verificados suelen recibir más pedidos."
        ),
        FaqEntry(
            "¿Cómo envío mis documentos correctamente?",
            "1) Toma la foto de tu local con buena luz, de frente y sin recortes.\n2) Si subes tu permiso municipal, asegúrate de que el texto se lea completo y sin borrosidad.\n3) Evita fotos oscuras, movidas o tomadas de una pantalla.\n4) Una vez subidas, toca 'Enviar documentos' para mandarlas a revisión — sin este paso, quedan guardadas pero no se revisan."
        ),
        FaqEntry(
            "¿Cómo activo o cierro mi negocio?",
            "Desde el Dashboard, toca 'Cambiar' junto al estado de tu restaurante para abrir o cerrar en cualquier momento."
        ),
        FaqEntry(
            "¿Cómo agrego un nuevo platillo?",
            "En la pestaña 'Mi Menú', toca el botón '+ Nuevo Platillo'."
        ),
        FaqEntry(
            "¿Qué pasa si desactivo mi cuenta?",
            "Tu negocio deja de ser visible para los compradores. Tu historial de pedidos se conserva."
        ),
        FaqEntry(
            "¿Cómo configuro mi radio de delivery?",
            "Ve a tu perfil, edita tu ubicación en el mapa y ajusta el radio con el deslizador."
        )
    )

    const val supportEmail = "soporte@labajada.app"
}