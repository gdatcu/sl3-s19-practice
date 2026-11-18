package ro.qatester.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Răspuns real POST /user/register.php (201):
 * {"status":"success","message":"User registered successfully. Use the token to confirm your account.",
 *  "api_key":"...","confirmation_token":"...","username":"john..._doe...","user_id":"105"}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResponse {
    private String status;
    private String message;

    @JsonProperty("user_id")
    private String userId;       // Mapăm "user_id" din JSON la "userId" în Java

    @JsonProperty("api_key")
    private String apiKey;

    private String username;     // ADĂUGAT: folosit în Capitolul 4 (registerResponse.getUsername())

    @JsonProperty("confirmation_token")
    private String confirmationToken;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getApiKey() { return apiKey; }
    public String getUsername() { return username; }
    public String getConfirmationToken() { return confirmationToken; }
}
