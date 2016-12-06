package luke.zhou;
/**
 * Created by Luke on 12/11/16.
 */

import luke.zhou.model.Command;
import luke.zhou.model.travian.Game;
import luke.zhou.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main
{
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String DEFAULT_LOGIN ="tdxh20";
    private static final String DEFAULT_NOTIFICATION_EMAIL ="sharlock@gmail.com";

    private static BlockingQueue<Command> mainCommandQueue = new LinkedBlockingQueue<>();


    public static boolean isDebug = false;


    public static void main(String[] args) throws InterruptedException
    {

        String login;
        String password;
        String notificationEmail;

        Console c = System.console();

        if (isDebug)
        {
            System.out.println("Debug Mode....................");
            login = DEFAULT_LOGIN;
            password = "*******";
            notificationEmail = DEFAULT_NOTIFICATION_EMAIL;
        }
        else
        {
            if (c == null)
            {
                System.err.println("No console.");
                System.exit(1);
            }

            System.out.println("Welcome to Travian Helper");
            notificationEmail = c.readLine("Enter your notification email(sharlock@gmail.com): ");
            login = c.readLine("Enter your login(tdxh20): ");
            password = c.readLine("Enter your password: ");
        }

        LOG.debug(login + "/" + password + "/" + notificationEmail);
        if (login.isEmpty()) login = DEFAULT_LOGIN;
        if (notificationEmail.isEmpty()) notificationEmail = DEFAULT_NOTIFICATION_EMAIL;

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

        if (isDebug)
        {
            //simulate the action which needs to test
            travianHelper.getTravianCommandQueue().put(Command.CLEAN_MESSAGE);
        }
        else
        {
            do {
                System.out.println();
                handleConsoleInput(c, travianHelper);
            }
            while (waitACK());
        }
    }

    private static boolean waitACK() throws InterruptedException
    {
        while (true)
        {
            Command cmd = mainCommandQueue.poll();
            if (cmd != null && Command.READY.equals(cmd)) return true;
        }
    }

    private static void handleConsoleInput(Console c, TravianHelper travianHelper) throws InterruptedException
    {
        String cmd = c.readLine("Enter your command: ");

        Command inputCmd = Command.get(cmd);
        LOG.debug("got cmd from console:" + inputCmd);
        if (inputCmd == null)
        {
            System.out.println("Can not recognize the command: " + cmd);
            mainCommandQueue.add(Command.READY);
        }
        else if (Command.HELP.equals(inputCmd))
        {
            displayHelpInfo();
            mainCommandQueue.add(Command.READY);
        }
        else
        {
            travianHelper.getTravianCommandQueue().put(inputCmd);
        }
    }

    private static void displayHelpInfo()
    {
        Arrays.stream(Command.values()).filter(c-> c.isVisible()).forEach(System.out::println);
    }

    public static synchronized BlockingQueue<Command> getMainCommandQueue()
    {
        return mainCommandQueue;
    }
}
