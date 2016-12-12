package luke.zhou.model.travian;

/**
 * Created by Luke on 11/12/16.
 */
public class FarmList
{
    Village belongTo;
    int size;

    public FarmList(Village belongTo, int size)
    {
        this.belongTo = belongTo;
        this.size = size;
    }
}
