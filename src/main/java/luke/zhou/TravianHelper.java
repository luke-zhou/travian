package luke.zhou;

import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
import luke.zhou.model.travian.TroopMovement;
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

    private Game game;

    public TravianHelper(String username, String password)
    {
        this.username = username;
        this.password = password;
        travianCommandQueue = new LinkedBlockingQueue<>();

        travian = new Page("http://ts4.travian.com.au/");
    }

    public void run()
    {
        game = new Game();
        travian.login(username, password);
        Main.getMainCommandQueue().add(Command.READY);
        int clocktick = 0;
        while (true)
        {
            try
            {
                Command cmd = travianCommandQueue.poll();
                if (cmd != null)
                {
                    LOG.debug("got cmd for helper:" + cmd);
                    switch (cmd)
                    {
                        case RAID:
                            startRaid();
                            break;
                        case EXIT:
                            travian.exit();
                            System.exit(0);
                            break;
                        case REPEAT_RAID:
                            clocktick = 0;
                            game.setAutoRaid(true);
                            break;
                        case STOP_RAID:
                            game.setAutoRaid(false);
                            break;
                        default:
                            LOG.debug("got cmd for travian helper:" + cmd);
                    }
                }

                checkUnderAttack();
                if (game.getAutoRaid() && clocktick <= 4*12)
                {
                    repeatRaiding(clocktick++);
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

    private void repeatRaiding(int clocktick)
    {
        LOG.debug("clocktick:"+clocktick);
        //every 20min
        if (clocktick % 4 == 0)
        {
            startRaid();
        }
    }

    private void startRaid()
    {
        String result = travian.sendRaid();
        LOG.info("Start Raid: " + result);
        travian.home(game);
    }

    private void checkUnderAttack() throws InterruptedException
    {
        travian.home(game);
        if (game.getVillages().stream().anyMatch(v -> v.isUnderAttack()))
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
