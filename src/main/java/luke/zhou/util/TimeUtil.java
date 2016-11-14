package luke.zhou.util;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 9:05 AM
 */
public class TimeUtil
{
    public static int SECOND = 1000;
    public static int MINUTE = 60 * SECOND;

    public static int seconds(int sec)
    {
        return sec * SECOND;
    }

    public static int minutes(int min)
    {
        return min * MINUTE;
    }
}
