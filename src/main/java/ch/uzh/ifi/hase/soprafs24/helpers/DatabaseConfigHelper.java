package ch.uzh.ifi.hase.soprafs24.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfigHelper {
    
    private static final String DB_URL_SECRET_ID = "db-url";
    private static final String DB_USERNAME_SECRET_ID = "db-username";
    private static final String DB_PASSWORD_SECRET_ID = "db-password";
    
    @Autowired(required = false)
    @Qualifier("secretManagerHelper")
    private SecretManagerHelper secretManagerHelper;
    
    @Autowired(required = false)
    @Qualifier("localSecretManagerHelper")
    private LocalSecretManagerHelper localSecretManagerHelper;
    
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    
    /**
     * Configures the datasource for production environment
     * using Google Secret Manager to retrieve database credentials for PostgreSQL
     */
    @Bean
    @Primary
    @Profile("prod")
    public DataSource prodDataSource() {
        try {
            // Add method to SecretManagerHelper to retrieve database credentials
            String dbUrl = secretManagerHelper.getSecret(DB_URL_SECRET_ID);
            String dbUsername = secretManagerHelper.getSecret(DB_USERNAME_SECRET_ID);
            String dbPassword = secretManagerHelper.getSecret(DB_PASSWORD_SECRET_ID);
            
            // Check if URL already contains credentials (username and password)
            if (dbUrl.contains("user=") && dbUrl.contains("password=")) {
                // URL already has embedded credentials, don't set username and password separately
                return DataSourceBuilder.create()
                        .url(dbUrl)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } else {
                // Standard configuration with separate username and password
                return DataSourceBuilder.create()
                        .url(dbUrl)
                        .username(dbUsername)
                        .password(dbPassword)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            }
        } catch (Exception e) {
            // Log the error and provide more details
            System.err.println("Error configuring database: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // No database configuration for local development
    // Spring Boot will automatically configure H2 in-memory database
} 