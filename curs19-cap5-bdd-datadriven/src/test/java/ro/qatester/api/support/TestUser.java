package ro.qatester.api.support;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;

/**
 * Utilizator de test creat "pe loc" prin POST /user/register.php.
 *
 * De ce? După migrarea pe apps.qualiadept.eu, API key-urile și utilizatorii
 * hardcodați din material (test_qa, john..._doe...) nu mai există. Fiecare clasă
 * de test își creează propriul utilizator (cu API key valid) și îl șterge la final.
 */
public class TestUser {

    public static final String BASE_URI = "https://apps.qualiadept.eu/hapifyme/api";

    /** Respectă politica API: min. 8 caractere, majusculă, minusculă, cifră și caracter special */
    public static final String PASSWORD = "Qa!Test2026x";

    private static final AtomicInteger COUNTER = new AtomicInteger();

    public final String firstName;
    public final String lastName;
    public final String email;
    public final String password;
    public final String username;           // generat de server, ex: john1765..._doe1765...
    public final String userId;             // ex: "105" (String în JSON)
    public final String apiKey;             // folosit în header-ul Authorization
    public final String confirmationToken;

    private TestUser(String firstName, String lastName, String email, JsonPath json) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = PASSWORD;
        this.username = json.getString("username");
        this.userId = json.getString("user_id");
        this.apiKey = json.getString("api_key");
        this.confirmationToken = json.getString("confirmation_token");
    }

    /** Înregistrează un utilizator nou, cu date unice pentru fiecare rulare. */
    public static TestUser register() {
        String unic = System.currentTimeMillis() + "" + COUNTER.incrementAndGet();
        String firstName = "John" + unic;
        String lastName = "Doe" + unic;
        String email = "john" + unic + "@hapifyme.com";

        JsonPath json = given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "first_name", firstName,
                        "last_name", lastName,
                        "email", email,
                        "password", PASSWORD))
            .when()
                .post("/user/register.php")
            .then()
                .statusCode(201)
                .extract().jsonPath();

        return new TestUser(firstName, lastName, email, json);
    }

    /** Login cu username + parolă; returnează Bearer token-ul (JWT). */
    public String login() {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
            .when()
                .post("/user/login.php")
            .then()
                .statusCode(200)
                .extract().path("token");
    }

    /** Cleanup: șterge contul (Bearer token). Nu aruncă excepții dacă userul nu mai există. */
    public void delete() {
        try {
            String token = login();
            given()
                    .baseUri(BASE_URI)
                    .header("Authorization", "Bearer " + token)
                .when()
                    .delete("/user/delete_profile.php");
        } catch (AssertionError | RuntimeException e) {
            // userul a fost deja șters de test (ex: fluxul E2E din Capitolul 4)
        }
    }
}
