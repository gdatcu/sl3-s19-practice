package ro.qatester.api.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/** 3.1 Metoda 1: String JSON (Rapid, dar Riscant) */
public class StringJsonBodyTest {

    private TestUser user;

    @BeforeClass
    public void setup() {
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testLoginCuStringJson() {
        String jsonBody = "{\"username\": \"" + user.username + "\", \"password\": \"" + user.password + "\"}";

        given()
            .baseUri(TestUser.BASE_URI)
            .contentType("application/json")
            .body(jsonBody)
        .when()
            .post("/user/login.php")      // calea completă (în material apare doar "/login.php")
        .then()
            .statusCode(200)
            .body("status", equalTo("success"));
    }

    @Test(description = "Dezavantajul Metodei 1: un JSON stricat (ghilimea lipsă)")
    public void testJsonStricat() {
        String jsonStricat = "{\"username\": \"" + user.username + "\", \"password\": ";   // JSON incomplet

        given()
            .baseUri(TestUser.BASE_URI)
            .contentType("application/json")
            .body(jsonStricat)
        .when()
            .post("/user/login.php")
        .then()
            // Materialul spune 400 Bad Request; serverul hapifyMe răspunde însă cu 500 (pagină HTML)
            .statusCode(500);
    }
}
