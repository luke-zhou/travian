package luke.zhou;

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
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

    private BlockingQueue<Command> travianCommandQueue;

    private Page travian;

    private Game game;

    public TravianHelper(Game game)
    {
        travianCommandQueue = new LinkedBlockingQueue<>();

        travian = new Page("http://ts4.travian.com.au");

        this.game = game;
    }

    public void run()
    {
        travian.login(game.getUsername(), game.getPassword());
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
                            game.setAutoRaid(true);
                            Main.getMainCommandQueue().add(Command.READY);
                            break;
                        case STOP_RAID:
                            game.setAutoRaid(false);
                            Main.getMainCommandQueue().add(Command.READY);
                            break;
                        case GET_INFO:
                            reloadInfo();
                            Main.getMainCommandQueue().add(Command.READY);
                            break;
                        default:
                            LOG.debug("got cmd for travian helper:" + cmd);
                    }
                }

                //every 5 mins
                if (clocktick % (12 * 5) == 0)
                {
                    checkUnderAttack();
                }

                //every 20 mins
                if (game.getAutoRaid() && (clocktick % (12 * 20) == 0))
                {
                    clocktick = 0;
                    repeatRaiding();
                }

                clocktick++;
                Thread.sleep(TimeUtil.seconds(5));
            } catch (Exception e)
            {
                LOG.error(e.getMessage());
                e.printStackTrace();
                travian.exit();
            }
        }
        //travian.exit();
    }

    private void repeatRaiding()
    {
        startRaid();
    }

    private void startRaid()
    {
        String result = travian.sendRaid();
        LOG.info("Start Raid: " + result);
        travian.home(game);
    }

    private void reloadInfo()
    {
        travian.getInfo(game);
        LOG.info("Game: " + game);
    }


    private void checkUnderAttack() throws InterruptedException
    {
        travian.home(game);
        if (game.getVillages().stream().anyMatch(v -> v.isUnderAttack()))
        {
            if (game.getAlarmOn())
            {
                game.switchAlarmOff();
                new Thread(new MailIO(game.getNotificationEmail())).start();
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
