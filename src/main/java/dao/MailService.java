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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Properties;

/**
 * Class to give a nice interface to send emails on behalf of shotmaniacs.
 * Singleton pattern.
 */
public enum MailService {
    MAIL;

    public static final String SHOTMANIACS_MAIL = "shotmaniacs.photography@gmail.com";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final String APPLICATION_NAME = "Shotmaniacs Mail Service";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public boolean success = false;
    private Gmail service;

    /**
     * Setting up of the credentials for the Google API
     * @param HTTP_TRANSPORT method it transports in
     * @return the credential object used to send emails
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) {
        try {
            String clientId = System.getenv("MAIL_CLIENT_ID");
            String clientSecret = System.getenv("MAIL_CLIENT_SECRET");
            // Set up OAuth 2.0 client credentials
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientId, clientSecret, Collections.singleton(GmailScopes.GMAIL_SEND))
                    .setAccessType("offline")
                    .build();
            String filename = "mail_authentication_token";
            // Obtain the authorization URL
            System.out.println(flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build());
            System.out.printf("Please save the authentication token in %s\\%s\n", System.getProperty("user.dir"), filename);
            Thread.sleep(1000 * 25);
            // Print the authorization URL and ask the user to visit it and authorize the application
            String authorizationCode = new BufferedReader(new FileReader(filename)).readLine().strip();
            System.out.println(authorizationCode);
            // Exchange the authorization code for access token
            flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Creates a mail message with the right format
     * @param subject the subject of the mail
     * @param body the body of the mail
     * @return a MimeMessage object
     * @throws MessagingException whenever an issue with the messaging occurs, it throws this.
     */
    private MimeMessage createEmail(String subject, String body)
            throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SHOTMANIACS_MAIL));
        message.setSubject(subject);
        message.setText(body);

        return message;
    }

    /**
     * A combined method of the two, to make all signatures in one place.
     * @param recipient the recipient of the mail
     * @param subject the subject of the mail
     * @param body the body of the mail
     * @throws MessagingException whenever an issue occurs with the message creation
     * @throws IOException whenever an issue occurs with the byte stream
     */
    public void sendMessage(String recipient, String subject, String body) throws MessagingException, IOException {
        if (success) {
            sendMessage(recipient, createEmail(subject, body));
        } else {
            System.err.printf("===MAIL=== Cannot send email to %s, because system has not been setup.\n", recipient);
        }
    }

    /**
     * Sends a message to a recipient
     * @param recipient the recipient it will send it to
     * @param mimeMessage the message it will send
     * @throws MessagingException whenever an issue occurs with the message creation
     * @throws IOException whenever an issue occurs with the byte stream
     */
    private void sendMessage(String recipient, MimeMessage mimeMessage)
            throws IOException, MessagingException {
        mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
        Message message = createMessageWithEmail(mimeMessage);
        service.users().messages().send(SHOTMANIACS_MAIL, message).execute();
        System.out.printf("===MAIL=== Mail has successfully been sent to %s.\n", recipient);
    }

    /**
     * Turns it into byte array to allow it to be sent.
     * @param emailContent the mimeMessage it needs to convert
     * @return a byte array that is ready for transport
     * @throws MessagingException whenever an issue occurs with the message creation
     * @throws IOException whenever an issue occurs with the byte stream
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

    /**
     * Sets up the mail class. The mail class can only work if this has been set up.
     */
    public void setup() {
        try {
            System.out.println("MAIL SENDING SETUP HAS BEEN CALLED.");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            success = true;
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("An error has occurred setting up the mail service.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * A public method to determine whether it has been set up.
     * @return a boolean that indicates success or not.
     */
    public boolean isConnected() {
        return success;
    }

}

