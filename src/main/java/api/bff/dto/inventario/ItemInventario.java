package api.bff.dto.inventario;

import java.time.LocalDateTime;

/**
 * Este DTO representa un movimiento específico en el inventario, ya sea una entrada
 * o una salida de stock. Cada vez que se añade o se quita stock, se crea un registro
 * de este tipo.
 */
public record ItemInventario(
        // Un identificador único para este movimiento de inventario.
        Long id,

        // La información del producto que se movió.
        ProductoResponse producto,

        // El origen o destino del movimiento.
        // Si fue una entrada, dirá de dónde vino (ej. "Proveedor X").
        // Si fue una salida, dirá a dónde fue (ej. "Venta a cliente Y").
        String origen,

        // La cantidad de unidades que se movieron. Será un número positivo para entradas
        // y podría ser negativo o positivo (dependiendo del diseño) para salidas.
        Integer cantidad,

        // La fecha y hora exactas en que se registró este movimiento.
        LocalDateTime ultimaActualizacion
) {}
