package luke.zhou.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 11:26 AM
 */
public class RandomUtilTest
{

    @Test
    public void testRandomIntFrom0() throws Exception
    {
        for (int i = 0; i < 100; i++)
        {
            int result = RandomUtil.randomIntFrom0(10);
            assertTrue(result >= 0 && result < 10);
        }
    }

    @Test
    public void testRandomIntCentre0() throws Exception
    {
        for (int i = 0; i < 100; i++)
        {
            int result = RandomUtil.randomIntCentre0(10);
            assertTrue(result >= -5 && result < 5);
        }
    }

    @Test
    public void testRandomRange() throws Exception
    {
        for (int i = 0; i < 100; i++)
        {
            int result = RandomUtil.randomRange(4,13);
            assertTrue(result >= 4 && result < 13);
        }
    }
}