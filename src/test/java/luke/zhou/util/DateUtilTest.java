package luke.zhou.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Luke on 3/12/16.
 */
public class DateUtilTest
{
    @Test
    public void getSpecificHour() throws Exception
    {
        System.out.println(DateUtil.getSpecificHour(2).getTime());
        System.out.println(DateUtil.getSpecificHour(6).getTime());
        System.out.println(DateUtil.getSpecificHour(22).getTime());

    }
}