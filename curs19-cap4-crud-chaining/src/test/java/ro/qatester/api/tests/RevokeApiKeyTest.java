package ro.qatester.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

/** 4.9 Soluție Exercițiul 2: Revoke API Key */
public class RevokeApiKeyTest {

    private TestUser user;

    @BeforeClass
    public void setup() {
        // Precondiție: un API Key valid, extras dintr-un pas de Register
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();   // ștergerea folosește Bearer token, deci merge și după revoke
    }

    @Test
    public void testRevokeApiKey() {
        String userApiKey = user.apiKey;

        given()
                .baseUri("https://apps.qualiadept.eu/hapifyme/api")
                .header("Authorization", userApiKey) // Autentificare cu API Key
                .contentType(ContentType.JSON)
                .when()
                .post("/user/revoke_api_key.php")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("API key has been successfully revoked."));
    }

    @Test(dependsOnMethods = "testRevokeApiKey",
          description = "După revocare, cheia nu mai poate fi folosită")
    public void testCheiaRevocataNuMaiFunctioneaza() {
        given()
                .baseUri(TestUser.BASE_URI)
                .header("Authorization", user.apiKey)
                .when()
                .get("/system/health_check.php")
                .then()
                .statusCode(401)
                .body("message", equalTo("Invalid or inactive API key."));
    }
}
