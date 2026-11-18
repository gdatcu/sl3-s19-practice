package ro.qatester.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ro.qatester.api.models.LoginRequest;
import ro.qatester.api.models.RegisterRequest;
import ro.qatester.api.models.RegisterResponse;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/** 3.4 Activitate Practică: Înregistrarea Utilizatorului (Test POST) */
public class HapifyMeRegisterTest {

    private String createdUsername;   // pentru cleanup
    private final String password = TestUser.PASSWORD;   // parola trebuie să respecte politica API

    @Test
    public void testRegisterWithTimestamp() {
        // 1. Generăm un email unic pentru a evita eroarea "Duplicate entry"
        String uniqueEmail = "john" + System.currentTimeMillis() + "@hapifyme.com";

        // 2. Pregătim datele (POJO Request)
        RegisterRequest registerBody = new RegisterRequest("John", "Doe", uniqueEmail, password);

        System.out.println("Se încearcă înregistrarea userului: " + uniqueEmail);

        // 3. Trimitem cererea POST
        RegisterResponse response = given()
                .baseUri("https://apps.qualiadept.eu/hapifyme/api/")
                .contentType(ContentType.JSON)
                .body(registerBody) // Jackson serializează automat în JSON cu snake_case
                .log().all()        // Vedem requestul in consolă
                .when()
                .post("user/register.php")
                .then()
                .log().all()        // Vedem răspunsul în consolă
                .statusCode(201)
                .extract()
                .as(RegisterResponse.class); // Deserializare în noul POJO

        createdUsername = response.getUsername();

        // 4. Validăm răspunsul
        // Verificăm statusul și existența ID-ului generat
        assertThat(response.getStatus(), is("success"));
        assertThat(response.getUserId(), not(emptyString()));
        assertThat(response.getMessage(), containsString("successfully"));

        System.out.println("User creat cu ID: " + response.getUserId());
    }

    @Test(description = "Test negativ: același email de două ori -> 409 Conflict")
    public void testRegisterEmailDuplicat() {
        TestUser existent = TestUser.register();
        try {
            given()
                    .baseUri(TestUser.BASE_URI)
                    .contentType(ContentType.JSON)
                    .body(new RegisterRequest("John", "Doe", existent.email, password))
                .when()
                    .post("/user/register.php")
                .then()
                    .statusCode(409)
                    .body("status", equalTo("error"))
                    .body("message", equalTo("Email already exists."));
        } finally {
            existent.delete();
        }
    }

    @Test(description = "Test negativ: parolă care nu respectă politica -> 400")
    public void testRegisterParolaSlaba() {
        given()
                .baseUri(TestUser.BASE_URI)
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("John", "Doe", "weak" + System.currentTimeMillis() + "@hapifyme.com", "abc"))
            .when()
                .post("/user/register.php")
            .then()
                .statusCode(400)
                .body("status", equalTo("error"))
                .body("message", startsWith("Password must be at least 8 characters long"));
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        // Ștergem userul creat de testRegisterWithTimestamp (login -> Bearer token -> delete)
        if (createdUsername == null) return;
        String token = given()
                .baseUri(TestUser.BASE_URI)
                .contentType(ContentType.JSON)
                .body(new LoginRequest(createdUsername, password))
            .when()
                .post("/user/login.php")
            .then()
                .extract().path("token");
        if (token != null) {
            given().baseUri(TestUser.BASE_URI)
                    .header("Authorization", "Bearer " + token)
                    .delete("/user/delete_profile.php");
        }
    }
}
