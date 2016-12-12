package luke.zhou.model.travian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Luke on 20/11/16.
 */
public class Resource
{
    private ResourceType type;
    private String link;
    private int level;
    private int location;

    private List<BuildingStatus> statusList;

    public Resource(ResourceType type, String link, int level, int location)
    {
        this.type = type;
        this.link = link;
        this.level = level;
        statusList = new ArrayList<>();
        this.location = location;
    }

    public int getLocation()
    {
        return location;
    }

    public ResourceType getType()
    {
        return type;
    }

    public int getLevel()
    {
        return level;
    }

    public boolean isReady(){
        return statusList.contains(BuildingStatus.READY);
    }

    public boolean isUnderConstruction(){
        return statusList.contains(BuildingStatus.IN_PROGRESS);
    }

    public void addStatus(BuildingStatus status)
    {
        statusList.add(status);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(location);
        sb.append(":");
        sb.append(type);
        sb.append("(");
        sb.append(level);
        sb.append(")");
        sb.append("[");
        for(BuildingStatus status :statusList){
            sb.append(status.getAbbreviation());
        }
        sb.append("]");


        return sb.toString();
    }

    public enum ResourceType
    {
        CLAY("Clay Pit"),
        IRON("Iron Mine"),
        WOOD("Woodcutter"),
        CROP("Cropland");

        private final String displayName;

        ResourceType(String displayName)
        {
            this.displayName = displayName;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public static ResourceType get(String value)
        {
            return lookup.get(value);
        }

        private static final Map<String, ResourceType> lookup = new HashMap<>();

        static
        {
            for (ResourceType type : ResourceType.values())
            {
                lookup.put(type.displayName, type);
            }
        }

    }
}
