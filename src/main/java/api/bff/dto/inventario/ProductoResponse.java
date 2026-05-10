package api.bff.dto.inventario;

/**
 * Este es otro DTO, pero este lo usamos para ENVIAR información de un producto
 * desde nuestra API hacia el cliente (por ejemplo, una aplicación web o móvil).
 * Fíjate que no contiene toda la información del producto, solo la que queremos
 * que el cliente vea. Por ejemplo, no solemos enviar IDs internos de la base de datos.
 */
public record ProductoResponse(
        // El SKU (Stock Keeping Unit) es un código único que identifica al producto.
        // Es como el DNI de un producto en el inventario.
        String sku,

        // El nombre del producto.
        String nombre,

        // Una descripción detallada del producto.
        String descripcion,

        // Un texto que indica el estado del stock actual del producto.
        // Por ejemplo: "EN_STOCK", "STOCK_BAJO", "SIN_STOCK".
        String estadoStock
) {}
