/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sourcecoding.pb.business.mail.boundary;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Matthias Reining
 */
public class MailServiceTest {

    @Test
    public void shouldSendMeAnEMail() throws IOException {
        System.out.println("blub");
        //GmailQuickStart.authorize();
        
        
        MailService ms = new MailService();
        ms.send("matthias.reining@outlook.com", "Test-Mail von ConAIR", "blub blu");

    }

}
