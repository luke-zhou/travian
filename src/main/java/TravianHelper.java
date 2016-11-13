import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by Luke on 13/11/16.
 */
public class TravianHelper
{
    private static final Logger LOG = LoggerFactory.getLogger(TravianHelper.class);

    private WebDriver driver;

    public TravianHelper()
    {
        System.setProperty("webdriver.chrome.driver", "browser-driver/chromedriver");
        driver = new ChromeDriver();
    }

    public void start() throws InterruptedException
    {

        // And now use this to visit Google
        driver.get("http://ts4.travian.com.au/");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        WebElement name = driver.findElement(By.name("name"));
        name.sendKeys("tdxh20");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("Carnegie71");

        // Now submit the form. WebDriver will find the form for us from the element
        System.out.println("Page title is: " + driver.getTitle());
        password.submit();

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 10; i++)
        {
            driver.get("http://ts4.travian.com.au/build.php?tt=99&id=39");
            WebElement selectAll = driver.findElement(By.id("raidListMarkAll123"));
            Thread.sleep(5 * 1000);
            selectAll.click();
            selectAll.submit();

            String result = driver.findElement(By.id("list123")).findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();
            LOG.info("Start Raid: " + result);
            driver.get("http://ts4.travian.com.au/dorf1.php");
            random.nextInt(20 * 1000);
            Thread.sleep(30 * 60 * 1000 + (random.nextInt(20 * 1000) - 10 * 1000));

        }

        // Check the title of the page

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        // (new WebDriverWait(driver, 10)).until(d -> d.getTitle().toLowerCase().startsWith("cheese!"));
//        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>()
//        {
//            public Boolean apply(WebDriver d)
//            {
//                return d.getTitle().toLowerCase().startsWith("cheese!");
//            }
//        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        Thread.sleep(10 * 1000);
        //Close the browser
        driver.quit();
    }
}
