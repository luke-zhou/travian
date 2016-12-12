package luke.zhou.model.travian;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luke on 20/11/16.
 */
public enum BuildingStatus
{
    READY("good", "R"),
    IN_PROGRESS("underConstruction","I"),
    NOT_READY("notNow","N"),
    MAX_LEVEL("maxLevel", "M");

    private final String value;
    private final String abbreviation;

    BuildingStatus(String value, String abbreviation)
    {
        this.value = value;
        this.abbreviation = abbreviation;
    }

    public String getValue()
    {
        return value;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public static BuildingStatus get(String value)
    {
        return lookup.get(value);
    }

    private static final Map<String, BuildingStatus> lookup = new HashMap<>();

    static
    {
        for (BuildingStatus status : BuildingStatus.values())
        {
            lookup.put(status.value, status);
        }
    }
}
