package luke.zhou;

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;
import org.openqa.selenium.NoSuchElementException;
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
                        case STOP_RAID:
                            game.setAutoRaid(false);
                            System.out.println("Repeat raid stopped");
                            break;
                        case GET_INFO:
                            reloadInfo();
                            break;
                        case TRANSFER:
                            transferResource();
                            break;
                        case CLEAN_MESSAGE:
                            travian.cleanupMessage();
                            travian.openMap();
                            System.out.println("All non-loss report cleaned up");
                            break;
                        case TEST:
                            travian.withdrawTroops();
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
                if (game.getAutoRaid() && (tick % (roughThirtyMin) == 0))
                {
                    repeatRaiding();
                    roughThirtyMin = 12 * RandomUtil.randomRange(20, 40);
                    //50% random to clean report
                    if (RandomUtil.randomIntCentre0(10) < 0)
                    {
                        LOG.debug("clean up message");
                        travian.cleanupMessage();
                    }

                    //33% random to open map
                    if (RandomUtil.randomIntFrom0(10) % 3 == 0)
                    {
                        LOG.debug("open map");
                        travian.openMap();
                    }

                }

                //every 10 mins


                tick++;
                Thread.sleep(TimeUtil.seconds(5));

            } catch (Exception e)
            {
                LOG.error(e.getMessage());
                e.printStackTrace();
                travian.exit();
                travian = new Page("http://ts4.travian.com.au");
                travian.login(game.getUsername(), game.getPassword());
            }
        }
        //travian.exit();
    }

    private void transferResource()
    {
        String result = travian.transferResource(game.getVillage("Big Bang"), game.getVillage("A New Hope"));
        LOG.info("Send Resource: " + result);
        System.out.println(result);
    }

    private void repeatRaiding()
    {
        String result = travian.sendRaid();
        LOG.info("Repeat Raid: " + result);
        travian.home(game);
    }

    private void startRaid()
    {
        String result = travian.sendRaid();
        LOG.info("Start Raid: " + result);
        System.out.println(result);
        travian.home(game);
    }

    private void reloadInfo()
    {
        travian.getInfo(game);
        LOG.info("Game: " + game);
        game.getVillages().stream().forEach(System.out::println);
    }


    private void checkUnderAttack() throws InterruptedException
    {
        travian.home(game);
        if (game.getVillages().stream().anyMatch(v -> v.isUnderAttack()))
        {
            if (game.getVillages().get(1).isUnderAttack())
            {
                travian.evadeTroops();
            }
            if (game.getAlarmOn())
            {
                game.switchAlarmOff();
                new Thread(new MailIO(game.getNotificationEmail())).start();
            }
        }
        else if(!game.getAlarmOn())
        {
            game.switchAlarmOn();
            travian.withdrawTroops();
        }
    }

    public BlockingQueue<Command> getTravianCommandQueue()
    {
        return travianCommandQueue;
    }
}
