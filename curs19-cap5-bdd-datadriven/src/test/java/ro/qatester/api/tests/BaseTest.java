package ro.qatester.api.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

/** 5.3 Refolosirea Specificațiilor (Request/Response Specs) */
public class BaseTest {
    protected static RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
        requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://apps.qualiadept.eu/hapifyme/api")
            .setContentType("application/json")
            .addHeader("Accept", "application/json")
            .build();
    }
}
