package luke.zhou.model.travian;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 12:27 PM
 */
public class TroopMovement
{
    String inComingAttack;
    String inComingRein;
    String outGoingAttack;
    String adventure;

    public Boolean underAttack(){
        return inComingAttack != null;
    }

    public String getInComingAttack()
    {
        return inComingAttack;
    }

    public void setInComingAttack(String inComingAttack)
    {
        this.inComingAttack = inComingAttack;
    }

    public String getInComingRein()
    {
        return inComingRein;
    }

    public void setInComingRein(String inComingRein)
    {
        this.inComingRein = inComingRein;
    }

    public String getOutGoingAttack()
    {
        return outGoingAttack;
    }

    public void setOutGoingAttack(String outGoingAttack)
    {
        this.outGoingAttack = outGoingAttack;
    }

    public String getAdventure()
    {
        return adventure;
    }

    public void setAdventure(String adventure)
    {
        this.adventure = adventure;
    }

    @Override
    public String toString()
    {
        return "TroopMovement{" +
                "inComingAttack='" + inComingAttack + '\'' +
                ", inComingRein='" + inComingRein + '\'' +
                ", outGoingAttack='" + outGoingAttack + '\'' +
                ", adventure='" + adventure + '\'' +
                '}';
    }
}
