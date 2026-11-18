package ro.qatester.api.tests;

import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.models.LoginRequest;
import ro.qatester.api.models.LoginResponse;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/** 3.8 Soluții la Exerciții */
public class Chapter3ExercisesTest {

    private TestUser user;

    @BeforeClass
    public void setup() {
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    /** Soluție Exercițiul 1: Test Simplu (JsonPath) - token, user.id și message cu .path("") */
    @Test
    public void simpleExtraction() {
        String body = "{\"username\": \"" + user.username + "\", \"password\": \"" + user.password + "\"}";

        Response res = given()
                .baseUri("https://apps.qualiadept.eu/hapifyme/api")
                .contentType("application/json")
                .body(body)
                .when()
                .post("/user/login.php")
                .then()
                .statusCode(200)
                .extract().response();

        String token = res.path("token");
        String userId = res.path("user.id");      // în JSON id-ul este String: "105"
        String message = res.path("message");
        System.out.println("Token: " + token);
        System.out.println("User ID: " + userId + " | Mesaj: " + message);

        assertThat(token, not(emptyOrNullString()));
        assertThat(userId, equalTo(user.userId));
        assertThat(message, equalTo("Login successful"));
    }

    /** Soluție Exercițiul 2: Test Profesional (POJO) */
    @Test
    public void professionalTest() {
        LoginRequest req = new LoginRequest(user.username, user.password);

        LoginResponse response = given()
                .baseUri("https://apps.qualiadept.eu/hapifyme/api/user")
                .log().all()
                .contentType("application/json")
                .body(req)
                .when()
                .post("/login.php")
                .as(LoginResponse.class); // Deserializare directă

        // Mesajul real este "Login successful" -> conține "successful", NU "successfully"
        if (response.getUser() != null && response.getMessage().contains("successful")) {
            System.out.println("Test POJO Passed! Token length: " + response.getToken().length());
        } else {
            throw new AssertionError("Validarea POJO a eșuat");
        }
        assertThat(response.getUser().getId(), greaterThan(0));
    }
}
