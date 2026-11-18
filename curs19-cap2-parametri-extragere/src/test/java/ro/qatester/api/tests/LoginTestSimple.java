package ro.qatester.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** 2.7 Varianta 1: Test Simplu (fără POJO, doar JsonPath) */
public class LoginTestSimple {

    private TestUser user;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://apps.qualiadept.eu/hapifyme/api";
        user = TestUser.register();     // Folosește un user valid!
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testLoginAndExtractData() {
        // 1. Trimitem cererea de Login
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"" + user.username + "\", \"password\": \"" + user.password + "\"}")
        .when()
                .post("/user/login.php");

        // 2. Validare inițială (Status Code & Mesaj)
        response.then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", containsString("Login successful"));

        // 3. Extragere date folosind JsonPath
        String token = response.jsonPath().getString("token");
        int userId = response.jsonPath().getInt("user.id");
        String email = response.jsonPath().getString("user.email");

        // Afișare pentru debug
        System.out.println("Token extras: " + token);
        System.out.println("User ID: " + userId);
        System.out.println("Email: " + email);

        // 4. Validări logice suplimentare (folosind TestNG Assert)
        Assert.assertNotNull(token, "Token-ul nu trebuie să fie null!");
        Assert.assertTrue(userId > 0, "ID-ul utilizatorului trebuie să fie un număr pozitiv!");
        Assert.assertTrue(email.contains("@"), "Email-ul extras nu este valid!");
    }
}
