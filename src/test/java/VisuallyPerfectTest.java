import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.fluent.BatchClose;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.IosDeviceInfo;
import com.applitools.eyes.visualgrid.model.IosDeviceName;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.junit.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class VisuallyPerfectTest
{
  private static Eyes eyes;
  static Configuration sconf = new Configuration();

  private static WebDriver driver;

  private static VisualGridRunner runner = new VisualGridRunner(new RunnerOptions().testConcurrency(2));
  private static BatchInfo batch = new BatchInfo("VisuallyPerfect");

  @BeforeAll
  public static void beforeAll() throws MalformedURLException {
    sconf.setBatch(batch);
    // Add Chrome browsers with different Viewports
    sconf.addBrowser(800, 600, BrowserType.CHROME);
    sconf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_14));
  }

  @BeforeEach
  public void beforeTest(TestInfo testInfo) throws MalformedURLException {
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setBrowserName("chrome");
    driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), caps);

    // Initialize the eyes SDK
    eyes = new Eyes(runner);
    //eyes.setLogHandler(new StdoutLogHandler(true));
    eyes.setConfiguration(sconf);
    eyes.open(driver, "Visually Perfect", testInfo.getDisplayName());
  }

  @Test
  public void loginFlow() {
    driver.get("http://demo.applitools.com");
    eyes.check(Target.window().fully().withName("login page"));

    //Simulate Self-Healing
    //((JavascriptExecutor)driver).executeScript("document.querySelector('#log-in').id='BlaBla_id'");

    driver.findElement(By.id("username")).sendKeys("brandon");
    driver.findElement(By.id("password")).sendKeys("mypasss");
    driver.findElement(By.id("log-in")).click();
    eyes.check(Target.window().fully().withName("dashboard"));
  }

  @AfterEach
  public void cleanUpTest() {
    // Close Eyes to tell the server it should display the results.
    eyes.closeAsync();

    // Quit the WebDriver instance.
    driver.quit();

    // Warning: `eyes.closeAsync()` will NOT wait for visual checkpoints to complete.
    // You will need to check the Eyes Test Manager for visual results per checkpoint.
    // Note that "unresolved" and "failed" visual checkpoints will not cause the JUnit test to fail.

    // If you want the JUnit test to wait synchronously for all checkpoints to complete see comments in printResults
    // for more details
  }

  @AfterAll
  public static void printResults() {

    // Close the batch and report visual differences to the console.
    // Note that it forces JUnit to wait synchronously for all visual checkpoints to complete.
    // getAllTestResults by default will throw an exception if there are diffs. Since this project is intended for the
    // github integration, we pass in false so that we don't fail the pipeline. We use the github pull request checks
    // as the quality gate when using the github integration.
    // Note if we were not using the github integration and we wanted to fail the individual test and not the entire
    // class, you can remove this block and add runner.getAllTestResults(); to the end of cleanUpTest.
    TestResultsSummary allTestResults = runner.getAllTestResults(false);
    System.out.println(allTestResults);
  }
}
