package luke.zhou;
/**
 * Created by Luke on 12/11/16.
 */

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main
{
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static BlockingQueue<Command> mainCommandQueue = new LinkedBlockingQueue<>() ;


    public static void main(String[] args) throws InterruptedException
    {
        Console c = System.console();
        if (c == null)
        {
            System.err.println("No console.");
            System.exit(1);
        }
        System.out.println("Welcome to Travian Helper");
        String login = c.readLine("Enter your login: ");
        String password = c.readLine("Enter your password: ");
        String notificationEmail = c.readLine("Enter your notification email: ");

        LOG.debug(login + "/" + password + "/" + notificationEmail);

        TravianHelper travianHelper = new TravianHelper(login, password, notificationEmail);
        new Thread(travianHelper).start();
        new Thread(new ControlCenter()).start();

        while(true){
            Command cmd = mainCommandQueue.poll();
            if (cmd==null) continue;
            switch (cmd)
            {
                case SEND_ALARM:
                    MailIO.sendUnderAttackAlarm(notificationEmail);
                    break;
                case RAID:
                    travianHelper.getTravianCommandQueue().put(cmd);
                default:
                    LOG.debug("got cmd for main:"+cmd);
            }
            Thread.sleep(TimeUtil.seconds(15));
        }
    }

    public static synchronized BlockingQueue<Command> getMainCommandQueue(){
        return mainCommandQueue;
    }
}