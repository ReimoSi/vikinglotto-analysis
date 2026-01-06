package ee.reimosi.lotto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests against running app (RANDOM_PORT) using RestTemplate + Basic Auth.
 * Uses in-memory HSQL with data.sql seeding.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

    @LocalServerPort
    int port;

    private String baseUrl;

    private RestTemplate anon;
    private RestTemplate user;
    private RestTemplate admin;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        anon = new RestTemplate();

        user = new RestTemplate();
        user.getInterceptors().add(new BasicAuthenticationInterceptor("user", "user123"));

        admin = new RestTemplate();
        admin.getInterceptors().add(new BasicAuthenticationInterceptor("admin", "admin123"));
    }

    @Test
    @DisplayName("GET /api/draws without auth -> 401")
    void draws_unauthorized() {
        try {
            anon.getForEntity(baseUrl + "/api/draws", String.class);
        } catch (org.springframework.web.client.RestClientResponseException ex) {
            assertThat(ex.getStatusCode().value()).isEqualTo(401);
            return;
        }
        // If no exception, it must not be OK
        assertThat(true).as("Expected 401 Unauthorized").isFalse();
    }


    @Test
    @DisplayName("GET /api/draws with USER -> 200 and non-empty array")
    void draws_withUser_ok() {
        ResponseEntity<List<Map<String, Object>>> resp = user.exchange(
                baseUrl + "/api/draws",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> body = resp.getBody();
        assertThat(body).isNotNull().isNotEmpty();
        Map<String, Object> first = body.get(0);
        assertThat(first).containsKeys("id", "drawId", "drawDate", "mainNumbers", "bonusNumbers");
    }

    @Test
    @DisplayName("GET /api/analysis/summary with USER -> 200 and expected fields")
    void analysis_summary_ok() {
        ResponseEntity<Map<String, Object>> resp = user.exchange(
                baseUrl + "/api/analysis/summary",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body).containsKeys("drawCount", "mainFrequencies", "bonusFrequencies", "chiSquareStat", "chiSquareDf");
        Number drawCount = (Number) body.get("drawCount");
        assertThat(drawCount.intValue()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/generate (uniform, rows=3, seed=42) -> deterministic structure")
    void generate_uniform_ok() {
        String url = baseUrl + "/api/generate?method=uniform&rows=3&seed=42";
        HttpEntity<Void> req = new HttpEntity<>((Void) null);
        ResponseEntity<Map<String, Object>> resp = user.exchange(
                url, HttpMethod.POST, req, new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("method")).isEqualTo("uniform");
        assertThat(((Number) body.get("rows")).intValue()).isEqualTo(3);
        List<Map<String, Object>> tickets = (List<Map<String, Object>>) body.get("tickets");
        assertThat(tickets).isNotNull().hasSize(3);
        Map<String, Object> first = tickets.get(0);
        assertThat(first).containsKeys("main", "bonus");
        List<?> main = (List<?>) first.get("main");
        assertThat(main).hasSize(6);
        assertThat(first.get("bonus")).isInstanceOf(Number.class);
    }

    @Test
    @DisplayName("GET /api/generate/export.csv (uniform, rows=3, seed=42) -> CSV header present")
    void generate_export_csv_ok() {
        String url = baseUrl + "/api/generate/export.csv?method=uniform&rows=3&seed=42";
        ResponseEntity<String> resp = user.getForEntity(url, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        MediaType ct = resp.getHeaders().getContentType();
        assertThat(ct).isNotNull();
        assertThat(ct.toString()).contains("text/csv");
        String body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.split("\\R")[0].trim()).isEqualTo("main1,main2,main3,main4,main5,main6,bonus");
    }

    @Test
    @DisplayName("POST /api/admin/import/csv (ADMIN) -> imports rows and increases /api/draws size")
    void admin_import_csv_ok() {
        // 1) initial size
        int before = getDrawsCount();

        // 2) build multipart with small CSV
        String csvContent = String.join("\n",
                "draw_id,draw_date,main_numbers,bonus_numbers",
                "2099-01-01,2099-01-01,1 2 3 4 5 6,7",
                "2099-01-08,2099-01-08,10 11 12 13 14 15,2"
        );
        NamedByteArrayResource csvRes = new NamedByteArrayResource(
                csvContent.getBytes(StandardCharsets.UTF_8), "import-test.csv");

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", csvRes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> req = new HttpEntity<>(form, headers);

        // 3) call import as ADMIN
        ResponseEntity<Map<String, Object>> importResp = admin.exchange(
                baseUrl + "/api/admin/import/csv",
                HttpMethod.POST,
                req,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(importResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = importResp.getBody();
        assertThat(body).isNotNull();
        Number imported = (Number) body.getOrDefault("imported", 0);
        assertThat(imported.intValue()).isGreaterThanOrEqualTo(1);

        // 4) size increased
        int after = getDrawsCount();
        assertThat(after).isGreaterThan(before);
    }

    @Test
    @DisplayName("GET /api/draws/export.csv -> CSV header + at least one data row + date-like token")
    void draws_export_csv_ok() {
        String url = baseUrl + "/api/draws/export.csv";
        ResponseEntity<String> resp = user.getForEntity(url, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().getContentType().toString()).contains("text/csv");

        String body = resp.getBody();
        assertThat(body).isNotNull();

        // strip possible UTF-8 BOM
        if (!body.isEmpty() && body.charAt(0) == '\uFEFF') {
            body = body.substring(1);
        }

        String[] lines = body.split("\\R");
        assertThat(lines.length).isGreaterThan(1); // header + >=1 data row

        String header = lines[0].trim();
        assertThat(header).isEqualTo("id,draw_id,draw_date,main_numbers,bonus_numbers");

        // Accept both ISO (YYYY-MM-DD) and Excel-safe (="YYYY-MM-DD")
        java.util.regex.Pattern datePat = java.util.regex.Pattern.compile(
                "(?s)(\\b\\d{4}-\\d{2}-\\d{2}\\b|=\"\\d{4}-\\d{2}-\\d{2}\")"
        );
        assertThat(datePat.matcher(body).find())
                .as("CSV body should contain ISO date (YYYY-MM-DD) or Excel-safe =\"YYYY-MM-DD\"")
                .isTrue();
    }

    private int getDrawsCount() {
        ResponseEntity<List<Map<String, Object>>> resp = user.exchange(
                baseUrl + "/api/draws",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> list = resp.getBody();
        return (list == null) ? 0 : list.size();
    }

    /** ByteArrayResource with filename (multipart needs it). */
    static class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;
        NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }
        @Override
        public String getFilename() {
            return filename;
        }
    }
}
