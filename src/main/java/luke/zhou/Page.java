package luke.zhou;

import luke.zhou.model.travian.Game;
import luke.zhou.model.travian.TroopMovement;
import luke.zhou.model.travian.Village;
import luke.zhou.util.IntegerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;

import java.util.logging.Level;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 10:25 AM
 */
public class Page
{
    private static final Logger LOG = LoggerFactory.getLogger(Page.class);

    private WebDriver driver;
    private String server;

    public Page(String server)
    {
        String browerDriver = "chrome";
        if (browerDriver.equals("FF")){
            String systemDriver;
            if(System.getProperty("os.name").toLowerCase().contains("windows")){
                systemDriver = "browser-driver/geckodriver.exe";
            }
            else{
                systemDriver = "browser-driver/geckodriver";
            }

            System.setProperty("webdriver.gecko.driver", systemDriver);

            driver = new FirefoxDriver();
            Capabilities exitingCaps =((FirefoxDriver)driver).getCapabilities();
            System.out.println("logging prefs:"+exitingCaps.getCapability(CapabilityType.LOGGING_PREFS));

        }
        else
        {
            String systemDriver;
            if (System.getProperty("os.name").toLowerCase().contains("windows"))
            {
                systemDriver = "browser-driver/chromedriver.exe";
            }
            else
            {
                systemDriver = "browser-driver/chromedriver";
            }

            System.setProperty("webdriver.chrome.driver", systemDriver);
            driver = new ChromeDriver();
        }
        this.server = server;
    }

    public void getInfo(Game game)
    {
         game.getVillages().stream().forEach(v->{
             loadURL(v.getLink());
             v.setLumber(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource1']//span[@id='l1']")));
             v.setClay(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource2']//span[@id='l2']")));
             v.setIron(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource3']//span[@id='l3']")));
             v.setCrop(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource4']//span[@id='l4']")));
         } );
    }

    public void login(String username, String password)
    {
        loadURL("");

        WebElement nameWE = driver.findElement(By.name("name"));
        nameWE.sendKeys(username);
        WebElement passwordWE = driver.findElement(By.name("password"));
        passwordWE.sendKeys(password);

        // Now submit the form. WebDriver will find the form for us from the element
        LOG.debug("Page title is: " + driver.getTitle());
        submit(passwordWE);
    }

    public String sendRaid()
    {
        loadURL("build.php?tt=99&id=39");
        WebElement selectAll = driver.findElement(By.id("raidListMarkAll123"));
        selectAll.click();
        submit(selectAll);

        return driver.findElement(By.id("list123")).findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();
    }

    public void home(Game game)
    {
        loadURL("dorf1.php");
        WebElement villageWE = driver.findElement(
                By.xpath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']/ul"));
        game.getVillages().clear();
        villageWE.findElements(By.tagName("li")).forEach(we ->{
            Village village = new Village(we.findElement(By.className("name")).getText());
            village.setUnderAttack(we.getAttribute("class").contains("attack"));
            village.setLink(we.findElement(By.tagName("a")).getAttribute("href"));
            game.addVillage(village);
        });

        LOG.debug("Village size:"+game.getVillages().size());
    }

    public void exit()
    {
        driver.close();
    }

    private String getText(String xpath)
    {
        return driver.findElements(By.xpath(xpath)).size() > 0 ?
                driver.findElement(By.xpath(xpath)).getText() : null;
    }

    private void loadURL(String url)
    {
        LOG.debug("url:"+url);
        if (!url.contains("http")) {
            driver.get(server+"/"+url);
        }
        else{
            driver.get(url);
        }
        afterActionWait();
    }

    private void submit(WebElement webElement)
    {
        webElement.submit();
        afterActionWait();
    }

    private void afterActionWait(){
        try
        {
            Thread.sleep(RandomUtil.randomRange(TimeUtil.seconds(5), TimeUtil.seconds(10)));
        }
        catch (InterruptedException e)
        {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
