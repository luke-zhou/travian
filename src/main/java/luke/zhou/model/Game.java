package luke.zhou.model;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 4:03 PM
 */
public class Game
{
    private Boolean alarmOn;

    public Game()
    {
        this.alarmOn = true;
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
}
