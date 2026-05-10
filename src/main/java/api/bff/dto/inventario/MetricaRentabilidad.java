package api.bff.dto.inventario;

import java.time.LocalDate;

/**
 * Este DTO se utiliza para devolver las métricas de rentabilidad calculadas para un producto.
 * Nos ayuda a entender qué tan rentable es un producto.
 */
public record MetricaRentabilidad(
        // Un identificador único para este cálculo de métrica.
        Long id,

        // La información del producto para el cual se calculó la métrica.
        ProductoResponse producto,

        // El margen de ganancia obtenido por el producto.
        Double margenGanancia,

        // Los costos asociados a la venta o mantenimiento del producto.
        Double costoOperativo,

        // El Retorno de la Inversión (Return On Investment). Es un indicador que mide
        // el beneficio obtenido en relación con la inversión realizada.
        Double roi,

        // La fecha en que se realizó este cálculo.
        LocalDate fechaCalculo
) {}
