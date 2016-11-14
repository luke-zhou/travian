package luke.zhou.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: LZhou
 * Date: 14/11/2016
 * Time: 2:17 PM
 */
public class MailIO
{
    private static final Logger LOG = LoggerFactory.getLogger(MailIO.class);
    private final static String username = "generalpurpose2017@gmail.com";
    private final static String password = "8G1l3,T0d8y";

    public static void sendUnderAttackAlarm(String notificationEmail)
    {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("generalpurpose2017gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(notificationEmail));
            message.setSubject("You Are Under Attack!!!");
            message.setText("This is an alarm email to inform you. Your village is under attack at the moment");

            Transport.send(message);

            LOG.debug("alarm email send out");

        }
        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
