package api.bff.dto.inventario;

/**
 * Este DTO se utiliza para recibir los datos cuando se registra una entrada de stock
 * para un producto existente.
 */
public record EntradaStockRequest(
        // El SKU del producto al que le vamos a añadir stock.
        String sku,

        // De dónde viene este nuevo stock. Puede ser el nombre de un proveedor,
        // una devolución, etc. Ejemplo: "Proveedor Tech Limitada".
        String origen,

        // La cantidad de unidades del producto que están entrando al inventario.
        Integer cantidad
) {}
