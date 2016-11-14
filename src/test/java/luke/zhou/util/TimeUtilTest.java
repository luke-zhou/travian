package luke.zhou.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 11:29 AM
 */
public class TimeUtilTest
{

    @Test
    public void testSeconds() throws Exception
    {
        assertTrue(TimeUtil.seconds(10) == 10 * 1000);
    }

    @Test
    public void testMinutes() throws Exception
    {
        assertTrue(TimeUtil.minutes(10) == 10 * 60 * 1000);
    }
}