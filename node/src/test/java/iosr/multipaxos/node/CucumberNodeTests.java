package iosr.multipaxos.node;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features", format = {"pretty", "json:target/cucumber-html-reports/testresults.json"})
public class CucumberNodeTests {

    @BeforeClass
    public static void setup() {
        ServerEnvironment.start();
    }

    @AfterClass
    public static void tearDown() {
        ServerEnvironment.shutdown();
    }
}