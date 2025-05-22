package ch.uzh.ifi.hase.soprafs24.helpers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * A local implementation of SecretManagerHelper for development profiles
 * that bypasses Google Secret Manager and uses in-memory H2 database
 */
@Component
@Qualifier("localSecretManagerHelper")
@Profile("!prod") // Active in all profiles except prod
public class LocalSecretManagerHelper {
    
    /**
     * Dummy initialization method - we don't need to connect to Secret Manager
     */
    @PostConstruct
    public void init() {
        // No initialization needed for local development
    }
    
    /**
     * Returns safe development values instead of accessing Secret Manager
     */
    public String getSecret(String secretId) {
        // Local development uses H2 in-memory database
        // Return null for database secrets to let Spring Boot autoconfigure H2
        switch (secretId) {
            case "db-url":
            case "db-username":
            case "db-password":
                return null;
            case "gemini-api-key":
                return "dummy-api-key-for-local-development";
            default:
                throw new RuntimeException("Unknown secret ID: " + secretId);
        }
    }
    
    /**
     * Returns a dummy API key for local development
     */
    public String getGeminiApiKey() {
        return "dummy-api-key-for-local-development";
    }
} 