package api.bff.service;

import api.bff.dto.inventario.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    private MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @InjectMocks
    private InventarioService inventarioService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        RestClient restClient = RestClient.builder(restTemplate).baseUrl("http://localhost:5002").build();
        inventarioService = new InventarioService(restClient);
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void crearProducto_ShouldReturnProductoResponse_WhenCalled() throws JsonProcessingException {
        var productoRequest = new ProductoRequest("Test Product", "A test description", 10);
        var productoResponse = new ProductoResponse("SKU123", "Test Product", "A test description", 0, "SIN_STOCK");

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/productos"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(productoResponse)));

        var result = inventarioService.crearProducto(productoRequest);

        assertNotNull(result);
        assertEquals("SKU123", result.sku());
    }

    @Test
    void listarTodosLosProductos_ShouldReturnListOfProductoResponse_WhenCalled() throws JsonProcessingException {
        var productoResponse = new ProductoResponse("SKU123", "Test Product", "A test description", 50, "EN_STOCK");
        List<ProductoResponse> productoResponseList = Collections.singletonList(productoResponse);

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/productos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(productoResponseList)));

        var result = inventarioService.listarTodosLosProductos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SKU123", result.get(0).sku());
    }

    @Test
    void registrarEntrada_ShouldReturnItemInventario_WhenCalled() throws JsonProcessingException {
        var entradaRequest = new EntradaStockRequest("SKU123", "Proveedor A", 100);
        var productoResponse = new ProductoResponse("SKU123", "Test Product", "Desc", 100, "EN_STOCK");
        var itemInventario = new ItemInventario(1L, productoResponse, "Proveedor A", 100, LocalDateTime.now());

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/stock/entrada"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(itemInventario)));

        var result = inventarioService.registrarEntrada(entradaRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("SKU123", result.producto().sku());
    }

    @Test
    void registrarSalida_ShouldReturnItemInventario_WhenCalled() throws JsonProcessingException {
        var salidaRequest = new SalidaStockRequest("SKU123", "Venta 1", 10);
        var productoResponse = new ProductoResponse("SKU123", "Test Product", "Desc", 90, "EN_STOCK");
        var itemInventario = new ItemInventario(2L, productoResponse, "Venta 1", 10, LocalDateTime.now());

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/stock/salida"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(itemInventario)));

        var result = inventarioService.registrarSalida(salidaRequest);

        assertNotNull(result);
        assertEquals(2L, result.id());
    }

    @Test
    void consultarStock_ShouldReturnIndicadorStock_WhenCalled() throws JsonProcessingException {
        String sku = "SKU123";
        var productoResponse = new ProductoResponse(sku, "Test Product", "Desc", 90, "EN_STOCK");
        var indicadorStock = new IndicadorStock(1L, productoResponse, 90, 10, "SOBRE_UMBRAL");

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/stock/" + sku))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(indicadorStock)));

        var result = inventarioService.consultarStock(sku);

        assertNotNull(result);
        assertEquals(90, result.stockTotalConsolidado());
    }

    @Test
    void historialMovimientos_ShouldReturnListOfItemInventario_WhenCalled() throws JsonProcessingException {
        String sku = "SKU123";
        var productoResponse = new ProductoResponse(sku, "Test Product", "Desc", 90, "EN_STOCK");
        var itemInventario = new ItemInventario(1L, productoResponse, "Proveedor A", 100, LocalDateTime.now());
        var historial = List.of(itemInventario);

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/movimientos/" + sku))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(historial)));

        var result = inventarioService.historialMovimientos(sku);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void eliminarProducto_ShouldCompleteSuccessfully_WhenCalled() {
        String sku = "SKU123";
        mockServer.expect(requestTo("http://localhost:5002/api/inventario/productos/" + sku))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        assertDoesNotThrow(() -> inventarioService.eliminarProducto(sku));
    }

    @Test
    void calcularMetricas_ShouldReturnMetricaRentabilidad_WhenCalled() throws JsonProcessingException {
        String sku = "SKU123";
        var metricaRequest = new MetricaRequest(199.99, 50.0);
        var productoResponse = new ProductoResponse(sku, "Test Product", "Desc", 90, "EN_STOCK");
        var metricaRentabilidad = new MetricaRentabilidad(1L, productoResponse, 149.99, 50.0, 2.99, LocalDate.now());

        String url = String.format("/api/inventario/metricas/%s?precioVenta=%s&costoOperativo=%s", sku, metricaRequest.precioVenta(), metricaRequest.costoOperativo());

        mockServer.expect(requestTo("http://localhost:5002" + url))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(metricaRentabilidad)));

        var result = inventarioService.calcularMetricas(sku, metricaRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void obtenerMetricas_ShouldReturnListOfMetricaRentabilidad_WhenCalled() throws JsonProcessingException {
        String sku = "SKU123";
        var productoResponse = new ProductoResponse(sku, "Test Product", "Desc", 90, "EN_STOCK");
        var metricaRentabilidad = new MetricaRentabilidad(1L, productoResponse, 149.99, 50.0, 2.99, LocalDate.now());
        var metricas = List.of(metricaRentabilidad);

        mockServer.expect(requestTo("http://localhost:5002/api/inventario/metricas/" + sku))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(metricas)));

        var result = inventarioService.obtenerMetricas(sku);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
