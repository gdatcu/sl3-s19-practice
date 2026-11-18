package ro.qatester.api.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/** 2.9 Soluții la Exerciții */
public class Chapter2ExercisesTest {

    private TestUser user;
    private String apiKey;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = TestUser.BASE_URI;
        user = TestUser.register();
        apiKey = user.apiKey;
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    /** Soluție Exercițiul 1: Path Param */
    @Test
    public void testHealthCheckWithParams() {
        given()
            .header("Authorization", apiKey)
            .pathParam("section", "system")
            .pathParam("page", "health_check.php")
        .when()
            .get("/{section}/{page}")
        .then()
            .statusCode(200)
            .body("status", equalTo("success"));
    }

    /** Soluție Exercițiul 2: Extragere Email după Login */
    @Test
    public void testLoginAndExtractEmail() {
        Response response = given()
            .contentType("application/json")
            .body("{\"username\": \"" + user.username + "\", \"password\": \"" + user.password + "\"}")
        .when()
            .post("/user/login.php");

        String email = response.jsonPath().getString("user.email");
        System.out.println("Email extras: " + email);

        Assert.assertTrue(email.contains("@"), "Email-ul invalid!");
    }
}
