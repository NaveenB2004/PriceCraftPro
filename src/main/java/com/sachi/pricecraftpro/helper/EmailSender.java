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
    String to;
    // Sender's email ID needs to be mentioned
    String from;

    String username;//change accordingly
    String password;//change accordingly

    String subject;
    String body;

    public EmailSender(String from,
            String to,
            String password,
            String subject,
            String body) {
        this.from = from;
        this.username = from;
        this.to = to;
        this.subject = subject;
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
