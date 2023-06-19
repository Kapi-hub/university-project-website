package dao;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Properties;

/**
 * Singleton pattern.
 */
public enum MailService {
    MAIL;

    public static final String SHOTMANIACS_MAIL = "shotmaniacs.photography@gmail.com";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final String APPLICATION_NAME = "Shotmaniacs Mail Service";
    public static boolean success = false;
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private GoogleAuthorizationCodeFlow flow;
    private String token;
    private Gmail service;
    private NetHttpTransport HTTP_TRANSPORT;

    public static void main(String[] args) throws MessagingException, IOException {
        MAIL.sendMessage("bfc.jonkhout@gmail.com", "TEST SUBJECT", "TEST BODY");
    }

    public String printAuthURL(String clientId, String clientSecret) {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("Could not create HTTP_TRANSPORT" + e.getMessage());
            return null;
        }
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientId, clientSecret, Collections.singleton(GmailScopes.GMAIL_SEND))
                .setAccessType("offline")
                .build();
        // Obtain the authorization URL
        String authURL = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("\n\nPlease open the following URL to receive your cool token:");
        System.out.println(authURL);
        System.out.println("\n");
        return authURL;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void connect() {
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
        success = true;
    }

    public boolean isConnected() {
        return success;
    }

    public Credential getCredentials() {
        try {
            flow.newTokenRequest(token).setRedirectUri(REDIRECT_URI).execute();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException e) {
            System.err.println("An error has occurred setting up the mail service.");
            e.printStackTrace();
        }
        return null;
    }

    public void deactivate() {
        success = false;
        service = null;
    }

    private MimeMessage createEmail(String subject, String body)
            throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SHOTMANIACS_MAIL));
        message.setSubject(subject);
        message.setText(body);

        return message;
    }

    public void sendMessage(String recipient, String subject, String body) throws MessagingException, IOException {
        sendMessage(recipient, createEmail(subject, body));
    }

    private void sendMessage(String recipient, MimeMessage mimeMessage)
            throws IOException, MessagingException {
        mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
        Message message = createMessageWithEmail(mimeMessage);
        service.users().messages().send(SHOTMANIACS_MAIL, message).execute();
        System.out.printf("===MAIL=== Mail has successfully been sent to %s.\n", recipient);
    }

    /**
     * Turns it into byte array to allow it to be sent.
     */
    private Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
