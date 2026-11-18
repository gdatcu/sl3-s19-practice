package ro.qatester.api.tests;

import io.restassured.RestAssured;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** 2.2 Lucrul cu Parametri în REST Assured */
public class ParametersTest {

    private TestUser user;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = TestUser.BASE_URI;
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test(description = "Sintaxa pentru Query Parameters")
    public void testQueryParam() {
        given()
            .header("Authorization", user.apiKey) // API Key necesar pentru acest endpoint
            .queryParam("username_or_email", user.email) // REST Assured adaugă automat ?cheie=valoare
            .log().uri() // Printează URL-ul final pentru verificare
        .when()
            .get("/user/retrieve_token.php")
        .then()
            .statusCode(200);
    }

    @Test(description = "Sintaxa pentru Path Parameters")
    public void testPathParams() {
        String section = "user";
        String endpoint = "get_profile.php";

        given()
            .header("Authorization", user.apiKey)   // get_profile.php cere API key
            .queryParam("user_id", user.userId)     // ...și parametrul user_id (nedocumentat în Swagger!)
            .pathParam("section", section)
            .pathParam("endpoint", endpoint)
        .when()
            .get("/{section}/{endpoint}") // URL devine: /user/get_profile.php
        .then()
            .statusCode(200)
            .body("status", equalTo("success"))
            .body("user.username", equalTo(user.username));
    }
}
