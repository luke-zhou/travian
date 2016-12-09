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
import org.openqa.selenium.interactions.Action;
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

    public void getInfo(Game game)
    {
        game.getVillages().stream().forEach(v -> {
            loadURL(v.getLink());
            v.setWarehouseCapacity(IntegerUtil.convertInteger(getText("//span[@id='stockBarWarehouse']")));
            v.setGranaryCapacity(IntegerUtil.convertInteger(getText("//span[@id='stockBarGranary']")));
            v.setLumber(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource1']//span[@id='l1']")));
            v.setClay(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource2']//span[@id='l2']")));
            v.setIron(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource3']//span[@id='l3']")));
            v.setCrop(IntegerUtil.convertInteger(getText("//ul[@id='stockBar']/li[@id='stockBarResource4']//span[@id='l4']")));
            for (int i = 0; i < 18; i++)
            {
                v.getResources()[i] = getResource(i);
            }
        });
    }

    public void cleanupMessage()
    {
        loadURL("berichte.php?t=1");
        List<WebElement> filters = driver.findElements(By.className("iconFilter"));
        if (!filters.get(0).getAttribute("class").contains("iconFilterActive"))
        {
            click(filters.get(0));
        }

        filters = driver.findElements(By.className("iconFilter"));
        if (filters.get(1).getAttribute("class").contains("iconFilterActive"))
        {
            click(filters.get(1));
        }

        filters = driver.findElements(By.className("iconFilter"));
        if (filters.get(2).getAttribute("class").contains("iconFilterActive"))
        {
            click(filters.get(2));
        }

        while (driver.findElements(By.xpath("//table[@id='overview']/tbody/tr/td[@class='noData']")).size() == 0)
        {
            click(driver.findElement(By.id("sAll2")));
            click(driver.findElement(By.id("del")));
        }
    }

    public void openMap()
    {
        loadURL("karte.php");
        loadURL("dorf1.php");
    }


    private Resource getResource(int i)
    {
        WebElement resourceMapWE = driver.findElement(By.xpath("//div[@id='content']//area[@href='build.php?id=" + (i + 1) + "']"));
        String href = resourceMapWE.getAttribute("href");
        String alt = resourceMapWE.getAttribute("alt");
        String[] results = alt.split("Level");
        Resource resource = new Resource(Resource.ResourceType.get(results[0].trim()),
                href, Integer.valueOf(results[1].trim()), i+1);
        List<WebElement> resourceWEs = driver.findElements(By.xpath("//div[@id='village_map']/div"));
        for (BuildingStatus status : BuildingStatus.values())
        {
            if (resourceWEs.get(i).getAttribute("class").contains(status.getValue()))
            {
                resource.addStatus(status);
            }
        }

        return resource;

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



    private void click(WebElement element)
    {
        element.click();
        afterActionWait();
    }

    public String sendRaidAllInList(Game game)
    {
        loadURL(game.getVillages().get(0).getLink());
        String raidAllResult = raidAllInList("123") + "\n" + raidAllInList("173");

        return raidAllResult;
    }

    public String sendRaidOneInList(int index)
    {
        return raidOneInList("181", index);
    }


    private String raidOneInList(String listId, int index)
    {
        loadURL("build.php?tt=99&id=39");
        List<WebElement> items = driver.findElements(By.xpath("//div[@id='list" + listId + "']//tr[@class='slotRow']"));
        String villageName = items.get(index).findElement(By.className("village")).findElement(By.tagName("a")).getText();
        WebElement checkboxWE = items.get(index).findElement(By.tagName("input"));
        checkboxWE.click();
        submit(checkboxWE);

        String result = driver.findElement(By.id("list" + listId)).findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();
        if (result.equals("0 raids have been made."))
        {
            return "Not enough troops for " + villageName;
        }
        else
        {
            return "Send raid to " + villageName;
        }
    }

    private String raidAllInList(String listId)
    {
        loadURL("build.php?tt=99&id=39");
        WebElement selectAll1 = driver.findElement(By.id("raidListMarkAll" + listId));
        selectAll1.click();
        submit(selectAll1);
        return driver.findElement(By.id("list" + listId)).findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();

    }

    public void home(Game game)
    {
        loadURL("dorf1.php");
        WebElement villageWE = driver.findElement(
                By.xpath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']/ul"));
        game.getVillages().clear();
        villageWE.findElements(By.tagName("li")).forEach(we -> {
            Village village = new Village(we.findElement(By.className("name")).getText());
            village.setUnderAttack(we.getAttribute("class").contains("attack"));
            village.setLink(we.findElement(By.tagName("a")).getAttribute("href"));
            game.addVillage(village);
        });

        LOG.debug("Village size:" + game.getVillages().size());
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
    }

    private void submit(WebElement webElement)
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
        loadURL(village.getLink());
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
        loadURL(village.getLink());
        loadURL("build.php?id="+resource.getId());
        click(driver.findElement(By.xpath("//div[@class='showBuildCosts normal']/button")));
    }

    public String transferCrop(Village village)
    {
        String result;
        loadURL(village.getLink());
        loadURL("build.php?t=5&id=35");
        if(!driver.findElement(By.id("merchantCapacityValue")).getText().equals("0"))
        {
            driver.findElement(By.xpath("//input[@id='r4']")).sendKeys("1000");
            driver.findElement(By.xpath("//input[@id='xCoordInput']")).sendKeys("-40");
            driver.findElement(By.xpath("//input[@id='yCoordInput']")).sendKeys("-9");

            submit(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            click(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            result =driver.findElement(By.xpath("//p[@id='note']")).getText();
        }
        else {
            result ="Don't have merchant at home";
        }

        loadURL("dorf1.php");
        return result;
    }

    public String transferResource(Village from, Village to)
    {
        String result;
        loadURL(from.getLink());
        loadURL("build.php?t=5&id=35");
        if(!driver.findElement(By.id("merchantCapacityValue")).getText().equals("0"))
        {
            int capacity = Integer.valueOf(driver.findElement(By.xpath("//div[@id='build']/div[@class='carry']/b")).getText());
            driver.findElement(By.xpath("//input[@id='r1']")).sendKeys(String.valueOf((int)capacity*0.3));
            driver.findElement(By.xpath("//input[@id='r2']")).sendKeys(String.valueOf((int)capacity*0.4));
            driver.findElement(By.xpath("//input[@id='r3']")).sendKeys(String.valueOf((int)capacity*0.3));
            driver.findElement(By.xpath("//input[@id='enterVillageName']")).sendKeys(to.getName());

            submit(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            click(driver.findElement(By.xpath("//button[@id='enabledButton']")));
            result= driver.findElement(By.xpath("//p[@id='note']")).getText();
        }
        else{
            result ="Don't have merchant at home";
        }
        loadURL("dorf1.php");
        return result;
    }
}
