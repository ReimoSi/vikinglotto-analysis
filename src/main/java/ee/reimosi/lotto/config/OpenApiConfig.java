package ee.reimosi.lotto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vikinglottoOpenAPI() {
        var basic = new SecurityScheme()
                .name("basicAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");

        return new OpenAPI()
                .info(new Info()
                        .title("Vikinglotto Analysis & Generator API")
                        .version("0.1.0")
                        .description("CRUD for draws, analysis and ticket generator."))
                .components(new Components().addSecuritySchemes("basicAuth",
                        new SecurityScheme().name("basicAuth").type(SecurityScheme.Type.HTTP).scheme("basic")));
    }
}
