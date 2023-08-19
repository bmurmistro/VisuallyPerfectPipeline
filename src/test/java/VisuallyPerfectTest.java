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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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

  private static VisualGridRunner runner;

  @BeforeClass
  public static void beforeClass() throws MalformedURLException {
    // Initialize the Runner for your test.
    runner = new VisualGridRunner(new RunnerOptions().testConcurrency(30));

    // Initialize the eyes SDK
    eyes = new Eyes(runner);

    eyes.setLogHandler(new StdoutLogHandler(true));
    // Set the AUT name
    sconf.setAppName("VisuallyPerfect");
    sconf.setTestName("Login Test");

    BatchInfo batch = new BatchInfo("VisuallyPerfect");
    sconf.setBatch(batch);
    sconf.setStitchMode(StitchMode.CSS);
    // Add Chrome browsers with different Viewports
    sconf.addBrowser(800, 600, BrowserType.CHROME);
    sconf.addBrowser(new IosDeviceInfo(IosDeviceName.iPhone_14));
    eyes.setConfiguration(sconf);

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setBrowserName("chrome");

    driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), caps);
  }

  @Test
  public void TestLoginFlow() throws InterruptedException {
    try {
      eyes.open(driver, sconf.getAppName(), sconf.getTestName());
      driver.get("http://demo.applitools.com");
      eyes.check(Target.window().fully().withName("login page"));

      //Simulate Self-Healing
      //((JavascriptExecutor)driver).executeScript("document.querySelector('#log-in').id='BlaBla_id'");

      driver.findElement(By.id("username")).sendKeys("brandon");
      driver.findElement(By.id("password")).sendKeys("mypasss");
      driver.findElement(By.id("log-in")).click();
      eyes.check(Target.window().fully().withName("dashboard"));

      eyes.closeAsync();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      eyes.abortAsync();
    }
  }

  @After
  public void afterTest(){
    eyes.abortIfNotClosed();
  }

  @AfterClass
  public static  void afterClass() {
    try {
      driver.quit();
      TestResultsSummary results = runner.getAllTestResults(false);

      System.out.println(results);
      BatchClose batchClose = new BatchClose();
      batchClose.setBatchId(Arrays.asList(sconf.getBatch().getId())).close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      eyes.abortAsync();
    }
  }
}
