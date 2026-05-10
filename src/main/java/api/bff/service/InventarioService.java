package api.bff.service;

import api.bff.dto.inventario.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

/**
 * Esta clase es un "Servicio". En Spring, un servicio contiene la lógica de negocio.
 * Este servicio en particular se encarga de hablar con el microservicio de inventario.
 * No contiene la lógica de inventario en sí, sino que actúa como un cliente o un puente
 * para llamar a los endpoints del otro microservicio.
 */
@Service
public class InventarioService {

    // RestClient es un cliente HTTP moderno de Spring para hacer llamadas a otras APIs.
    // Lo declaramos como 'final' porque su valor no cambiará una vez que se haya inicializado.
    private final RestClient restClient;

    /**
     * Este es el constructor de la clase. Spring lo usará para "inyectar" la dependencia
     * que necesitamos.
     * @param restClient Le pedimos a Spring que nos pase el bean de RestClient que configuramos
     *                   en RestClientConfig.java. Usamos @Qualifier("inventoryRestClient") para
     *                   ser específicos y decirle a Spring que queremos el cliente configurado
     *                   para el microservicio de inventario, y no otro.
     */
    public InventarioService(@Qualifier("inventoryRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Llama al endpoint para crear un nuevo producto en el microservicio de inventario.
     * @param request El DTO con la información del producto a crear.
     * @return Un DTO con la respuesta que nos da el microservicio.
     */
    public ProductoResponse crearProducto(ProductoRequest request) {
        // Usamos el restClient para construir una petición HTTP POST.
        return restClient.post()
                .uri("/api/inventario/productos") // La ruta del endpoint en el otro microservicio.
                .body(request) // El cuerpo de la petición será el objeto 'request' convertido a JSON.
                .retrieve() // Ejecutamos la petición.
                .body(ProductoResponse.class); // Convertimos la respuesta JSON a un objeto ProductoResponse.
    }

    /**
     * Llama al endpoint para obtener la lista de todos los productos.
     * @return Una lista de objetos ProductoResponse.
     */
    public List<ProductoResponse> listarTodosLosProductos() {
        return restClient.get()
                .uri("/api/inventario/productos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoResponse>>() {});
    }

    /**
     * Llama al endpoint para registrar una entrada de stock.
     * @param request El DTO con los datos de la entrada.
     * @return Un objeto ItemInventario que representa el movimiento creado.
     */
    public ItemInventario registrarEntrada(EntradaStockRequest request) {
        return restClient.post()
                .uri("/api/inventario/stock/entrada")
                .body(request)
                .retrieve()
                .body(ItemInventario.class);
    }

    /**
     * Llama al endpoint para registrar una salida de stock.
     * @param request El DTO con los datos de la salida.
     * @return Un objeto ItemInventario que representa el movimiento creado.
     */
    public ItemInventario registrarSalida(SalidaStockRequest request) {
        return restClient.post()
                .uri("/api/inventario/stock/salida")
                .body(request)
                .retrieve()
                .body(ItemInventario.class);
    }

    /**
     * Llama al endpoint para consultar el stock de un producto específico.
     * @param sku El SKU del producto a consultar.
     * @return Un objeto IndicadorStock con la información del stock.
     */
    public IndicadorStock consultarStock(String sku) {
        // Usamos {sku} como un placeholder en la URI, y luego lo reemplazamos con el valor de la variable sku.
        return restClient.get()
                .uri("/api/inventario/stock/{sku}", sku)
                .retrieve()
                .body(IndicadorStock.class);
    }

    /**
     * Llama al endpoint para obtener el historial de movimientos de un producto.
     * @param sku El SKU del producto.
     * @return Una lista de movimientos de inventario.
     */
    public List<ItemInventario> historialMovimientos(String sku) {
        return restClient.get()
                .uri("/api/inventario/movimientos/{sku}", sku)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ItemInventario>>() {});
    }

    /**
     * Llama al endpoint para eliminar un producto.
     * @param sku El SKU del producto a eliminar.
     */
    public void eliminarProducto(String sku) {
        restClient.delete()
                .uri("/api/inventario/productos/{sku}", sku)
                .retrieve()
                .toBodilessEntity(); // Usamos toBodilessEntity() porque no esperamos un cuerpo en la respuesta (es un 204 No Content).
    }

    /**
     * Llama al endpoint para calcular las métricas de rentabilidad de un producto.
     * @param sku El SKU del producto.
     * @param request El DTO con los datos para el cálculo.
     * @return Un objeto MetricaRentabilidad con los resultados.
     */
    public MetricaRentabilidad calcularMetricas(String sku, MetricaRequest request) {
        // Aquí pasamos los parámetros como "query params" en la URL.
        // La URL final será algo como: /api/inventario/metricas/SKU123?precioVenta=199.99&costoOperativo=50.0
        return restClient.post()
                .uri("/api/inventario/metricas/{sku}?precioVenta={precioVenta}&costoOperativo={costoOperativo}",
                        sku, request.precioVenta(), request.costoOperativo())
                .retrieve()
                .body(MetricaRentabilidad.class);
    }

    /**
     * Llama al endpoint para obtener el historial de métricas de un producto.
     * @param sku El SKU del producto.
     * @return Una lista de métricas calculadas anteriormente.
     */
    public List<MetricaRentabilidad> obtenerMetricas(String sku) {
        return restClient.get()
                .uri("/api/inventario/metricas/{sku}", sku)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MetricaRentabilidad>>() {});
    }
}
