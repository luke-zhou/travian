package luke.zhou.model.travian;

import luke.zhou.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Luke on 20/11/16.
 */
public class Resource
{
    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);

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

    public void build(Page page) {
        WebDriver pageResult = page.loadURL("build.php?id=" + location);
        if(pageResult.findElements(By.xpath("//div[@class='showBuildCosts normal']/button")).size()>0){
            WebElement buttonWE = pageResult.findElement(By.xpath("//div[@class='showBuildCosts normal']/button"));
            if(!buttonWE.getAttribute("class").contains("disabled")){
                page.click(buttonWE);
                LOG.info(type +":" + location + " has been upgraded from " + level+ " to " + (level + 1));
                return;
            }
        }
        LOG.info("No available resource for upgrading "+type);
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
