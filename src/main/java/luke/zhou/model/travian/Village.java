package luke.zhou.model.travian;

/**
 * Created by Luke on 16/11/16.
 */
public class Village
{
    boolean isUnderAttack;
    String name;
    String link;

    int lumber;
    int clay;
    int iron;
    int crop;


    public Village(String name)
    {
        this.isUnderAttack = false;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Village{" +
                "isUnderAttack=" + isUnderAttack +
                ", name='" + name + '\'' +
                ", lumber=" + lumber +
                ", clay=" + clay +
                ", iron=" + iron +
                ", crop=" + crop +
                '}';
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
}
