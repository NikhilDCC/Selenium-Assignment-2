import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v119.browser.Browser;
import org.openqa.selenium.devtools.v119.network.Network;
import org.openqa.selenium.devtools.v119.network.model.Request;
import org.openqa.selenium.devtools.v119.network.model.Response;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.locators.RelativeLocator;
import java.util.Map;
import java.util.Optional;


public class Assignment1 {
    public static void main(String[] args) {
        ChromeDriver driver = null;
        try {
            //Chrome Options instead of DesiredCapabilities in Selenium 3
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            options.addArguments("-–disable-notifications");
            options.addArguments("---ignore-certificate-errors");
            options.setBrowserVersion("119");

            //Selenium Manager in Selenium 4
            driver = new ChromeDriver(options);

            //CDP or Chrome Dev tools in Selenium 4
            DevTools devTool = driver.getDevTools();
            devTool.createSession();

            //Relative Locators
            driver.get("http://zero.webappsecurity.com/login.html");

            WebElement getADemo = driver.findElement(By.id("user_login"));
            WebElement getADemo1 = driver.findElement(RelativeLocator.RelativeBy.id("user_login"));
            getADemo1.sendKeys("HEllo");
            WebElement pwdField1 = driver.findElement(By.id("user_password"));

            WebElement labelUsername = driver.findElement(RelativeLocator.with(By.tagName("label")).toLeftOf(getADemo));
            WebElement pwdField = driver.findElement(RelativeLocator.with(By.id("user_password")).below(getADemo));
            pwdField.sendKeys("Test password");
            System.out.println(labelUsername.getText());

            WebElement checkRememberMe = driver.findElement(RelativeLocator.with(By.id("user_remember_me")).toRightOf(By.xpath("//label[@for='user_remember_me']")));
            checkRememberMe.click();

            WebElement loginUsername = driver.findElement(RelativeLocator.with((By.id("user_login"))).near(By.id("user_password")));
            loginUsername.sendKeys("Near");

            WebElement loginBtn = driver.findElement(RelativeLocator.with(By.xpath("//input[@name='submit']")).below(By.id("user_remember_me")));

            //Action Method additions in Selenium 4 vs 3
            Actions action = new Actions(driver);
            action.clickAndHold(loginBtn).perform();
            action.contextClick(loginBtn).perform();
            action.click(loginBtn).perform();
            action.doubleClick().perform();


            // CDP tools

            driver.get("https://weatherstack.com/");
       /* devTool.send(Emulation.setDeviceMetricsOverride(

                500,

                600,

                50,

                true,

                Optional.empty(),

                Optional.empty(),

                Optional.empty(),

                Optional.empty(),

                Optional.empty(),

                Optional.empty(),

                Optional.empty(),

                Optional.empty(),

                Optional.empty()

        ));
*/
            driver.executeCdpCommand("Emulation.setDeviceMetricsOverride", Map.of(

                    "width", 500,

                    "height", 600,

                    "deviceScaleFactor", 50,

                    "mobile", true

            ));


            //Browser version
            Browser.GetVersionResponse browser = devTool.send(Browser.getVersion());
            System.out.println("Browser Version => " + browser.getProduct());
            System.out.println("User Agent => " + browser.getUserAgent());

            //GeoLocation Override
            Map coordinates = Map.of(
                    "latitude", 30.3079823,
                    "longitude", -97.893803,
                    "accuracy", 1
            );
            driver.executeCdpCommand("Emulation.setGeolocationOverride", coordinates);
            //Refresh if already on the page
            driver.navigate().refresh();

            //Network Tools
            devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            devTool.addListener(Network.requestWillBeSent(), requestConsumer -> {
                Request request = requestConsumer.getRequest();
                System.out.println(request.getUrl());
            });


            devTool.addListener(Network.responseReceived(), responseConsumer -> {
                Response response = responseConsumer.getResponse();
                if (response.getUrl().contains("ws_api.php?")) {
                    System.out.println(response.getStatus() + " " + response.getUrl());
                    Integer value = 200;
                    Assert.assertEquals("NOT EQUAL!", value, response.getStatus());
                    System.out.println("End of response");
                }
            });

        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();
        } finally {
            // Close the WebDriver instance
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
