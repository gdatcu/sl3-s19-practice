package ro.qatester.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.models.LoginResponse; // Importăm clasa POJO
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;

/** 2.7 Varianta 2: Test Profesional (folosind POJO) */
public class LoginTestPOJO {

    private TestUser user;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://apps.qualiadept.eu/hapifyme/api";
        user = TestUser.register();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test
    public void testLoginWithPOJO() {
        // 1. Trimitem cererea și DESERIALIZĂM răspunsul direct în obiectul LoginResponse
        LoginResponse loginData = given()
                .contentType(ContentType.JSON)
                .body("{\"username\": \"" + user.username + "\", \"password\": \"" + user.password + "\"}")
        .when()
                .post("/user/login.php")
        .then()
                .statusCode(200) // Validăm statusul HTTP întâi
                .extract()
                .as(LoginResponse.class); // Magia se întâmplă aici!

        // 2. Acum lucrăm cu obiecte Java, nu cu string-uri JSON
        System.out.println("Status API: " + loginData.getStatus());
        System.out.println("Token utilizator: " + loginData.getToken());
        System.out.println("User Email: " + loginData.getUser().getEmail());

        // 3. Validări (Type Safe)
        Assert.assertEquals(loginData.getStatus(), "success", "Statusul nu este cel așteptat!");
        Assert.assertNotNull(loginData.getToken(), "Token-ul lipsește!");
        Assert.assertTrue(loginData.getToken().length() > 20, "Token-ul pare prea scurt/invalid!");
        // Putem valida și datele imbricate
        Assert.assertNotNull(loginData.getUser(), "Obiectul 'user' lipsește din răspuns!");
        Assert.assertTrue(loginData.getUser().getId() > 0, "ID-ul utilizatorului este invalid!");
    }
}
