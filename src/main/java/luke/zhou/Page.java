package luke.zhou;

import luke.zhou.model.travian.Game;
import luke.zhou.model.travian.TroopMovement;
import luke.zhou.model.travian.Village;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;

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
        String systemDriver;
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            systemDriver = "browser-driver/chromedriver.exe";
        }
        else{
            systemDriver = "browser-driver/chromedriver";
        }

        System.setProperty("webdriver.chrome.driver", systemDriver);
        driver = new ChromeDriver();
        this.server = server;
    }

    public void login(String username, String password)
    {
        loadURL(server);

        WebElement nameWE = driver.findElement(By.name("name"));
        nameWE.sendKeys(username);
        WebElement passwordWE = driver.findElement(By.name("password"));
        passwordWE.sendKeys(password);

        // Now submit the form. WebDriver will find the form for us from the element
        LOG.debug("Page title is: " + driver.getTitle());
        passwordWE.submit();
    }

    public String sendRaid()
    {
        loadURL(server + "build.php?tt=99&id=39");
        WebElement selectAll = driver.findElement(By.id("raidListMarkAll123"));
        selectAll.click();
        selectAll.submit();

        return driver.findElement(By.id("list123")).findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();
    }

    public void home(Game game)
    {
        loadURL(server + "dorf1.php");
        WebElement villageWE = driver.findElement(
                By.xpath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']/ul"));
        villageWE.findElements(By.tagName("li")).forEach(we ->{
            Village village = new Village(we.findElement(By.className("name")).getText());
            village.setUnderAttack(we.getAttribute("class").contains("attack"));
            village.setLink("dorf1.php"+we.findElement(By.tagName("a")).getAttribute("href"));
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
        driver.get(url);
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
