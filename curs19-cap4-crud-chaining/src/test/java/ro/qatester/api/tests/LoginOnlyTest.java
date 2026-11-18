package ro.qatester.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.models.LoginRequest;
import ro.qatester.api.models.LoginResponse;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/** 4.8 Exercițiul 1: Implementarea Logicii de Login */
public class LoginOnlyTest {

    private TestUser user;   // "un cont existent" - creat înainte de test

    @BeforeClass
    public void setup() {
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testLoginAfiseazaBearerToken() {
        LoginResponse response = given()
                .baseUri(TestUser.BASE_URI)
                .contentType(ContentType.JSON)
                .body(new LoginRequest(user.username, user.password))
            .when()
                .post("/user/login.php")
            .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        System.out.println("Bearer Token: " + response.getToken());
        assertThat(response.getToken(), startsWith("eyJ"));   // un JWT începe întotdeauna cu "eyJ"
    }
}
