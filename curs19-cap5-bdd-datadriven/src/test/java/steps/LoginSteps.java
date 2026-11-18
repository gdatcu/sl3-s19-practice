package steps;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ro.qatester.api.models.LoginRequest;
import ro.qatester.api.support.TestUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

/** 5.4.2 Pasul 2: Step Definitions ("lipiciul" dintre Gherkin și REST Assured) */
public class LoginSteps {

    private RequestSpecification request;
    private Response response;
    private TestUser registeredUser;   // userul creat de scenariu (pentru cleanup)

    @Given("I define the base URL as {string}")
    public void setBaseUrl(String url) {
        request = given().baseUri(url).contentType("application/json");
    }

    @Given("a new user is registered")
    public void registerNewUser() {
        // Contul "test_qa" din material nu mai există: creăm un user nou pentru scenariu
        registeredUser = TestUser.register();
    }

    @Given("I prepare a login request for the registered user")
    public void prepareLoginForRegisteredUser() {
        request.body(new LoginRequest(registeredUser.username, registeredUser.password));
    }

    @Given("I prepare a login request for user {string} with password {string}")
    public void prepareLoginBody(String user, String pass) {
        LoginRequest loginBody = new LoginRequest(user, pass);
        request.body(loginBody);
    }

    @When("I send a POST request to {string}")
    public void sendPostRequest(String endpoint) {
        response = request.when().post(endpoint);
    }

    @Then("I receive a {int} status code")
    public void verifyStatusCode(int code) {
        response.then().statusCode(code);
    }

    @Then("I receive a successful login message")
    public void verifyLoginMessage() {
        response.then().body("message", is("Login successful"));
    }

    @Then("I receive the error message {string}")
    public void verifyErrorMessage(String message) {
        response.then().body("status", is("error")).body("message", is(message));
    }

    @After
    public void cleanup() {
        if (registeredUser != null) registeredUser.delete();
    }
}
