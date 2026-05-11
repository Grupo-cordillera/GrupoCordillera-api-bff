package api.bff.service;

import api.bff.dto.inventario.ProductoRequest;
import api.bff.dto.inventario.ProductoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    // El servidor de prueba que interceptará las llamadas de RestClient.
    private MockRestServiceServer mockServer;

    // Un objeto para convertir objetos Java a JSON y viceversa.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // La instancia de InventarioService que vamos a probar.
    @InjectMocks
    private InventarioService inventarioService;

    @BeforeEach
    void setUp() {
        // Para usar MockRestServiceServer, necesitamos un RestTemplate.
        RestTemplate restTemplate = new RestTemplate();
        // El servidor de prueba se asocia al RestTemplate para interceptar sus llamadas.
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        // Creamos el RestClient a partir del RestTemplate que ya está siendo monitoreado por el mockServer.
        RestClient restClient = RestClient.builder(restTemplate).baseUrl("http://localhost:5002").build();
        // Inyectamos el RestClient de prueba en nuestro servicio.
        inventarioService = new InventarioService(restClient);
    }

    @AfterEach
    void tearDown() {
        // Verificamos que todas las expectativas que configuramos en el mockServer se hayan cumplido.
        mockServer.verify();
    }

    @Test
    void crearProducto_ShouldReturnProductoResponse_WhenCalled() throws JsonProcessingException {
        // Preparamos los datos de prueba correctos según la definición del record.
        var productoRequest = new ProductoRequest("Test Product", "A test description", 10);
        var productoResponse = new ProductoResponse("SKU123", "Test Product", "A test description", 0, "SIN_STOCK");

        // Configuramos el servidor de prueba para que espere una petición POST a "/api/inventario/productos"
        // y devuelva una respuesta exitosa (200 OK) con el cuerpo de productoResponse en formato JSON.
        mockServer.expect(requestTo("http://localhost:5002/api/inventario/productos"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(productoResponse)));

        // Llamamos al método que queremos probar.
        var result = inventarioService.crearProducto(productoRequest);

        // Verificamos que el resultado no sea nulo y que el SKU sea el esperado.
        assertNotNull(result);
        assertEquals("SKU123", result.sku());
        assertEquals("Test Product", result.nombre());
    }

    @Test
    void listarTodosLosProductos_ShouldReturnListOfProductoResponse_WhenCalled() throws JsonProcessingException {
        // Preparamos los datos de prueba correctos según la definición del record.
        var productoResponse = new ProductoResponse("SKU123", "Test Product", "A test description", 50, "EN_STOCK");
        List<ProductoResponse> productoResponseList = Collections.singletonList(productoResponse);

        // Configuramos el servidor de prueba para que espere una petición GET a "/api/inventario/productos"
        // y devuelva una respuesta exitosa (200 OK) con la lista de productos en formato JSON.
        mockServer.expect(requestTo("http://localhost:5002/api/inventario/productos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(productoResponseList)));

        // Llamamos al método que queremos probar.
        var result = inventarioService.listarTodosLosProductos();

        // Verificamos que el resultado no sea nulo, que contenga un elemento y que el SKU del primer elemento sea el esperado.
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SKU123", result.get(0).sku());
    }
}
