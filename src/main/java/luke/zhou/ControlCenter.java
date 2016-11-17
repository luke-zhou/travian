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

        if(Main.isDebug){
            try
            {
                Main.getMainCommandQueue().put(Command.GET_INFO);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Console c = System.console();

            if (c == null)
            {
                System.err.println("No console.");
                System.exit(1);
            }
            else
            {
                while (true)
                {
                    String cmd = c.readLine("Enter your command: ");
                    try
                    {
                        Command inputCmd = Command.get(cmd);
                        if (inputCmd == null)
                        {
                            System.out.println("Can not recognize the command: " + cmd);
                            continue;
                        }
                        if (Command.HELP.equals(inputCmd))
                        {
                            displayHelpInfo();
                            continue;
                        }
                        Main.getMainCommandQueue().put(inputCmd);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void displayHelpInfo()
    {
        Arrays.stream(Command.values()).forEach(System.out::println);
    }
}
