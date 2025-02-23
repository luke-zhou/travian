package luke.zhou.model.travian;

import luke.zhou.Page;
import luke.zhou.util.RandomUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 4:03 PM
 */
public class Game
{
    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private static final String SERVER = "http://ts4.travian.com.au";

    private Boolean alarmOn;
    private Boolean autoRaid;
    private Boolean autoBuild;
    private int autoRepeatCount;
    private String username;
    private String password;
    private String notificationEmail;
    private Date date;


    private List<Village> villages;

    private List<FarmList> farmLists;

    public Game()
    {
        this.alarmOn = true;
        autoRaid = false;
        autoBuild = false;
        this.villages = new ArrayList<>();
        farmLists = new ArrayList<>();
    }

    public void login(Page page)
    {
        WebDriver pageResult = page.loadURL("");

        WebElement nameWE = pageResult.findElement(By.name("name"));
        nameWE.sendKeys(username);
        WebElement passwordWE = pageResult.findElement(By.name("password"));
        passwordWE.sendKeys(password);

        page.submit(passwordWE);
        load(page);
    }

    public void load(Page page)
    {
        WebDriver pageResult = page.loadURL("dorf1.php");
        WebElement villageWE = pageResult.findElement(
                By.xpath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']/ul"));
        villageWE.findElements(By.tagName("li")).forEach(we -> {
            String villageName = we.findElement(By.className("name")).getText();
            Village village = getVillage(villageName);
            if (village == null)
            {
                village = new Village(villageName);
                villages.add(village);
            }
                village.setAutoBuild(village.getName().toLowerCase().contains("zdjz"));
            village.setUnderAttack(we.getAttribute("class").contains("attack"));
            String link = we.findElement(By.tagName("a")).getAttribute("href");
            village.setNewdid(Integer.valueOf(link.substring(link.indexOf("=") + 1, link.length() - 1)));
        });

        LOG.debug("Village size:" + villages.size());

        villages.stream().forEach(v -> v.load(page));

        loadFarmList(page);

        LOG.debug("Farmlist size:" + farmLists.size());
    }

    public boolean checkUnderAttack(Page page)
    {
        WebDriver pageResult = page.loadURL("dorf1.php");
        WebElement villageWE = pageResult.findElement(
                By.xpath("//div[@id='sidebarBoxVillagelist']//div[@class='innerBox content']/ul"));
        return villageWE.findElements(By.tagName("li")).stream().anyMatch(we -> we.getAttribute("class").contains("attack"));
    }

    public void loadFarmList(Page page)
    {
        Village village = villages.stream().filter(v -> v.hasRallyPoint()).findFirst().get();
        page.loadURL(village.getHomeLink());
        WebDriver pageResult = page.loadURL("build.php?tt=99&id=39");
        List<WebElement> raidListWEs = pageResult.findElements(By.xpath("//div[@id='raidList']/div[@class='listEntry']"));
        raidListWEs.stream().forEach(we -> {
            String listId = we.getAttribute("id");
            FarmList farmList = farmLists.stream().filter(l -> l.getId().equals(listId)).findFirst().orElse(null);
            if (farmList == null)
            {
                farmList = new FarmList(listId);
                farmLists.add(farmList);
            }
            String displayName = we.findElement(By.className("listTitleText")).getText().trim();
            farmList.setName(displayName);
            farmList.setSize(we.findElements(By.xpath(".//tr[@class='slotRow']")).size());
            FarmList.AttackType type = Arrays.stream(FarmList.AttackType.values())
                    .filter(v -> displayName.toLowerCase().contains(v.getDisplayName().toLowerCase()))
                    .findFirst().orElse(FarmList.AttackType.OTHER);
            farmList.setType(type);
            LOG.debug(farmList.toString());
        });
    }

    public Village getVillage(String name)
    {
        return villages.stream().filter(v -> v.getName().toLowerCase().equals(name.toLowerCase())).findFirst().orElse(null);
    }

    public String autoRaid(Page page)
    {
        if (!autoRaid)
        {
            return "";
        }

        loadFarmList(page);
        StringBuilder result = new StringBuilder();
        farmLists.stream().forEach(l -> {
            result.append(l.raid(page));
            result.append("\n");
        });

        if (RandomUtil.possibility(0.85))
        {
            page.loadURL("dorf1.php");
        }
        return result.toString();
    }

    public void cleanUpMessage(Page page)
    {
        WebDriver pageResult = page.loadURL("berichte.php?t=1");
        List<WebElement> filters = pageResult.findElements(By.className("iconFilter"));
        if (!filters.get(0).getAttribute("class").contains("iconFilterActive"))
        {
            page.click(filters.get(0));
        }

        filters = pageResult.findElements(By.className("iconFilter"));
        if (filters.get(1).getAttribute("class").contains("iconFilterActive"))
        {
            page.click(filters.get(1));
        }

        filters = pageResult.findElements(By.className("iconFilter"));
        if (filters.get(2).getAttribute("class").contains("iconFilterActive"))
        {
            page.click(filters.get(2));
        }

        while (pageResult.findElements(By.xpath("//table[@id='overview']/tbody/tr/td[@class='noData']")).size() == 0)
        {
            page.click(pageResult.findElement(By.id("sAll2")));
            page.click(pageResult.findElement(By.id("del")));
        }

        if (RandomUtil.possibility(0.85))
        {
            page.loadURL("dorf1.php");
        }
    }

    public void openMap(Page page)
    {
        page.loadURL("karte.php");
        if (RandomUtil.possibility(0.85))
        {
            page.loadURL("dorf1.php");
        }
    }

    public void autoBuild(Page page)
    {
        load(page);

        villages.stream().filter(v -> v.isAutoBuild()).forEach(v -> {
            v.autoBuild(page);
            if (v.needResource())
            {
                String result = v.transferResource(page, getVillage("Empire Strikes Back"));
                LOG.info("Send Resource: " + result);
            }
        });
    }


    @Override
    public String toString()
    {
        return "Game{" +
                "alarmOn=" + alarmOn +
                ", autoRaid=" + autoRaid +
                ", autoBuild=" + autoBuild +
                ", autoRepeatCount=" + autoRepeatCount +
                ", villages=" + villages +
                '}';
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getNotificationEmail()
    {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail)
    {
        this.notificationEmail = notificationEmail;
    }

    public void addVillage(Village village)
    {
        villages.add(village);
    }

    public List<Village> getVillages()
    {
        return villages;
    }

    public void switchAlarmOn()
    {
        this.alarmOn = true;
    }

    public void switchAlarmOff()
    {
        this.alarmOn = false;
    }

    public Boolean getAlarmOn()
    {
        return alarmOn;
    }

    public void setAlarmOn(Boolean alarmOn)
    {
        this.alarmOn = alarmOn;
    }

    public Boolean getAutoRaid()
    {
        return autoRaid;
    }

    public void setAutoRaid(Boolean autoRaid)
    {
        this.autoRaid = autoRaid;
    }

    public Boolean getAutoBuild()
    {
        return autoBuild;
    }

    public void setAutoBuild(Boolean autoBuild)
    {
        this.autoBuild = autoBuild;
    }

    public int getAutoRepeatCount()
    {
        return autoRepeatCount;
    }

    public void increasingAutoRepeatCount()
    {
        autoRepeatCount++;
    }

    public void resetAutoRepeatCount(int listSize)
    {
        autoRepeatCount = RandomUtil.randomIntFrom0(listSize);
    }

    public static String getServer()
    {
        return SERVER;
    }


}
