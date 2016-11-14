package luke.zhou;

import luke.zhou.model.Command;

import java.io.Console;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 4:27 PM
 */
public class ControlCenter implements Runnable
{
    @Override
    public void run()
    {
        Console c = System.console();
        if (c == null)
        {
            System.err.println("No console.");
            System.exit(1);
        }

        while (true)
        {
            String cmd = c.readLine("Enter your command: ");
            try
            {
                Main.getMainCommandQueue().put(Command.valueOf(cmd));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
