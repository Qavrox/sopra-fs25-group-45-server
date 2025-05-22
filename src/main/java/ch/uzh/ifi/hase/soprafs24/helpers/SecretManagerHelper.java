package ch.uzh.ifi.hase.soprafs24.helpers;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class SecretManagerHelper {
    
    private static final String GEMINI_API_KEY_SECRET_ID = "gemini-api-key";
    private static final String GEMINI_API_KEY_ENV_VAR = "GEMINI_API_KEY";
    
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    
    private SecretManagerServiceClient secretManagerClient;
    
    @PostConstruct
    public void init() {
        // Only initialize Secret Manager client if not in local profile
        if (!"local".equals(activeProfile)) {
            try {
                this.secretManagerClient = SecretManagerServiceClient.create();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize Secret Manager client", e);
            }
        }
    }
    
    /**
     * Gets the Gemini API key from either Google Secret Manager or environment variables
     * depending on the active profile.
     *
     * @return The Gemini API key
     * @throws RuntimeException if the key cannot be retrieved
     */
    public String getGeminiApiKey() {
        if ("local".equals(activeProfile)) {
            return getLocalGeminiApiKey();
        } else {
            return getSecretManagerGeminiApiKey();
        }
    }
    
    /**
     * Gets the Gemini API key from environment variables for local development.
     *
     * @return The Gemini API key from environment variables
     * @throws RuntimeException if the environment variable is not set
     */
    private String getLocalGeminiApiKey() {
        String apiKey = System.getenv(GEMINI_API_KEY_ENV_VAR);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY environment variable is not set");
        }
        return apiKey;
    }
    
    /**
     * Gets the Gemini API key from Google Secret Manager.
     *
     * @return The Gemini API key from Secret Manager
     * @throws RuntimeException if the secret cannot be retrieved
     */
    private String getSecretManagerGeminiApiKey() {
        try {
            final String projectId = "702248203659";
            
            SecretVersionName secretVersionName = SecretVersionName.of(
                projectId,
                GEMINI_API_KEY_SECRET_ID,
                "latest"
            );
            
            AccessSecretVersionResponse response = secretManagerClient.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve Gemini API key from Secret Manager", e);
        }
    }
    
    /**
     * Gets any secret from Google Secret Manager by secret ID.
     *
     * @param secretId The ID of the secret to retrieve
     * @return The secret value
     * @throws RuntimeException if the secret cannot be retrieved
     */
    public String getSecret(String secretId) {
        if ("local".equals(activeProfile)) {
            throw new RuntimeException("Secret Manager is not available in local profile");
        }
        
        try {
            final String projectId = "702248203659";
            
            SecretVersionName secretVersionName = SecretVersionName.of(
                projectId,
                secretId,
                "latest"
            );
            
            AccessSecretVersionResponse response = secretManagerClient.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve secret from Secret Manager: " + secretId, e);
        }
    }
} 