package luke.zhou.model.travian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Luke on 18/11/16.
 */
public class Building
{
    private BuildingType type;
    private String link;
    private int level;
    private int location;

    private List<BuildingStatus> statusList;

    public Building(Building.BuildingType type, String link, int level, int location)
    {
        this.type = type;
        this.link = link;
        this.level = level;
        statusList = new ArrayList<>();
        this.location = location;
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

    public boolean isEmpty()
    {
        return BuildingType.EMPTY.equals(type);
    }

    public int getLocation()
    {
        return location;
    }

    public enum BuildingType
    {
        SAWMILL("Sawmill"),
        BRICKYARD("Brickyard"),
        IRON_FOUNDRY("Iron Foundry"),
        GRAIN_MILL("Grain Mill"),
        BAKERY("Bakery"),
        WAREHOUSE("Warehouse"),
        GRANARY("Granary"),
        SMITHY("Smithy"),
        TOURNAMENT_SQUARE("Tournament Square"),
        MAIN_BUILDING("Main Building"),
        RALLY_POINT("Rally Point"),
        MARKETPLACE("Marketplace"),
        EMBASSY("Embassy"),
        BARRACKS("Barracks"),
        STABLE("Stable"),
        WORKSHOP("Workshop"),
        ACADEMY("Academy"),
        CRANNY("Cranny"),
        TOWN_HALL("Town Hall"),
        RESIDENCE("Residence"),
        PALACE("Palace"),
        TREASURY("Treasury"),
        TRADE_OFFICE("Trade Officee"),
        GREAT_BARRACKS("Great Barracks"),
        GREAT_STABLE("Great Stable"),
        CITY_WALL("City Wall"),
        EARTH_WALL("Earth Wall"),
        PALISADE("Palisade"),
        STONEMASONS_LODGE("Stonemason's Lodge"),
        BREWERY("Brewery"),
        TRAPPER("Trapper"),
        HEROS_MANSION("Hero's Mansion"),
        GREAT_WAREHOUSE("Great Warehouse"),
        GREAT_GRANARY("Great Granary"),
        WONDER("Wonder of the World"),
        HORSE_DRINKING_TROUGH("Horse Drinking Trough"),
        EMPTY("Building site");

        private final String displayName;

        BuildingType(String displayName)
        {
            this.displayName = displayName;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public static BuildingType get(String value)
        {
            return lookup.get(value);
        }

        private static final Map<String, BuildingType> lookup = new HashMap<>();

        static
        {
            for (BuildingType type : BuildingType.values())
            {
                lookup.put(type.displayName, type);
            }
        }

    }
}
