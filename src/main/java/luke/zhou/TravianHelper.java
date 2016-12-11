package luke.zhou;

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
import luke.zhou.model.travian.Resource;
import luke.zhou.model.travian.Village;
import luke.zhou.util.DateUtil;
import luke.zhou.util.RandomUtil;
import luke.zhou.util.TimeUtil;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
        boolean attacked =false;
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
                            game.resetAutoRepeatCount(9);
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
                            transferResource();
                            break;
                        case CLEAN_MESSAGE:
                            travian.cleanupMessage();
                            travian.openMap();
                            System.out.println("All non-loss report cleaned up");
                            break;
                        case TEST:
                            //travian.attack(game.getVillage("A New Hope"),-35,-15);
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
                if (tick % (roughThirtyMin) == 0||(tick==5))
                {
                    if (game.getAutoRaid())
                    {
                        repeatRaidingAllInList();
                        repeatRaidingOneInList(game.getAutoRepeatCount() % 8);
                        //50% random to clean report
                        if (RandomUtil.randomIntCentre0(10) < 0)
                        {
                            LOG.debug("clean up message");
                            travian.cleanupMessage();
                        }

                        Calendar now = Calendar.getInstance();
                        LOG.debug(now.getTime().toString());
//                    if(now.after(DateUtil.getSpecificHour(2))
//                            && now.before(DateUtil.getSpecificHour(6))
//                            && !attacked){
//                        LOG.info("Launch attack");
//                        travian.attack(-9, 45);
//                        attacked=true;
//                    }
                    }

                    if(game.getAutoBuild()){
                        buildResource();
                    }

                    //33% random to open map
                    if (RandomUtil.randomIntFrom0(10) % 3 == 0)
                    {
                        LOG.debug("open map");
                        travian.openMap();
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

    private void transferResource()
    {
        Village village = game.getVillage("Empire Strikes Back");
        double maxResource = Math.max(village.getIron()*1.0/village.getWarehouseCapacity(),
        Math.max(
                village.getClay()*1.0/village.getWarehouseCapacity(),
                village.getLumber()*1.0/village.getWarehouseCapacity()) );
        LOG.debug(String.valueOf(maxResource));
        String result = travian.transferResource(game.getVillage("A New Hope"), game.getVillage("Empire Strikes Back"));
        LOG.info("Send Resource: " + result);
        System.out.println(result);
    }

    private void repeatRaidingAllInList()
    {
        String result = travian.sendRaidAllInList(game);
        LOG.info("Repeat Raid: " + result);
        travian.home(game);
    }

    private void repeatRaidingOneInList(int index)
    {
        String result = travian.sendRaidOneInList(index);
        LOG.info(result);
        if (!result.contains("Not enough troops")){
            game.increasingAutoRepeatCount();
        }
        travian.home(game);
    }

    private void startRaid()
    {
        String result = travian.sendRaidAllInList(game);
        LOG.info("Start Raid: " + result);
        System.out.println(result);
        travian.home(game);
    }

    private void reloadInfo()
    {
        game.load(travian);
        LOG.info("Game: " + game);
        game.getVillages().stream().forEach(System.out::println);
    }


    private void checkUnderAttack() throws InterruptedException
    {
        travian.home(game);
        if (game.getVillages().stream().anyMatch(v -> v.isUnderAttack()))
        {
            if (game.getVillage("Big Bang").isUnderAttack())
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

    private void buildResource(){
        game.load(travian);
        Village village = game.getVillage("Empire Strikes Back");
        Resource resource = Arrays.stream(village.getResources())
                .filter(r->r.getType().equals(Resource.ResourceType.CROP))
                .filter(r -> r.isReady()&&(!r.isUnderConstruction()))
                .sorted((r1, r2) -> r1.getLevel()- r2.getLevel())
                .findFirst().orElse(null);
        if (resource!=null){
            travian.build(village, resource);
            LOG.info("resource:"+resource.getId()+" has been upgraded from "+resource.getLevel() +" to "+(resource.getLevel()+1));
            //System.out.print("Build successfully");
        }
        else
        {
            LOG.info("No available resource can be upgraded");
        }

        if(village.getCrop()*1.0/village.getGranaryCapacity()>0.5){
            String result = travian.transferCrop(village);
            LOG.info("Send Resource: " + result);
        }

        double maxResource = Math.max(village.getIron()*1.0/village.getWarehouseCapacity(),
                Math.max(
                        village.getClay()*1.0/village.getWarehouseCapacity(),
                        village.getLumber()*1.0/village.getWarehouseCapacity()) );
        LOG.debug("max resource: "+String.valueOf(maxResource));
        if (maxResource < 0.5)
        {
            String result = travian.transferResource(game.getVillage("A New Hope"), game.getVillage("Empire Strikes Back"));
            LOG.info("Send Resource: " + result);
        }

    }

    public BlockingQueue<Command> getTravianCommandQueue()
    {
        return travianCommandQueue;
    }
}
