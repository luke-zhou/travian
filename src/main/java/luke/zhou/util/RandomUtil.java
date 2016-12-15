package luke.zhou.util;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 9:51 AM
 */
public class RandomUtil
{
    private static Random random = new Random(System.currentTimeMillis());

    public static int randomIntFrom0(int range)
    {
        return random.nextInt(range);
    }

    public static int randomIntCentre0(int range)
    {
        return random.nextInt(range) - range/2;
    }

    public static int randomRange(int lowBound, int upBound)
    {
        return random.nextInt(upBound - lowBound) + lowBound;
    }

    public static boolean possibility(double num)
    {
        return randomIntFrom0(100) < 100* num;
    }
}
