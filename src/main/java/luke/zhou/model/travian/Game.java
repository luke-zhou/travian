package luke.zhou.model.travian;

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
    private Boolean alarmOn;
    private Boolean autoRaid;
    private String username;
    private String password;
    private String notificationEmail;
    private Date date;


    private List<Village> villages;

    public Game()
    {
        this.alarmOn = true;
        autoRaid = false;
        this.villages = new ArrayList<>();
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
}
