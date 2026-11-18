package ro.qatester.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ro.qatester.api.models.LoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/** 5.9 Soluție Exercițiul 1: Login Negativ Parametrizat */
public class NegativeLoginTest {

    @DataProvider(name = "invalidLoginData")
    public Object[][] provideData() {
        return new Object[][] {
                // username, password, status asteptat
                { "user_name", "pass123", 401 },               // user inexistent
                { "test_qa@hapifyme.com", "wrong", 401 },      // email în loc de username + parolă scurtă
                { "", "", 401 }                                 // câmpuri goale
        };
    }

    @Test(dataProvider = "invalidLoginData")
    public void testInvalidLogin(String username, String pass, int expectedStatus) {
        LoginRequest req = new LoginRequest(username, pass);

        given()
                .baseUri("https://apps.qualiadept.eu/hapifyme/api")
                .contentType(ContentType.JSON)
                .body(req)
                .when()
                .post("/user/login.php")
                .then()
                .log().ifValidationFails()
                .statusCode(expectedStatus)
                .body("status", not(equalTo("success"))); // Verificăm că nu e succes
    }
}
