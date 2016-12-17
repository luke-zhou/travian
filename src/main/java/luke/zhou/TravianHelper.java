package luke.zhou;

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
import luke.zhou.model.travian.Resource;
import luke.zhou.model.travian.Village;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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

        travian = new Page(game.getServer());

        this.game = game;
    }

    public void run()
    {
        game.login(travian);
        Main.getMainCommandQueue().add(Command.READY);

        int tick = 0;
        int roughFiveMin = 12 * 5;
        int roughThirtyMin = 12 * 30;
        int wholeDay = 12 * 60 * 24;
        while (true)
        {
            if (tick == wholeDay) tick = 0;

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
                            System.out.println("Repeat raid started");
                            break;
                        case AUTO_BUILD:
                            game.setAutoBuild(true);
                            System.out.println("Auto build started");
                            break;
                        case STOP_RAID:
                            game.setAutoRaid(false);
                            System.out.println("Repeat raid stopped");
                            break;
                        case STOP_BUILD:
                            game.setAutoBuild(false);
                            System.out.println("Auto build stopped");
                            break;
                        case GET_INFO:
                            reloadInfo();
                            break;
                        case TRANSFER:
                            break;
                        case CLEAN_MESSAGE:
                            game.cleanUpMessage(travian);
                            System.out.println("All non-loss report cleaned up");
                            break;
                        case TEST:
                            buildResource();
                            System.out.println("test function");
                            break;

                        default:
                            LOG.debug("got cmd for travian helper:" + cmd);
                    }

                    Main.getMainCommandQueue().add(Command.READY);
                }

                //every 5 mins
                if (tick % (roughFiveMin) == 0)
                {
                    checkUnderAttack();
                    roughFiveMin = 12 * RandomUtil.randomRange(3, 7);
                }

                //every 30 mins
                if (tick % (roughThirtyMin) == 0)
                {
                    String result = game.autoRaid(travian);
                    if (!result.isEmpty())
                    {
                        LOG.info(result);
                    }

                    //50% chance to clean up green message
                    if (RandomUtil.possibility(0.5))
                    {
                        LOG.debug("clean up message");
                        game.cleanUpMessage(travian);
                    }

                    if (game.getAutoBuild())
                    {
                        buildResource();
                    }

                    //33% random to open map
                    if (RandomUtil.possibility(0.33))
                    {
                        LOG.debug("open map");
                        game.openMap(travian);
                    }

                    roughThirtyMin = 12 * RandomUtil.randomRange(20, 40);

                }

                tick++;
                Thread.sleep(TimeUtil.seconds(5));

            } catch (Exception e)
            {
                LOG.error(e.getMessage());
                e.printStackTrace();
                travian.exit();
                travian = new Page(game.getServer());
                game.login(travian);
            }
        }
        //travian.exit();
    }


    private void startRaid()
    {
        String result = game.autoRaid(travian);
        LOG.info("Start Raid: " + result);
        System.out.println(result);
        //travian.home(game);
    }

    private void reloadInfo()
    {
        game.load(travian);
        LOG.info("Game: " + game);
        game.getVillages().stream().forEach(System.out::println);
    }


    private void checkUnderAttack() throws InterruptedException
    {
        if (game.checkUnderAttack(travian))
        {
            if (game.getAlarmOn())
            {
                game.switchAlarmOff();
                new Thread(new MailIO(game.getNotificationEmail())).start();
            }
        }
        else if (!game.getAlarmOn())
        {
            game.switchAlarmOn();
        }
    }

    private void buildResource()
    {
        game.autoBuild(travian);
    }

    public BlockingQueue<Command> getTravianCommandQueue()
    {
        return travianCommandQueue;
    }
}
