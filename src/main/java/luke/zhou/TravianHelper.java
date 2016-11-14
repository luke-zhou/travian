package luke.zhou;

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.model.Game;
import luke.zhou.model.TroopMovement;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Luke on 13/11/16.
 */
public class TravianHelper implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(TravianHelper.class);

    private String username;
    private String password;

    private BlockingQueue<Command> travianCommandQueue;

    private Page travian;

    public TravianHelper(String username, String password)
    {
        this.username = username;
        this.password = password;
        travianCommandQueue = new LinkedBlockingQueue<>();

        travian = new Page("http://ts4.travian.com.au/");
    }

    public void start() throws InterruptedException
    {
        travian.login(username, password);

        for (int i = 0; i < 10; i++)
        {
            String result = travian.sendRaid();
            LOG.info("Start Raid: " + result);
            travian.home();
            Thread.sleep(TimeUtil.minutes(20) + RandomUtil.randomIntCentre0(20 * 1000));
        }

        travian.exit();
    }

    public void run()
    {
        Game game = new Game();
        travian.login(username, password);

        while (true)
        {
            try
            {
                checkUnderAttack(game);

                Command cmd = travianCommandQueue.poll();
                if (cmd==null) continue;
                switch (cmd)
                {
                    case RAID:
                        startRaid();
                        break;
                    default:
                        LOG.debug("got cmd for travian helper:"+cmd);
                }

                Thread.sleep(TimeUtil.minutes(5));
            }
            catch (Exception e)
            {
                LOG.error(e.getMessage());
                e.printStackTrace();
                travian.exit();
            }
        }

        //travian.exit();
    }

    private void startRaid(){
        String result = travian.sendRaid();
        LOG.info("Start Raid: " + result);
        travian.home();
    }

    private void checkUnderAttack(Game game) throws InterruptedException
    {
        TroopMovement troopMovement = travian.home();
        if (troopMovement.underAttack())
        {
            if (game.getAlarmOn())
            {
                game.switchAlarmOff();
                Main.getMainCommandQueue().put(Command.SEND_ALARM);
            }
        }
        else
        {
            game.switchAlarmOn();
        }
    }

    public BlockingQueue<Command> getTravianCommandQueue()
    {
        return travianCommandQueue;
    }
}
