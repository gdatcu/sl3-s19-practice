package ro.qatester.api.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;

/** 2.3 Extragerea Datelor - Metoda 1: JsonPath (Rapid și Murdar) */
public class JsonPathExtractionTest {

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

    @Test
    public void testExtractTokenAndId() {
        Response response = given()
            .contentType("application/json")
            .body("{\"username\": \"" + user.username + "\", \"password\": \"" + user.password + "\"}")
        .when()
            .post("/user/login.php");

        // Extragere folosind calea JSON (JsonPath)
        String token = response.jsonPath().getString("token");
        int userId = response.jsonPath().getInt("user.id"); // Navigare în obiect imbricat

        System.out.println("Token extras: " + token);
        Assert.assertNotNull(token, "Token-ul nu trebuie să fie null!");
        Assert.assertEquals(userId, Integer.parseInt(user.userId));
    }
}
