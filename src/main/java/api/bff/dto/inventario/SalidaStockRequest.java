package api.bff.dto.inventario;

/**
 * Este DTO se usa para recibir los datos cuando se registra una salida de stock
 * de un producto.
 */
public record SalidaStockRequest(
        // El SKU del producto del que vamos a restar stock.
        String sku,

        // Hacia dónde va este stock. Puede ser el número de una boleta, una orden de despacho, etc.
        // Ejemplo: "Boleta #12345".
        String destino,

        // La cantidad de unidades del producto que están saliendo del inventario.
        Integer cantidad
) {}
