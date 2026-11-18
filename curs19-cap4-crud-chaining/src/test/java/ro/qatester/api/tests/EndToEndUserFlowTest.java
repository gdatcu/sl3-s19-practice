package ro.qatester.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import ro.qatester.api.models.*;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** 4.5 Activitate Practică: Fluxul End-to-End (Register -> Login -> Update -> Delete) */
public class EndToEndUserFlowTest {

    @Test
    public void testFullUserLifecycle() {
        String baseUri = "https://apps.qualiadept.eu/hapifyme/api";

        // --- ETAPA 1: REGISTER (Obținem API Key și User ID) ---
        long ts = System.currentTimeMillis();
        String uniqueEmail = "chain_" + ts + "@hapifyme.com";
        String password = TestUser.PASSWORD;   // min. 8 caractere, majusculă, minusculă, cifră, caracter special
        String firstName = "Chain";
        String lastName = "User";

        RegisterRequest registerBody = new RegisterRequest(firstName, lastName, uniqueEmail, password);

        RegisterResponse registerResponse = given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(registerBody)
                .when()
                .post("/user/register.php")
                .then()
                .statusCode(201)
                .extract().as(RegisterResponse.class);

        String extractedApiKey = registerResponse.getApiKey();
        int extractedUserId = Integer.parseInt(registerResponse.getUserId());
        String extractedUsername = registerResponse.getUsername();

        System.out.println("1. Register Done. API Key: " + extractedApiKey + " . Username is: " + extractedUsername);

        // --- ETAPA 2: LOGIN (Obținem Bearer Token) ---
        // Avem nevoie de token pentru ștergere mai târziu
        // Notă: login-ul hapifyMe acceptă DOAR username-ul (generat la register), nu și email-ul
        LoginRequest loginBody = new LoginRequest(extractedUsername, password);

        LoginResponse loginResponse = given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(loginBody)
                .when()
                .post("/user/login.php")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        String bearerToken = loginResponse.getToken();
        System.out.println("2. Login Done. Bearer Token: " + bearerToken);

        // --- ETAPA 3: UPDATE PROFILE (Folosind API Key) ---
        String updatedEmail = "updated_" + ts + "@updated.com";   // email unic la fiecare rulare
        UpdateProfileRequest updateBody = new UpdateProfileRequest(extractedUserId, "UpdatedName",
                "UpdatedLast", updatedEmail, "updated.jpg");

        given()
                .baseUri(baseUri)
                .header("Authorization", extractedApiKey) // Folosim API Key
                .contentType(ContentType.JSON)
                .log().all()
                .body(updateBody)
                .when()
                .put("/user/update_profile.php")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", containsString("updated"));

        System.out.println("3. Update Done.");

        // --- ETAPA 3b: READ PROFILE (Chaining: verificăm că update-ul s-a aplicat) ---
        given()
                .baseUri(baseUri)
                .header("Authorization", extractedApiKey)
                .queryParam("user_id", extractedUserId)
                .when()
                .get("/user/get_profile.php")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("user.first_name", equalTo("UpdatedName"))
                .body("user.last_name", equalTo("UpdatedLast"))
                .body("user.email", equalTo(updatedEmail))
                .body("user.profile_pic", equalTo("updated.jpg"));

        System.out.println("3b. Get Profile Done. Datele actualizate au fost verificate.");

        // --- ETAPA 4: DELETE PROFILE (Cleanup - Folosind Bearer Token) ---
        given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + bearerToken) // Folosim Bearer Token
                .contentType(ContentType.JSON)
                .when()
                .delete("/user/delete_profile.php")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", containsString("deleted"));

        System.out.println("4. Delete Done. User cleaned up.");

        // --- ETAPA 5: VERIFICARE (Idempotency) - al doilea DELETE nu mai găsește userul ---
        given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + bearerToken)
                .when()
                .delete("/user/delete_profile.php")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found or already deleted."));
    }
}
