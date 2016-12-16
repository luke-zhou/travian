package luke.zhou.model.travian;

import luke.zhou.Page;
import luke.zhou.util.RandomUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Luke on 16/11/16.
 */
public class Village
{
    private static final Logger LOG = LoggerFactory.getLogger(Village.class);

    boolean isUnderAttack;
    String name;
    int newdid;

    int lumber;
    int clay;
    int iron;
    int crop;
    int warehouseCapacity;
    int granaryCapacity;

    Resource[] resources;
    Building[] buildings;

    boolean autoBuild;


    public Village(String name)
    {
        this.isUnderAttack = false;
        this.autoBuild = false;
        this.name = name;
        resources = new Resource[18];
        buildings = new Building[22];
    }

    public void load(Page page)
    {
        loadResourcesValue(page);

        loadResourcesLevel(page);

        loadBuildings(page);

    }

    private void loadBuildings(Page page)
    {
        WebDriver pageResult = page.loadURL("dorf2.php" + "?newdid=" + newdid + "&");
        IntStream.range(0, 22).forEach(i -> {
            buildings[i] = getBuilding(i, pageResult);
        });
    }

    private void loadResourcesValue(Page page)
    {
        page.loadURL(getHomeLink());
        warehouseCapacity = page.getInt("//span[@id='stockBarWarehouse']");
        granaryCapacity = page.getInt("//span[@id='stockBarGranary']");
        lumber = page.getInt("//ul[@id='stockBar']/li[@id='stockBarResource1']//span[@id='l1']");
        clay = page.getInt("//ul[@id='stockBar']/li[@id='stockBarResource2']//span[@id='l2']");
        iron = page.getInt("//ul[@id='stockBar']/li[@id='stockBarResource3']//span[@id='l3']");
        crop = page.getInt("//ul[@id='stockBar']/li[@id='stockBarResource4']//span[@id='l4']");
    }

    private void loadResourcesLevel(Page page)
    {
        WebDriver pageResult = page.loadURL(getHomeLink());
        IntStream.range(0, 18).forEach(i -> {
            resources[i] = getResource(i, pageResult);
        });
    }

    private Building getBuilding(int i, WebDriver pageResult)
    {
        WebElement resourceMapWE = pageResult.findElement(By.xpath("//map[@id='clickareas']//area[@href='build.php?id=" + (i + 19) + "']"));
        String href = resourceMapWE.getAttribute("href");
        String alt = resourceMapWE.getAttribute("alt");
        Building building;
        if (alt.equals(Building.BuildingType.EMPTY.getDisplayName()))
        {
            building = new Building(Building.BuildingType.EMPTY, href, 0, i + 19);
        }
        else
        {
            int index1 = alt.indexOf("<span");
            int index2 = alt.indexOf(">Level");
            int index3 = alt.indexOf("</span>");

            String buildingType = alt.substring(0, index1 - 1);
            String buildingLevel = alt.substring(index2 + 7, index3);
            building = new Building(Building.BuildingType.get(buildingType),
                    href, Integer.valueOf(buildingLevel), i + 19);
            List<WebElement> buildingWEs = pageResult.findElements(By.xpath("//div[@id='levels']/div"));
            for (BuildingStatus status : BuildingStatus.values())
            {
                WebElement buildingWE = buildingWEs.stream().
                        filter(e -> e.getAttribute("class").contains("aid" + building.getLocation())).findFirst().get();
                if (buildingWE.getAttribute("class").contains(status.getValue()))
                {
                    building.addStatus(status);
                }
            }
        }
        return building;
    }


    private Resource getResource(int i, WebDriver pageResult)
    {
        WebElement resourceMapWE = pageResult.findElement(By.xpath("//div[@id='content']//area[@href='build.php?id=" + (i + 1) + "']"));
        String href = resourceMapWE.getAttribute("href");
        String alt = resourceMapWE.getAttribute("alt");
        String[] results = alt.split("Level");
        Resource resource = new Resource(Resource.ResourceType.get(results[0].trim()),
                href, Integer.valueOf(results[1].trim()), i + 1);
        List<WebElement> resourceWEs = pageResult.findElements(By.xpath("//div[@id='village_map']/div"));
        for (BuildingStatus status : BuildingStatus.values())
        {
            if (resourceWEs.get(i).getAttribute("class").contains(status.getValue()))
            {
                resource.addStatus(status);
            }
        }

        return resource;

    }

    public boolean hasRallyPoint(){
        return Arrays.stream(buildings).anyMatch(b -> b.getType().equals(Building.BuildingType.RALLY_POINT));
    }


