package luke.zhou.model.travian;

/**
 * Created by Luke on 16/11/16.
 */
public class Village
{
    boolean isUnderAttack;
    String name;
    String link;

    public Village(String name)
    {
        this.isUnderAttack = false;
        this.name = name;
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

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }
}
