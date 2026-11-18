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

/** 3.3 Activitate Practică: Scrierea Testului de Login cu POJO */
public class HapifyMeLoginTest {

    private TestUser user;   // înlocuiește contul "test_qa", care nu mai există după migrare

    @BeforeClass
    public void setup() {
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testLoginWithPOJO() {
        // 1. Pregătim datele (Obiect Java)
        LoginRequest loginBody = new LoginRequest(user.username, user.password);

        // 2. Trimitem cererea
        LoginResponse response = given()
                .baseUri("https://apps.qualiadept.eu/hapifyme/api")
                .contentType(ContentType.JSON) // Specificăm că trimitem JSON
                .body(loginBody) // REST Assured face SERIALIZAREA automat
                .when()
                .post("/user/login.php")
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class); // REST Assured face DESERIALIZAREA automat

        // 3. Validăm datele (folosind obiectul Java primit)
        System.out.println("Token extras: " + response.getToken());

        assertThat(response.getStatus(), is("success"));
        assertThat(response.getToken(), not(emptyString()));
        assertThat(response.getUser().getEmail(), equalTo(user.email));
    }
}
