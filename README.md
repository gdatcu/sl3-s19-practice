# Curs 19 – REST Assured: proiectele Maven pentru Capitolele 1–5

Codul din materialul „Curs 19 – Material auxiliar”, organizat câte un proiect Maven pe capitol,
adaptat la mediul migrat: **https://apps.qualiadept.eu/hapifyme/api**
(Swagger: https://apps.qualiadept.eu/hapifyme/api-docs/index.html)

| Proiect | Capitol | Clase de test |
|---|---|---|
| curs19-cap1-introducere-gwt | 1. Introducere, GWT | JavaStandardVsRestAssuredTest, SystemHealthTest |
| curs19-cap2-parametri-extragere | 2. Parametri, JsonPath, POJO | ParametersTest, JsonPathExtractionTest, TokenExtractionTest, LoginTestSimple, LoginTestPOJO, Chapter2ExercisesTest |
| curs19-cap3-post-pojo | 3. POST și serializare POJO | StringJsonBodyTest, HapifyMeLoginTest, HapifyMeRegisterTest, Chapter3ExercisesTest |
| curs19-cap4-crud-chaining | 4. CRUD și Chaining | GetProfileTest, EndToEndUserFlowTest, LoginOnlyTest, RevokeApiKeyTest |
| curs19-cap5-bdd-datadriven | 5. BDD, DataProvider, Cucumber | LoginDataDrivenTest, MyTest, NegativeLoginTest, CucumberRunnerTest (login.feature) |

## Deschidere în IntelliJ
1. **File → Open** → folderul `curs19-capitolele-1-5` (încarcă toate cele 5 capitole ca module Maven).
   Sau deschide doar folderul unui capitol – fiecare este un proiect independent.
2. Dacă nu ai JDK: **File → Project Structure → SDK → Download JDK** (17 sau mai nou).
3. Click dreapta pe `src/test/resources/testng.xml` din capitol → **Run**.

## Rulare din terminal
```
mvn test                                  # din folderul rădăcină: toate capitolele
mvn test -pl curs19-cap4-crud-chaining    # un singur capitol
```

## Ce s-a schimbat față de codul din material (și de ce)
După migrare, codul din document nu mai rulează așa cum este scris:

1. **API key-urile hardcodate nu mai sunt valide** (`401 Invalid or inactive API key.`), iar conturile
   `test_qa`, `john1765038126663_doe1765038126663`, `john1764512276895_doe1764512276895` nu mai există.
   → Clasa `support/TestUser` înregistrează un utilizator nou (POST /user/register.php), oferă API key-ul,
   username-ul și user_id-ul primite și **șterge contul la final** (login → Bearer → DELETE).
2. **GET /user/get_profile.php cere parametrul `user_id`** (lipsește din Swagger). Fără el, răspunsul este
   `200` cu `{"status":"error","message":"User ID is required."}`.
3. **RegisterResponse nu avea câmpul `username`**, dar Capitolul 4 apelează `registerResponse.getUsername()`
   (eroare de compilare). → Câmp adăugat.
4. **LoginRequest.setUsername(String email)** atribuia `this.username = username` (parametrul greșit). → Corectat.
5. Parola de la înregistrare trebuie să aibă min. 8 caractere, majusculă, minusculă, cifră și caracter special.
6. Login-ul acceptă **doar username-ul** (cu email-ul răspunde 401).
7. Mesajul de login este **„Login successful”** – nu conține „successfully” (Exercițiul 2 din 3.7).
8. GET pe /user/login.php răspunde **400**, nu 405; un JSON invalid primește **500** (pagină HTML), nu 400.
9. Capitolul 4: email-ul din update este unic la fiecare rulare și am adăugat un GET profile după update
   (verifică efectiv modificarea) și un al doilea DELETE (404 – idempotency).
10. Capitolul 5 (Cucumber): scenariul valid creează propriul user (`Given a new user is registered`);
    am adăugat și un scenariu negativ. Lombok din 2.4 nu este folosit (POJO-uri cu getteri).

## Atenție
- Testele rulează pe serverul live și creează utilizatori temporari (sunt șterși la final).
- `get_profile.php` returnează profilul **oricărui** user_id cu orice API key valid (problemă de securitate
  de tip IDOR) – nu folosiți date personale reale pe acest mediu.
