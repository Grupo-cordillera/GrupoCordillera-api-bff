package api.bff.dto.inventario;

/**
 * Este DTO se usa para recibir los datos necesarios para calcular las métricas
 * de rentabilidad de un producto.
 */
public record MetricaRequest(
        // El precio al que se vende el producto.
        Double precioVenta,

        // Los costos asociados a la operación, como almacenamiento, marketing, etc.
        Double costoOperativo
) {}
