/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.mail.boundary;

import java.util.Properties;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Matthias
 */
@Stateless
public class MailService {

    public void send(String mailTo, String subject, String body) {
        sendMail(mailTo, subject, body);
    }

    @Asynchronous
    public void asyncSend(String mailTo, String subject, String body) {
        sendMail(mailTo, subject, body);
    }

    private void sendMail(String mailTo, String subject, String body) {

        final String u = "conair-deutschland@outlook.com";
        final String p = "DKzuK1DKynsfdfK6n3pm";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp-mail.outlook.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        System.out.println(props);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(u, p);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(u));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mailTo));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

            System.out.println("Mail send done...");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
