package luke.zhou.util;

/**
 * Created by Luke on 17/11/16.
 */
public class IntegerUtil
{
    public static Integer convertInteger(String intString)
    {
        return Integer.valueOf(intString.replace(",",""));
    }
}
