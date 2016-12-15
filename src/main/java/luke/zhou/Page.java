package luke.zhou;

import luke.zhou.model.travian.BuildingStatus;
import luke.zhou.model.travian.Game;
import luke.zhou.model.travian.Resource;
import luke.zhou.model.travian.Village;
import luke.zhou.util.IntegerUtil;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
        if (browerDriver.equals("FF"))
        {
            String systemDriver;
            if (System.getProperty("os.name").toLowerCase().contains("windows"))
            {
                systemDriver = "browser-driver/geckodriver.exe";
            }
            else
            {
                systemDriver = "browser-driver/geckodriver";
            }

            System.setProperty("webdriver.gecko.driver", systemDriver);

            driver = new FirefoxDriver();
            Capabilities exitingCaps = ((FirefoxDriver) driver).getCapabilities();
            System.out.println("logging prefs:" + exitingCaps.getCapability(CapabilityType.LOGGING_PREFS));

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

    public void click(WebElement element)
    {
        element.click();
        afterActionWait();
    }

    public void exit()
    {
        driver.close();
    }

    public String getText(String xpath)
    {
        return driver.findElements(By.xpath(xpath)).size() > 0 ?
                driver.findElement(By.xpath(xpath)).getText() : null;
    }

    public Integer getInt(String xpath)
    {
        return driver.findElements(By.xpath(xpath)).size() > 0 ?
                IntegerUtil.convertInteger(driver.findElement(By.xpath(xpath)).getText()) : null;
    }

    public WebDriver loadURL(String url)
    {
        LOG.debug("url:" + url);
        if (!url.contains("http"))
        {
            driver.get(server + "/" + url);
        }
        else
        {
            driver.get(url);
        }
        afterActionWait();

        return driver;
    }

    public void submit(WebElement webElement)
    {
        webElement.submit();
        afterActionWait();
    }

    private void afterActionWait()
    {
        try
        {
            Thread.sleep(RandomUtil.randomRange(TimeUtil.seconds(5), TimeUtil.seconds(10)));
        } catch (InterruptedException e)
        {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void evadeTroops()
    {
        loadURL("build.php?tt=2&id=39");
        List<WebElement> tds = driver.findElements(By.xpath("//table[@id='troops']//td"));
        tds.stream().forEach(td -> {
            if (td.findElements(By.tagName("a")).size() > 0)
            {
                click(td.findElement(By.tagName("a")));
            }
        });

        driver.findElement(By.id("xCoordInput")).sendKeys("-3");
        driver.findElement(By.id("yCoordInput")).sendKeys("43");

        driver.findElement(By.xpath("//input[@class='radio'][@name='c'][@value='2']")).click();
        click(driver.findElement(By.id("btn_ok")));
        click(driver.findElement(By.id("btn_ok")));
        loadURL("dorf1.php");

    }

    public void withdrawTroops()
    {
        loadURL("build.php?tt=1&id=39");
        if (driver.findElements(By.xpath("//a[text()='withdraw']")).size() > 0)
        {
            driver.findElements(By.xpath("//a[text()='withdraw']")).stream().forEach(e -> {
                click(e);
                click(driver.findElement(By.id("btn_ok")));
            });
        }

        loadURL("dorf1.php");

    }

    public void attack(Village village, int x, int y)
    {
        loadURL(village.getHomeLink());
        loadURL("build.php?tt=2&id=39");


        WebElement tab = driver.findElement(By.xpath("//a[@href='build.php?tt=2&id=39']"));
        Actions openNewTab = new Actions(driver)
                .keyDown(Keys.COMMAND)
                .keyDown(Keys.SHIFT)
                .click(tab)
                .keyUp(Keys.SHIFT)
                .keyUp(Keys.COMMAND);
        openNewTab.perform();


        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        System.out.println(tabs);
        driver.switchTo().window(tabs.get(1));


//        List<WebElement> tds = driver.findElements(By.xpath("//table[@id='troops']//td"));
//        tds.stream().forEach(td -> {
//            if (td.findElements(By.tagName("img")).size() > 0)
//            {
//                String troopType = td.findElement(By.tagName("img")).getAttribute("alt");
//                if (troopType.equals("Clubswinger") )
//                {
//                    td.findElement(By.tagName("input")).sendKeys("10");
//                }
//            }
//        });
//        loadURL(village.getLink());
//        loadURL("build.php?tt=2&id=39");
//        List<WebElement> tds2 = driver.findElements(By.xpath("//table[@id='troops']//td"));
//        tds2.stream().forEach(td -> {
//            if (td.findElements(By.tagName("img")).size() > 0)
//            {
//                String troopType = td.findElement(By.tagName("img")).getAttribute("alt");
//                if (troopType.equals("Clubswinger") )
//                {
//                    td.findElement(By.tagName("input")).sendKeys("20");
//                }
//            }
//        });

//        driver.findElement(By.id("xCoordInput")).sendKeys(String.valueOf(x));
//        driver.findElement(By.id("yCoordInput")).sendKeys(String.valueOf(y));
//
//        driver.findElement(By.xpath("//input[@class='radio'][@name='c'][@value='3']")).click();
//        click(driver.findElement(By.id("btn_ok")));
//        click(driver.findElement(By.id("btn_ok")));
//        loadURL("dorf1.php");
    }

    public void build(Village village, Resource resource)
    {
        loadURL(village.getHomeLink());
        loadURL("build.php?id=" + resource.getLocation());
        click(driver.findElement(By.xpath("//div[@class='showBuildCosts normal']/button")));
    }

    public String transferCrop(Village village)
    {
        String result;
        loadURL(village.getHomeLink());
        loadURL("build.php?t=5&id=35");
        if (!driver.findElement(By.id("merchantCapacityValue")).getText().equals("0"))
        {
            driver.findElement(By.xpath("//input[@id='r4']")).sendKeys("1000");
            driver.findElement(By.xpath("//input[@id='xCoordInput']")).sendKeys("-40");
            driver.findElement(By.xpath("//input[@id='yCoordInput']")).sendKeys("-9");

            submit(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            click(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            result = driver.findElement(By.xpath("//p[@id='note']")).getText();
        }
        else
        {
            result = "Don't have merchant at home";
        }

        loadURL("dorf1.php");
        return result;
    }

    public String transferResource(Village from, Village to)
    {
        String result;
        loadURL(from.getHomeLink());
        loadURL("build.php?t=5&id=35");
        if (!driver.findElement(By.id("merchantCapacityValue")).getText().equals("0"))
        {
            int capacity = Integer.valueOf(driver.findElement(By.xpath("//div[@id='build']/div[@class='carry']/b")).getText());
            driver.findElement(By.xpath("//input[@id='r1']")).sendKeys(String.valueOf((int) capacity * 0.3));
            driver.findElement(By.xpath("//input[@id='r2']")).sendKeys(String.valueOf((int) capacity * 0.4));
            driver.findElement(By.xpath("//input[@id='r3']")).sendKeys(String.valueOf((int) capacity * 0.3));
            driver.findElement(By.xpath("//input[@id='enterVillageName']")).sendKeys(to.getName());

            submit(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            click(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            result = driver.findElement(By.xpath("//p[@id='note']")).getText();
        }
        else
        {
            result = "Don't have merchant at home";
        }
        loadURL("dorf1.php");
        return result;
    }

    public WebDriver getPageResult()
    {
        return driver;
    }
}
