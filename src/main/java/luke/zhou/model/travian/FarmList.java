package luke.zhou.model.travian;

import luke.zhou.Page;
import luke.zhou.util.RandomUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by Luke on 11/12/16.
 */
public class FarmList
{
    int size;
    String id;
    String name;

    AttackType type;

    int lastItemIndex;

    public FarmList(String id)
    {
        this.id = id;
        lastItemIndex = -1;
    }


    public void setSize(int size)
    {
        this.size = size;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setType(AttackType type)
    {
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "FarmList{" +
                "size=" + size +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String raid(Page page)
    {
        switch (type)
        {
            case ALL:
                return name + ": " + raidAll(page);
            case ONE:
                return name + ": " + raidOne(page);
            default:
                return name + ": " + "";
        }
    }

    private String raidAll(Page page)
    {
        WebElement selectAll = page.getPageResult().findElement(By.id("raidListMarkAll" + id.replace("list", "")));
        selectAll.click();
        page.submit(selectAll);
        return page.getPageResult().findElement(By.id(id))
                .findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();
    }

    private String raidOne(Page page)
    {
        if (lastItemIndex == -1)
        {
            lastItemIndex = RandomUtil.randomIntFrom0(size);
        }
        List<WebElement> items = page.getPageResult().findElements(By.xpath("//div[@id='" + id + "']//tr[@class='slotRow']"));
        String villageName = items.get(lastItemIndex).findElement(By.className("village")).findElement(By.tagName("a")).getText();
        WebElement checkboxWE = items.get(lastItemIndex).findElement(By.tagName("input"));
        checkboxWE.click();
        page.submit(checkboxWE);

        String result = page.getPageResult().findElement(By.id(id))
                .findElement(By.className("listContent ")).findElement(By.tagName("p")).getText();
        if (result.equals("0 raids have been made."))
        {
            return "Not enough troops for " + villageName;
        }
        else
        {
            lastItemIndex = (lastItemIndex + 1) % size;
            return "Send raid to " + villageName;
        }
    }

    public enum AttackType
    {
        ALL("all"),
        ONE("one"),
        OTHER("");

        private final String displayName;

        AttackType(String displayName)
        {
            this.displayName = displayName;
        }

        public String getDisplayName()
        {
            return displayName;
        }

    }
}
