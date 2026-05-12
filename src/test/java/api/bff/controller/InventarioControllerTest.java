package api.bff.controller;

import api.bff.dto.inventario.*;
import api.bff.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();
    }

    @Test
    void crearProducto_ShouldReturnOkAndProductoResponse_WhenCalled() throws Exception {
        var productoRequest = new ProductoRequest("Producto de Prueba", "Descripción de prueba", 10);
        var productoResponse = new ProductoResponse("SKU-TEST-001", "Producto de Prueba", "Descripción de prueba", 100, "EN_STOCK");
        when(inventarioService.crearProducto(any(ProductoRequest.class))).thenReturn(productoResponse);

        mockMvc.perform(post("/api/bff/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is("SKU-TEST-001")));
    }

    @Test
    void listarTodosLosProductos_ShouldReturnOkAndListOfProductos_WhenCalled() throws Exception {
        var productoResponse = new ProductoResponse("SKU-TEST-001", "Producto de Prueba", "Descripción de prueba", 100, "EN_STOCK");
        List<ProductoResponse> productos = Collections.singletonList(productoResponse);
        when(inventarioService.listarTodosLosProductos()).thenReturn(productos);

        mockMvc.perform(get("/api/bff/inventario/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sku", is("SKU-TEST-001")));
    }

    @Test
    void registrarEntrada_ShouldReturnOkAndItemInventario_WhenCalled() throws Exception {
        var entradaRequest = new EntradaStockRequest("SKU123", "Proveedor A", 100);
        var productoResponse = new ProductoResponse("SKU123", "Test", "Desc", 100, "EN_STOCK");
        var itemInventario = new ItemInventario(1L, productoResponse, "Proveedor A", 100, LocalDateTime.now());
        when(inventarioService.registrarEntrada(any(EntradaStockRequest.class))).thenReturn(itemInventario);

        mockMvc.perform(post("/api/bff/inventario/stock/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entradaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void registrarSalida_ShouldReturnOkAndItemInventario_WhenCalled() throws Exception {
        var salidaRequest = new SalidaStockRequest("SKU123", "Venta 1", 10);
        var productoResponse = new ProductoResponse("SKU123", "Test", "Desc", 90, "EN_STOCK");
        var itemInventario = new ItemInventario(2L, productoResponse, "Venta 1", 10, LocalDateTime.now());
        when(inventarioService.registrarSalida(any(SalidaStockRequest.class))).thenReturn(itemInventario);

        mockMvc.perform(post("/api/bff/inventario/stock/salida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salidaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)));
    }

    @Test
    void consultarStock_ShouldReturnOkAndIndicadorStock_WhenSkuExists() throws Exception {
        var productoResponse = new ProductoResponse("SKU-TEST-001", "Producto de Prueba", "Descripción de prueba", 100, "EN_STOCK");
        var indicadorStock = new IndicadorStock(1L, productoResponse, 100, 10, "SOBRE_UMBRAL");
        when(inventarioService.consultarStock(anyString())).thenReturn(indicadorStock);

        mockMvc.perform(get("/api/bff/inventario/stock/{sku}", "SKU-TEST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.producto.sku", is("SKU-TEST-001")));
    }

    @Test
    void historialMovimientos_ShouldReturnOkAndListOfItemInventario_WhenCalled() throws Exception {
        var productoResponse = new ProductoResponse("SKU123", "Test", "Desc", 90, "EN_STOCK");
        var itemInventario = new ItemInventario(1L, productoResponse, "Proveedor A", 100, LocalDateTime.now());
        var historial = List.of(itemInventario);
        when(inventarioService.historialMovimientos(anyString())).thenReturn(historial);

        mockMvc.perform(get("/api/bff/inventario/movimientos/{sku}", "SKU123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void eliminarProducto_ShouldReturnNoContent_WhenCalled() throws Exception {
        doNothing().when(inventarioService).eliminarProducto(anyString());

        mockMvc.perform(delete("/api/bff/inventario/productos/{sku}", "SKU123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void calcularMetricas_ShouldReturnOkAndMetricaRentabilidad_WhenCalled() throws Exception {
        var metricaRequest = new MetricaRequest(199.99, 50.0);
        var productoResponse = new ProductoResponse("SKU123", "Test", "Desc", 90, "EN_STOCK");
        var metricaRentabilidad = new MetricaRentabilidad(1L, productoResponse, 149.99, 50.0, 2.99, LocalDate.now());
        when(inventarioService.calcularMetricas(anyString(), any(MetricaRequest.class))).thenReturn(metricaRentabilidad);

        mockMvc.perform(post("/api/bff/inventario/metricas/{sku}", "SKU123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metricaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void obtenerMetricas_ShouldReturnOkAndListOfMetricas_WhenCalled() throws Exception {
        var productoResponse = new ProductoResponse("SKU123", "Test", "Desc", 90, "EN_STOCK");
        var metricaRentabilidad = new MetricaRentabilidad(1L, productoResponse, 149.99, 50.0, 2.99, LocalDate.now());
        var metricas = List.of(metricaRentabilidad);
        when(inventarioService.obtenerMetricas(anyString())).thenReturn(metricas);

        mockMvc.perform(get("/api/bff/inventario/metricas/{sku}", "SKU123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}
