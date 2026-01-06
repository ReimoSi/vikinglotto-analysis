package ee.reimosi.lotto.config;

import org.hsqldb.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HsqlServerConfig {
    private static final Logger log = LoggerFactory.getLogger(HsqlServerConfig.class);

    /**
     * Starts an HSQLDB Server on port 9001, backed by an in-memory DB named "mydb".
     * You can connect from IntelliJ with: jdbc:hsqldb:hsql://localhost:9001/mydb
     * NOTE: This mirrors the same in-memory DB used by spring.datasource.url=jdbc:hsqldb:mem:mydb
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server hsqlServer() {
        Server server = new Server();
        server.setLogWriter(null);        // suppress HSQL console logging
        server.setSilent(true);           // reduce verbosity
        server.setDatabaseName(0, "mydb");     // must match “mem:mydb”
        server.setDatabasePath(0, "mem:mydb"); // in-memory
        server.setPort(9001);             // TCP port for client connections
        log.info("Starting HSQLDB server on port 9001 (in-memory database 'mydb')");
        return server;
    }
}
