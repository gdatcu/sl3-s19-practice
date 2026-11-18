package ro.qatester.api.tests;

import io.restassured.RestAssured;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

// Importuri statice pentru a face codul mai citibil (Given, When, Then)
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/** 1.4 Activitate Practică: Primul Test API pe hapifyMe (Health Check) + 1.8 Soluție Exercițiul 2 */
public class SystemHealthTest {

    private TestUser user;
    private String apiKey;

    @BeforeClass
    public void setup() {
        // Setăm URL-ul de bază global pentru acest test
        // Astfel nu trebuie să scriem tot URL-ul la fiecare request
        RestAssured.baseURI = "https://apps.qualiadept.eu/hapifyme/api";

        // Cheile API din material nu mai sunt valide după migrare:
        // creăm un utilizator nou și folosim API key-ul primit la înregistrare
        user = TestUser.register();
        apiKey = user.apiKey;
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testSystemHealthCheck() {
        given()
            .header("Authorization", apiKey) // 1. PREGĂTIRE (GIVEN)
            .log().all() // Opțional: Printează detaliile cererii în consolă pentru debugging
        .when()
            .get("/system/health_check.php") // 2. ACȚIUNE (WHEN)
        .then()
            .log().body() // Opțional: Printează răspunsul în consolă
            .statusCode(200) // 3. VALIDARE (THEN) - Verificăm statusul
            .body("status", equalTo("success")) // Verificăm câmpul 'status' din JSON
            .body("message", equalTo("System health check successful."));
    }

    /** Exercițiul 2: Test Negativ (Health Check fără Auth) */
    @Test
    public void testHealthCheckUnauthorized() {
        given()
            // NU punem header-ul de Authorization
            .log().all()
        .when()
            .get("/system/health_check.php")
        .then()
            .log().body()
            // Ne așteptăm la eroare de acces
            .statusCode(401) // Verificăm că accesul e refuzat
            .body("status", equalTo("error"))
            .body("message", equalTo("API key is required."));
    }
}
