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
    SEND_ALARM("send alarm", "", false),
    RAID("raid", "check all in the raid list, raid once", true),
    REPEAT_RAID("repeat raid", "check all in the raid list, raid until stop", true),
    AUTO_BUILD("build", "auto build crop land in empire strikes back", true),
    STOP_RAID("stop raid", "stop repeat raid", true),
    STOP_BUILD("stop build", "stop auto build", true),
    GET_INFO("info", "display brief information of account", true),
    HELP("help", "display command list", true),
    READY("ready", "", false),
    TRANSFER("transfer", "transfer resouse to new village", true),
    CLEAN_MESSAGE("cleanup","clean up the non-loss reports", true),
    TEST("test", "", false),
    EXIT("exit", "Exit program", true);

    private final String value;
    private final String description;
    private final boolean visible;

    Command(String value, String description, boolean visible)
    {
        this.value = value;
        this.description = description;
        this.visible = visible;
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

    public boolean isVisible()
    {
        return visible;
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
        return String.format("%-20s", value) + description;
    }
}
