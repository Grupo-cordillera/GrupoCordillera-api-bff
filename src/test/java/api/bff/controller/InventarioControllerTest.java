package api.bff.controller;

import api.bff.dto.inventario.IndicadorStock;
import api.bff.dto.inventario.ProductoRequest;
import api.bff.dto.inventario.ProductoResponse;
import api.bff.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    private ProductoRequest productoRequest;
    private ProductoResponse productoResponse;
    private IndicadorStock indicadorStock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();

        // Corregido: Usamos el constructor del record.
        productoRequest = new ProductoRequest("Producto de Prueba", "Descripción de prueba", 10);

        // Corregido: Usamos el constructor del record.
        productoResponse = new ProductoResponse("SKU-TEST-001", "Producto de Prueba", "Descripción de prueba", 100, "EN_STOCK");

        // Corregido: Usamos el constructor del record.
        indicadorStock = new IndicadorStock(1L, productoResponse, 100, 10, "SOBRE_UMBRAL");
    }

    @Test
    void crearProducto_ShouldReturnOkAndProductoResponse_WhenCalled() throws Exception {
        when(inventarioService.crearProducto(any(ProductoRequest.class))).thenReturn(productoResponse);

        mockMvc.perform(post("/api/bff/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku", is("SKU-TEST-001")))
                .andExpect(jsonPath("$.nombre", is("Producto de Prueba")));
    }

    @Test
    void listarTodosLosProductos_ShouldReturnOkAndListOfProductos_WhenCalled() throws Exception {
        List<ProductoResponse> productos = Collections.singletonList(productoResponse);
        when(inventarioService.listarTodosLosProductos()).thenReturn(productos);

        mockMvc.perform(get("/api/bff/inventario/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sku", is("SKU-TEST-001")));
    }

    @Test
    void consultarStock_ShouldReturnOkAndIndicadorStock_WhenSkuExists() throws Exception {
        when(inventarioService.consultarStock(anyString())).thenReturn(indicadorStock);

        mockMvc.perform(get("/api/bff/inventario/stock/{sku}", "SKU-TEST-001"))
                .andExpect(status().isOk())
                // Validamos los datos del indicador y del producto anidado
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.stockTotalConsolidado", is(100)))
                .andExpect(jsonPath("$.producto.sku", is("SKU-TEST-001")));
    }
}
