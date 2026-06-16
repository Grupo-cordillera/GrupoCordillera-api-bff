package api.bff.dto.inventario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Este es un Data Transfer Object (DTO) que usamos para recibir la información
 * necesaria para crear un nuevo producto.
 * Un "record" en Java es una forma moderna y corta de crear una clase que solo guarda datos.
 * Es perfecto para los DTOs.
 */
public record ProductoRequest(
        // @NotBlank es una regla de validación.
        // Se asegura de que el texto 'nombre' no llegue vacío o solo con espacios en blanco.
        // Si la regla no se cumple, se devuelve el mensaje de error que definimos.
        @NotBlank(message = "El nombre del producto no puede estar vacío")
        String nombre,

        // Este campo es para la descripción del producto. Es opcional, por eso no tiene
        // ninguna anotación de validación como @NotBlank o @NotNull.
        String descripcion,

        // @NotNull se asegura de que el valor no sea nulo (es decir, que siempre nos den un número).
        @NotNull(message = "Debes definir un umbral mínimo de stock")
        // @Min se asegura de que el número sea como mínimo 0. No podemos tener un stock mínimo negativo.
        @Min(value = 0, message = "El umbral mínimo no puede ser negativo")
        Integer umbralMinimo
) {}
