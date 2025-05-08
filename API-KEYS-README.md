# API Keys Management Guide

This document provides instructions for securely managing API keys for development and production environments.

## Gemini API Key

The application uses Google's Gemini AI API for generating poker advice. You'll need to obtain an API key from the Google AI Studio.

### Local Development

For local development, you have two options for configuring the Gemini API key:

1. **Environment Variable (Recommended)**:
   - Set the `GEMINI_API_KEY` environment variable in your local environment.
   - For example, in bash/zsh: `export GEMINI_API_KEY=your_api_key_here`
   - For Windows: `set GEMINI_API_KEY=your_api_key_here`

2. **Application Properties (Alternative)**:
   - Create a file called `local.properties` at the root of the project
   - Add the following line: `gemini.api.key=your_api_key_here`
   - This file is already in `.gitignore` to prevent accidental commits

### Production Deployment

For production deployment to Google Cloud, we recommend using Google Cloud Secret Manager:

1. **Store the API key in Secret Manager**:
   ```bash
   gcloud secrets create gemini-api-key --replication-policy="automatic"
   echo -n "your_api_key_here" | gcloud secrets versions add gemini-api-key --data-file=-
   ```

2. **Grant access to your service account**:
   ```bash
   gcloud secrets add-iam-policy-binding gemini-api-key \
     --member="serviceAccount:your-service-account@your-project.iam.gserviceaccount.com" \
     --role="roles/secretmanager.secretAccessor"
   ```

3. **Configure your app.yaml to use the secret**:
   ```yaml
   env_variables:
     SPRING_PROFILES_ACTIVE: production
   
   secrets:
     - name: GEMINI_API_KEY
       secret_manager_path: projects/your-project-id/secrets/gemini-api-key/versions/latest
   ```

### Security Best Practices

1. **Never commit API keys to source control**
2. **Rotate API keys periodically**
3. **Use minimum required permissions for service accounts**
4. **Monitor API key usage for unusual patterns**
5. **Use environment-specific API keys to limit potential exposure**

## Additional Resources

- [Google Cloud Secret Manager Documentation](https://cloud.google.com/secret-manager/docs)
- [Spring Boot External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Google AI Studio](https://ai.google.dev/) - To obtain Gemini API keys 