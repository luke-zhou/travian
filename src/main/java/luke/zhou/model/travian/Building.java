package luke.zhou.model.travian;

/**
 * Created by Luke on 18/11/16.
 */
public class Building
{
    BuildingCatagory catagory;

    private enum BuildingCatagory
    {
        INFRASTRUCTURE,
        MILITARY,
        RESOURCES
    }
}