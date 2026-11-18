package ro.qatester.api.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ro.qatester.api.support.TestUser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static io.restassured.RestAssured.given;

/** 1.1 Comparație: Java Standard (HttpURLConnection) vs. REST Assured */
public class JavaStandardVsRestAssuredTest {

    private static final String HEALTH_URL = TestUser.BASE_URI + "/system/health_check.php";
    private TestUser user;

    @BeforeClass
    public void setup() {
        user = TestUser.register();    // obținem un API key valid
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (user != null) user.delete();
    }

    @Test(description = "Varianta 1: Java Standard - NU faceți asta!")
    public void varianta1_JavaStandard() throws Exception {
        // Cod greu de citit și întreținut
        URL url = URI.create(HEALTH_URL).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", user.apiKey);

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Validare manuală
        if (status != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + status);
        }
        System.out.println("Răspuns (Java standard): " + content);
    }

    @Test(description = "Varianta 2: REST Assured - Așa DA!")
    public void varianta2_RestAssured() {
        // Cod fluent, lizibil, care se ocupă de conexiune și validare
        given()
            .header("Authorization", user.apiKey)
        .when()
            .get(HEALTH_URL)
        .then()
            .statusCode(200);
    }
}
