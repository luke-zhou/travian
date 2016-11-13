/**
 * Created by Luke on 12/11/16.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;

public class Main
{
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException
    {
        TravianHelper travianHelper = new TravianHelper();
        travianHelper.start();

    }
}
