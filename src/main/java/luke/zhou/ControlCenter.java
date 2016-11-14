package luke.zhou;

import luke.zhou.model.Command;

import java.io.Console;
import java.util.Arrays;

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
                if(Command.HELP.equals(Command.get(cmd))){
                    displayHelpInfo();
                }
                Main.getMainCommandQueue().put(Command.get(cmd));
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void displayHelpInfo()
    {
        Arrays.stream(Command.values()).forEach(System.out::println);
    }
}
