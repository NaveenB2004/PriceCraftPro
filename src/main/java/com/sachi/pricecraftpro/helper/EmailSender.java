package com.sachi.pricecraftpro.helper;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSender {

    // Recipient's email ID needs to be mentioned.
    private String to;
    // Sender's email ID needs to be mentioned
    private String from;

    private String username;//change accordingly
    private String password;//change accordingly

    private String subject;
    private String body;
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public void setFrom(String from) {
        this.from = from;
        this.username = from;
    }
    
    public void setKey(String key) {
        this.password = key;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    public boolean sendMail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(body);

            // Send message
            Transport.send(message);

            return true;

        } catch (AddressException ex) {
            Logger.getLogger(EmailSender.class.getName())
                    .log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSender.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
