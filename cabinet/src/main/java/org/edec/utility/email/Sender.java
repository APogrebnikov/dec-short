package org.edec.utility.email;

import org.zkoss.zk.ui.Executions;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Sender {

    private static final String EMAIL_USERNAME = "email.username";
    private static final String EMAIL_PASSWORD = "email.password";

    private static final String EMAIL_FROM = "dec-noreply@sfu-kras.ru";
    private static final String SMTP_SERVER = "mail.sfu-kras.ru";
    private static final String SMTP_PORT = "465";

    private ServletContext servletContext;
    private Properties configFile;
    private Session session;
    private String username, password;

    public Sender () throws IOException {
        loadProperties();
        username = configFile.getProperty(EMAIL_USERNAME);
        password = configFile.getProperty(EMAIL_PASSWORD);
        session = auth();
    }

    public Sender (ServletContext servletContext) throws IOException {
        this.servletContext = servletContext;
        loadProperties();
        username = configFile.getProperty(EMAIL_USERNAME);
        password = configFile.getProperty(EMAIL_PASSWORD);
        session = auth();
    }

    public void loadProperties () throws IOException {
        configFile = new Properties();
        String pathConfigFile;
        if (Executions.getCurrent() == null) {
            pathConfigFile = servletContext.getRealPath("WEB-INF/properties/auth.properties");
        } else {
            pathConfigFile = Executions.getCurrent().getDesktop().getWebApp().getRealPath("WEB-INF/properties/auth.properties");
        }
        FileInputStream inputStream = new FileInputStream(pathConfigFile);
        configFile.load(inputStream);
        inputStream.close();
    }

    private Session auth () {
        Properties authProp = new Properties();
        authProp.put("mail.smtp.auth", "true");
        authProp.put("mail.smtp.starttls.enable", "true");
        authProp.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        authProp.put("mail.smtp.host", SMTP_SERVER);
        authProp.put("mail.smtp.port", SMTP_PORT);
        authProp.put("mail.smtp.timeout", 5000);
        authProp.put("mail.smtp.connectiontimeout", 5000);
        Session session = Session.getInstance(authProp, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication () {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }

    public void sendSimpleMessage (String emailTo, String subject, String msg) throws MessagingException {

        if (emailTo == null || emailTo.equals("") || msg == null || msg.equals("") || subject == null || subject.equals("")) {
            MessagingException msgEx = new MessagingException("Отправить e-mail не удаетеся, т.к. не заполнено почта/тема/текст письма!");
            throw msgEx;
        }
        Message message = new CustomMimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
        message.setSubject(subject);
        message.setText(msg);

        Transport.send(message);
    }
}
