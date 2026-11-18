package ro.qatester.api.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/** 5.4 Rulează fișierele .feature ca teste TestNG */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "steps",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class CucumberRunnerTest extends AbstractTestNGCucumberTests {
}
