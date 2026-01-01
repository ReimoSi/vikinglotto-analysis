package ee.reimosi.lotto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vikinglottoOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Vikinglotto Analysis & Generator API")
                        .version("0.1.0")
                        .description("CRUD for draws, simple analysis (frequencies + chi-square) and ticket generator.")
        );
    }
}
