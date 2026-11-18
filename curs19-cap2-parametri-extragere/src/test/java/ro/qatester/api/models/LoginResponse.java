package ro.qatester.api.models;

/**
 * POJO pentru răspunsul de la POST /user/login.php.
 * Răspuns real: {"status":"success","message":"Login successful","token":"eyJ...","user":{"id":"105","username":"...","email":"..."}}
 */
public class LoginResponse {
    private String status;
    private String message;
    private String token;
    private UserDetails user;

    // Getteri (Jackson folosește câmpurile private pentru deserializare)
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserDetails getUser() { return user; }

    // Clasă internă pentru obiectul "user" din JSON
    public static class UserDetails {
        private int id; // În JSON id-ul vine ca String ("105"); Jackson îl convertește automat în int
        private String username;
        private String email;

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }
}
