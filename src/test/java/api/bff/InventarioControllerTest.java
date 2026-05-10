package api.bff;

import api.bff.controller.InventarioController;
import api.bff.dto.inventario.ProductoRequest;
import api.bff.dto.inventario.ProductoResponse;
import api.bff.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService inventarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void crearProductoReturnsOk() throws Exception {
        ProductoRequest req = new ProductoRequest("Prod", "Desc", 0);
        ProductoResponse resp = new ProductoResponse("SKU1", "Prod", "Desc", 10, "EN_STOCK");
        when(inventarioService.crearProducto(any(ProductoRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/bff/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU1"))
                .andExpect(jsonPath("$.nombre").value("Prod"));
    }

    @Test
    void eliminarProductoReturnsNoContent() throws Exception {
        doNothing().when(inventarioService).eliminarProducto("SKU1");

        mockMvc.perform(delete("/api/bff/inventario/productos/SKU1"))
                .andExpect(status().isNoContent());
    }
}
