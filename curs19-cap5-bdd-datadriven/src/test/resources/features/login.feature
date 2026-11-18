Feature: Login Functionality on hapifyMe

  Scenario: Login with valid user
    Given I define the base URL as "https://apps.qualiadept.eu/hapifyme/api"
    And a new user is registered
    And I prepare a login request for the registered user
    When I send a POST request to "/user/login.php"
    Then I receive a 200 status code
    And I receive a successful login message

  Scenario: Login with invalid credentials
    Given I define the base URL as "https://apps.qualiadept.eu/hapifyme/api"
    And I prepare a login request for user "test_qa" with password "parola_gresita"
    When I send a POST request to "/user/login.php"
    Then I receive a 401 status code
    And I receive the error message "Invalid username or password"
