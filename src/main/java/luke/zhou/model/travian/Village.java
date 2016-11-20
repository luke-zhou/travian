package luke.zhou.model.travian;

import java.util.Arrays;

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

    Resource[] resources;


    public Village(String name)
    {
        this.isUnderAttack = false;
        this.name = name;
        resources = new Resource[18];
    }
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Village(" + name + "):\t" + "isUnderAttack=" + isUnderAttack + "\n");
        sb.append("lumber:" + lumber +"\tclay:" + clay +"\tiron:" + iron +"\tcrop:" + crop + "\n");
        sb.append("resources:\n");
        for(int i=0; i< resources.length;i++)
        {
            sb.append((i+1)+":");
            sb.append(resources[i].toString());
            sb.append("\t");
            if((i+1)%5==0) sb.append("\n")  ;
        }
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
