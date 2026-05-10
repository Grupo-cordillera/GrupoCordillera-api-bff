package api.bff.dto.inventario;

/**
 * Este DTO se usa para devolver la información consolidada del stock de un producto.
 * Nos da una vista rápida de cuántas unidades hay y cómo está ese nivel de stock
 * en comparación con el mínimo que definimos.
 */
public record IndicadorStock(
        // Un identificador único para este registro de indicador.
        Long id,

        // Contiene la información básica del producto al que pertenece este indicador.
        // Usamos el DTO ProductoResponse para mantener la consistencia.
        ProductoResponse producto,

        // El número total de unidades de este producto que tenemos en el inventario.
        Integer stockTotalConsolidado,

        // El número mínimo de unidades que deberíamos tener. Si el stock baja de este número,
        // podríamos necesitar comprar más.
        Integer umbralMinimo,

        // Un texto que resume el estado del stock. Por ejemplo: "SOBRE_UMBRAL", "BAJO_UMBRAL".
        String estado
) {}
