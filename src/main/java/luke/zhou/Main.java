package luke.zhou;
/**
 * Created by Luke on 12/11/16.
 */

import luke.zhou.io.MailIO;
import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
import luke.zhou.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main
{
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static BlockingQueue<Command> mainCommandQueue = new LinkedBlockingQueue<>();

    public static boolean isDebug = false;


    public static void main(String[] args) throws InterruptedException
    {

        String login;
        String password;
        String notificationEmail;

        if (isDebug)
        {
            System.out.println("Debug Mode....................");
            login = "tdxh20";
            password = "********";
            notificationEmail = "Sharlock@gmail.com";
        }
        else
        {
            Console c = System.console();
            if (c == null)
            {
                System.err.println("No console.");
                System.exit(1);
            }

            System.out.println("Welcome to Travian Helper");
            login = c.readLine("Enter your login: ");
            password = c.readLine("Enter your password: ");
            notificationEmail = c.readLine("Enter your notification email: ");

        }

        LOG.debug(login + "/" + password + "/" + notificationEmail);

        Game game = new Game();
        game.setUsername(login);
        game.setPassword(password);
        game.setNotificationEmail(notificationEmail);

        TravianHelper travianHelper = new TravianHelper(game);
        new Thread(travianHelper).start();

        System.out.print("Loading");
        while (true)
        {
            Command cmd = mainCommandQueue.poll();
            if (cmd != null && Command.READY.equals(cmd)) break;
            System.out.print(".");
            Thread.sleep(TimeUtil.seconds(2));
        }
        System.out.println();
        new Thread(new ControlCenter()).start();

        while (true)
        {
            Command cmd = mainCommandQueue.poll();
            if (cmd != null)
            {
                LOG.debug("got cmd for main:" + cmd);
                switch (cmd)
                {
                    case RAID:
                    case REPEAT_RAID:
                    case STOP_RAID:
                    case GET_INFO:
                    case EXIT:
                        travianHelper.getTravianCommandQueue().put(cmd);
                        break;
                }
            }
            Thread.sleep(TimeUtil.seconds(15));
        }
    }

    public static synchronized BlockingQueue<Command> getMainCommandQueue()
    {
        return mainCommandQueue;
    }
}
