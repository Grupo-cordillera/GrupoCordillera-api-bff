package api.bff.controller;

import api.bff.dto.inventario.*;
import api.bff.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Esta clase es un "Controlador". En Spring, un controlador es la puerta de entrada
 * a nuestra API desde el exterior. Define los endpoints (las URLs) que los clientes
 * pueden llamar.
 * Este controlador expone las funcionalidades del microservicio de inventario a través del BFF.
 */
@RestController // Anotación que le dice a Spring que esta clase manejará peticiones HTTP.
@RequestMapping("/api/bff/inventario") // Define la ruta base para todos los endpoints de este controlador.
@RequiredArgsConstructor // Anotación de Lombok que crea un constructor con los campos 'final'.
public class InventarioController {

    // Inyectamos nuestro InventarioService. El controlador no hace el trabajo pesado,
    // solo recibe la petición, se la pasa al servicio y luego devuelve la respuesta.
    private final InventarioService inventarioService;

    /**
     * Endpoint para crear un nuevo producto.
     * @param request El cuerpo de la petición HTTP se convertirá en un objeto ProductoRequest.
     *                @Valid le dice a Spring que aplique las reglas de validación que definimos en el DTO.
     * @return Un objeto ResponseEntity que contiene la respuesta del servicio y un código de estado HTTP (ej. 200 OK).
     */
    @PostMapping("/productos")
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = inventarioService.crearProducto(request);
        return ResponseEntity.ok(response); // .ok() es un atajo para devolver un estado 200 OK.
    }

    /**
     * Endpoint para listar todos los productos.
     * @return Una lista de productos.
     */
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoResponse>> listarTodosLosProductos() {
        List<ProductoResponse> productos = inventarioService.listarTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para registrar una entrada de stock.
     * @param request Datos de la entrada de stock.
     * @return El item de inventario creado.
     */
    @PostMapping("/stock/entrada")
    public ResponseEntity<ItemInventario> registrarEntrada(@RequestBody EntradaStockRequest request) {
        ItemInventario item = inventarioService.registrarEntrada(request);
        return ResponseEntity.ok(item);
    }

    /**
     * Endpoint para registrar una salida de stock.
     * @param request Datos de la salida de stock.
     * @return El item de inventario creado.
     */
    @PostMapping("/stock/salida")
    public ResponseEntity<ItemInventario> registrarSalida(@RequestBody SalidaStockRequest request) {
        ItemInventario item = inventarioService.registrarSalida(request);
        return ResponseEntity.ok(item);
    }

    /**
     * Endpoint para consultar el stock de un producto.
     * @param sku El SKU se obtiene de la URL (ej. /api/bff/inventario/stock/SKU123).
     * @return La información del stock del producto.
     */
    @GetMapping("/stock/{sku}")
    public ResponseEntity<IndicadorStock> consultarStock(@PathVariable String sku) {
        IndicadorStock stock = inventarioService.consultarStock(sku);
        return ResponseEntity.ok(stock);
    }

    /**
     * Endpoint para ver el historial de movimientos de un producto.
     * @param sku El SKU del producto.
     * @return Una lista con el historial de movimientos.
     */
    @GetMapping("/movimientos/{sku}")
    public ResponseEntity<List<ItemInventario>> historialMovimientos(@PathVariable String sku) {
        List<ItemInventario> historial = inventarioService.historialMovimientos(sku);
        return ResponseEntity.ok(historial);
    }

    /**
     * Endpoint para eliminar un producto.
     * @param sku El SKU del producto a eliminar.
     * @return Una respuesta sin cuerpo con el estado 204 No Content, que indica que la operación fue exitosa.
     */
    @DeleteMapping("/productos/{sku}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String sku) {
        inventarioService.eliminarProducto(sku);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para calcular las métricas de rentabilidad de un producto.
     * @param sku El SKU del producto.
     * @param request Los datos para el cálculo (precio de venta y costo).
     * @return La métrica calculada.
     */
    @PostMapping("/metricas/{sku}")
    public ResponseEntity<MetricaRentabilidad> calcularMetricas(
            @PathVariable String sku,
            @RequestBody MetricaRequest request) {
        MetricaRentabilidad metrica = inventarioService.calcularMetricas(sku, request);
        return ResponseEntity.ok(metrica);
    }

    /**
     * Endpoint para obtener el historial de métricas de un producto.
     * @param sku El SKU del producto.
     * @return Una lista con las métricas calculadas previamente.
     */
    @GetMapping("/metricas/{sku}")
    public ResponseEntity<List<MetricaRentabilidad>> obtenerMetricas(@PathVariable String sku) {
        List<MetricaRentabilidad> metricas = inventarioService.obtenerMetricas(sku);
        return ResponseEntity.ok(metricas);
    }
}
