package luke.zhou.model.travian;

import luke.zhou.Page;
import luke.zhou.util.IntegerUtil;
import luke.zhou.util.RandomUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

    private static final String SERVER ="http://ts4.travian.com.au";

    private Boolean alarmOn;
    private Boolean autoRaid;
    private Boolean autoBuild;
    private int autoRepeatCount;
    private String username;
    private String password;
    private String notificationEmail;
    private Date date;


    private List<Village> villages;

    public Game()
    {
        this.alarmOn = true;
        autoRaid = false;
        autoBuild = false;
        this.villages = new ArrayList<>();
    }

    public void login(Page page){
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
        villages.clear();
        villageWE.findElements(By.tagName("li")).forEach(we -> {
            Village village = new Village(we.findElement(By.className("name")).getText());
            village.setUnderAttack(we.getAttribute("class").contains("attack"));
            String link = we.findElement(By.tagName("a")).getAttribute("href");
            village.setNewdid(Integer.valueOf(link.substring(link.indexOf("=")+1, link.length()-1)));
            villages.add(village);
        });

        LOG.debug("Village size:" + villages.size());
        villages.stream().forEach(v -> v.load(page));
    }

    public Village getVillage(String name)
    {
       return villages.stream().filter(v-> v.getName().toLowerCase().equals(name.toLowerCase())).findFirst().get();
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

    public void increasingAutoRepeatCount(){
        autoRepeatCount++;
    }

    public void resetAutoRepeatCount(int listSize){
        autoRepeatCount= RandomUtil.randomIntFrom0(listSize);
    }

    public static String getServer()
    {
        return SERVER;
    }
}
