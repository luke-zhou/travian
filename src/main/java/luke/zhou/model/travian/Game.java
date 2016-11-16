package luke.zhou.model.travian;

import java.util.ArrayList;
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

    private List<Village> villages;

    public Game()
    {
        this.alarmOn = true;
        autoRaid = false;
        this.villages = new ArrayList<>();
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
