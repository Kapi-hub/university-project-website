package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.IOException;

import static dao.MailService.MAIL;
import static org.junit.jupiter.api.Assertions.*;

public class TestMailService {

    @AfterEach
    public void testSuccess() {
        assertTrue(MAIL.success);
    }

    @Test
    public void testCreatingMimeMessage() throws MessagingException, IOException {
        String subject = "Subject Test";
        String body = "Body Test";

        MimeMessage mimeMessage = MAIL.createEmail(subject, body);

        assertNotNull(mimeMessage);
        assertEquals(subject, mimeMessage.getSubject());
        assertEquals(body, mimeMessage.getContent());
    }

    @Test
    public void testSendMessage() throws IOException, MessagingException {
        String recipient = "example@example.com"; // Provide a valid recipient email address
        String subject = "Subject Test";
        String body = "Body Test";

        MimeMessage mimeMessage = MAIL.createEmail(subject, body);

        MAIL.sendMessage(recipient, mimeMessage);
    }

}
