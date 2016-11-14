package luke.zhou.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 4:39 PM
 */
public enum Command
{
    SEND_ALARM("send alarm","not available for user"),
    RAID("raid", "check all in the raid list, raid once"),
    REPEAT_RAID("repeat raid", "check all in the raid list, raid until stop"),
    STOP_RAID("stop raid", "stop repeat raid"),
    GET_INFO("info", "display brief information of account"),
    HELP("help", "display command list");

    private final String value;
    private final String description;

    Command(String value, String description)
    {
        this.value = value;
        this.description = description;
    }


    public String getValue()
    {
        return value;
    }

    private static final Map<String, Command> lookup = new HashMap<>();

    static
    {
        for (Command cmd : Command.values())
        {
            lookup.put(cmd.getValue(), cmd);
        }
    }

    public static Command get(String value)
    {
        return lookup.get(value);
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString()
    {
        return String.format("%-20s", value) + description ;
    }
}
