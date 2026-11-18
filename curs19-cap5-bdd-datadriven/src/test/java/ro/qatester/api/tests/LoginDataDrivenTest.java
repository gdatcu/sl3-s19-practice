package ro.qatester.api.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ro.qatester.api.models.LoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/** 5.2 Data Driven Testing cu TestNG DataProvider */
public class LoginDataDrivenTest extends BaseTest {

    // 1. Definim Furnizorul de Date
    @DataProvider(name = "loginData")
    public Object[][] createLoginData() {
        return new Object[][] {
                // { username, password, expectedStatusCode, expectedError }
                { "user_inexistent", "pass123", 401, "Invalid username or password" },
                { "test_qa", "parola_gresita", 401, "Invalid username or password" },
                { "", "pass123", 401, "Invalid username or password" }
        };
    }

    // 2. Conectăm Testul la DataProvider
    @Test(dataProvider = "loginData")
    public void testLoginScenarios(String user, String pass, int statusCode, String errMessage) {

        LoginRequest req = new LoginRequest(user, pass);

        given()
                .spec(requestSpec) // Include automat URL și Headers
                .body(req)
                .when()
                .post("/user/login.php")
                .then()
                // 3. Validăm dinamic folosind parametrii primiți
                .statusCode(statusCode)
                .body("message", equalTo(errMessage));
    }
}
