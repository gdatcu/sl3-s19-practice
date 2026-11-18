package ro.qatester.api.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/** 5.3 Utilizare în Test */
public class MyTest extends BaseTest {

    private TestUser user;

    @BeforeClass   // TestNG rulează întâi setup() din BaseTest (clasa părinte), apoi această metodă
    public void createUser() {
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void simpleTest() {
        given()
            .spec(requestSpec) // Include automat URL și Headers
            .header("Authorization", user.apiKey)   // get_profile.php cere API key...
            .queryParam("user_id", user.userId)     // ...și user_id
        .when()
            .get("/user/get_profile.php") // Doar endpoint-ul relativ
        .then()
            .statusCode(200)
            .body("status", equalTo("success"));
    }
}
