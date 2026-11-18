package ro.qatester.api.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** 4.2 Operațiunea READ/GET cu date extrase (Get Profile) */
public class GetProfileTest {

    private TestUser user;

    @BeforeClass
    public void setup() {
        user = TestUser.register();   // "am extras deja apiKey din testul anterior"
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testGetProfile() {
        String apiKey = user.apiKey;

        given()
            .header("Authorization", apiKey) // Pasăm cheia în header
            .queryParam("user_id", user.userId) // OBLIGATORIU, deși lipsește din Swagger
        .when()
            .get("https://apps.qualiadept.eu/hapifyme/api/user/get_profile.php")
        .then()
            .statusCode(200)
            .body("status", equalTo("success"))
            .body("message", equalTo("Profile retrieved successfully."))
            .body("user.username", equalTo(user.username))
            .body("user.email", equalTo(user.email));
    }

    @Test(description = "Capcană: fără user_id serverul răspunde 200, dar cu status=error")
    public void testGetProfileFaraUserId() {
        given()
            .header("Authorization", user.apiKey)
        .when()
            .get(TestUser.BASE_URI + "/user/get_profile.php")
        .then()
            .statusCode(200)                                 // status HTTP "de succes"...
            .body("status", equalTo("error"))                // ...dar răspunsul este o eroare!
            .body("message", equalTo("User ID is required."));
    }
}
