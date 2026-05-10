package api.bff.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
public class RestClientConfig {

    @Value("${microservices.auth.url}")
    private String authUrl;

    @Value("${microservices.inventory.url}")
    private String inventoryUrl;

    @Bean
    public RestClient authRestClient() {
        // El cliente para el servicio de autenticación no necesita enviar el token,
        // ya que es el que los genera.
        return RestClient.builder()
                .baseUrl(authUrl)
                .build();
    }

    /**
     * Este bean configura el RestClient para el microservicio de inventario.
     * La parte más importante aquí es el .requestInterceptor().
     */
    @Bean
    public RestClient inventoryRestClient() {
        return RestClient.builder()
                .baseUrl(inventoryUrl)
                // Aquí es donde ocurre la magia.
                // Un interceptor es una pieza de código que se ejecuta para cada petición
                // que hace este RestClient.
                .requestInterceptor((request, body, execution) -> {

                    // Usamos RequestContextHolder para acceder al contexto de la petición actual
                    // que está manejando el BFF.
                    Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                            // Nos aseguramos de que sea una petición HTTP (siempre lo será en este contexto).
                            .filter(ServletRequestAttributes.class::isInstance)
                            .map(ServletRequestAttributes.class::cast)
                            // Obtenemos el objeto HttpServletRequest, que contiene toda la info de la petición entrante.
                            .map(ServletRequestAttributes::getRequest)
                            // De la petición entrante, obtenemos el encabezado "Authorization".
                            .map(req -> req.getHeader(HttpHeaders.AUTHORIZATION))
                            // Si el encabezado "Authorization" existe...
                            .ifPresent(token -> {
                                // ...lo añadimos a la petición SALIENTE que el RestClient está a punto de hacer.
                                // De esta forma, el microservicio de inventario recibirá el mismo token
                                // que el cliente le envió al BFF.
                                request.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
                            });

                    // Finalmente, le decimos a la petición que continúe su camino.
                    return execution.execute(request, body);
                })
                .build();
    }
}
