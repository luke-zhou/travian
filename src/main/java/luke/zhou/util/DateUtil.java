package luke.zhou.util;

import java.util.Calendar;

/**
 * Created by Luke on 3/12/16.
 */
public class DateUtil
{
    public static Calendar getSpecificHour(int hour)
    {
        Calendar specific = Calendar.getInstance();
        specific.set(Calendar.HOUR_OF_DAY, hour);
        specific.set(Calendar.MINUTE, 0);
        specific.set(Calendar.SECOND, 0);
        specific.set(Calendar.MILLISECOND, 0);
        return specific;
    }

}