    public void autoBuild(Page page) {
        Resource resource = Arrays.stream(resources)
                .filter(r -> r.isReady() && (!r.isUnderConstruction()))
                .sorted((r1, r2) -> r1.getLevel() - r2.getLevel())
                .findFirst().orElse(null);
        if (resource != null)
        {
            page.loadURL(getHomeLink());
            resource.build(page);
            LOG.info("resource:" + resource.getLocation() + " has been upgraded from " + resource.getLevel() + " to " + (resource.getLevel() + 1));
        }
        else
        {
            LOG.info("No available resource can be upgraded");
        }
    }

    public double getCropStorage(){
        return crop * 1.0 / granaryCapacity;
    }

    public double getClayStorage(){
        return clay * 1.0 / warehouseCapacity;
    }

    public double getLumberStorage(){
        return lumber * 1.0 / warehouseCapacity;
    }

    public double getIronStorage(){
        return iron * 1.0 / warehouseCapacity;
    }

    public double getMaxResourcePercentage()
    {
        double maxResource = Math.max(getCropStorage(),
                                Math.max(getIronStorage(),
                                    Math.max(getClayStorage(), getLumberStorage())
                                )
                            );
        LOG.debug("max resource: " + String.valueOf(maxResource));

        return maxResource;
    }

    public String transferResource(Page page, Village to)
    {
        String result;
        WebDriver pageResult = page.loadURL("build.php?t=5&id=35");
        if (!pageResult.findElement(By.id("merchantCapacityValue")).getText().equals("0"))
        {
            int capacity = page.getInt("//div[@id='build']/div[@class='carry']/b");
            pageResult.findElement(By.xpath("//input[@id='r1']")).sendKeys(String.valueOf((int) capacity * 0.3));
            pageResult.findElement(By.xpath("//input[@id='r2']")).sendKeys(String.valueOf((int) capacity * 0.3));
            pageResult.findElement(By.xpath("//input[@id='r3']")).sendKeys(String.valueOf((int) capacity * 0.3));
            pageResult.findElement(By.xpath("//input[@id='r4']")).sendKeys(String.valueOf((int) capacity * 0.1));
            pageResult.findElement(By.xpath("//input[@id='enterVillageName']")).sendKeys(to.getName());

            page.submit(pageResult.findElement(By.xpath("//button[@id='enabledButton']")));
            page.click(pageResult.findElement(By.xpath("//button[@id='enabledButton']")));
            result = pageResult.findElement(By.xpath("//p[@id='note']")).getText();
        }
        else
        {
            result = "Don't have merchant at home";
        }
        if (RandomUtil.possibility(0.85))
        {
            page.loadURL("dorf1.php");
        }
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Village(" + name + "):\t" + "isUnderAttack=" + isUnderAttack + "\n");
        sb.append("lumber:" + lumber + "/" + warehouseCapacity +
                "\tclay:" + clay + "/" + warehouseCapacity +
                "\tiron:" + iron + "/" + warehouseCapacity +
                "\tcrop:" + crop + "/" + granaryCapacity + "\n");
        sb.append("resources:\n");
        IntStream.range(0, resources.length).forEach(i -> {
            sb.append(resources[i].toString());
            sb.append("\t");
            if ((i + 1) % 5 == 0) sb.append("\n");
        });
        sb.append("\n");
        IntStream.range(0, buildings.length).forEach(i -> {
            sb.append(buildings[i].toString());
            sb.append("\t");
            if ((i + 1) % 5 == 0) sb.append("\n");
        });
        return sb.toString();
    }

    public Resource[] getResources()
    {
        return resources;
    }

    public boolean isUnderAttack()
    {
        return isUnderAttack;
    }

    public void setUnderAttack(boolean underAttack)
    {
        isUnderAttack = underAttack;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getNewdid()
    {
        return newdid;
    }

    public void setNewdid(int newdid)
    {
        this.newdid = newdid;
    }

    public int getLumber()
    {
        return lumber;
    }

    public void setLumber(int lumber)
    {
        this.lumber = lumber;
    }

    public int getClay()
    {
        return clay;
    }

    public void setClay(int clay)
    {
        this.clay = clay;
    }

    public int getIron()
    {
        return iron;
    }

    public void setIron(int iron)
    {
        this.iron = iron;
    }

    public int getCrop()
    {
        return crop;
    }

    public void setCrop(int crop)
    {
        this.crop = crop;
    }

    public int getWarehouseCapacity()
    {
        return warehouseCapacity;
    }

    public void setWarehouseCapacity(int warehouseCapacity)
    {
        this.warehouseCapacity = warehouseCapacity;
    }

    public int getGranaryCapacity()
    {
        return granaryCapacity;
    }

    public void setGranaryCapacity(int granaryCapacity)
    {
        this.granaryCapacity = granaryCapacity;
    }

    public String getHomeLink()
    {
        return "dorf1.php" + "?newdid=" + newdid + "&";
    }

    public boolean isAutoBuild() {
        return autoBuild;
    }

    public void setAutoBuild(boolean autoBuild) {
        this.autoBuild = autoBuild;
    }


}
