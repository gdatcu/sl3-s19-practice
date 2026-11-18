package ro.qatester.api.tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.*;

/** 2.5 Activitate Practică: Extragerea Token-ului de confirmare */
public class TokenExtractionTest {

    private TestUser user;
    private String apiKey;
    private String testEmail;   // userul trebuie să existe - îl creăm noi

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://apps.qualiadept.eu/hapifyme/api";
        user = TestUser.register();
        apiKey = user.apiKey;
        testEmail = user.email;
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testRetrieveAndValidateToken() {
        Response response = given()
            .header("Authorization", apiKey)
            .queryParam("username_or_email", testEmail)
            .log().uri() // Vedem URL-ul generat în consolă
        .when()
            .get("/user/retrieve_token.php");

        // Afișăm răspunsul pentru debug
        response.then().log().body();

        // 1. Validare status HTTP
        response.then().statusCode(200);

        // 2. Extragere date
        JsonPath json = response.jsonPath();
        String status = json.getString("status");
        String confToken = json.getString("confirmation_token");

        System.out.println("Status: " + status);
        System.out.println("Confirmation Token: " + confToken);

        // 3. Validare logică
        Assert.assertEquals(status, "success", "Statusul ar trebui să fie 'success'");

        // AI Tip: Poți întreba ChatGPT "How to assert a string is not null or empty in TestNG?"
        Assert.assertNotNull(confToken, "Token-ul nu trebuie să fie null!");
        Assert.assertFalse(confToken.isEmpty(), "Token-ul nu trebuie să fie gol!");

        // Bonus: token-ul este cel primit la înregistrare
        Assert.assertEquals(confToken, user.confirmationToken);
    }
}
