package luke.zhou.model;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 4:39 PM
 */
public enum Command
{
    SEND_ALARM("send alarm"),
    RAID("raid");

    private final String value;
    Command(String value)
    {
        this.value = value;
    }
}
