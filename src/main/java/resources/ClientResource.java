package resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ClientDao;
import dao.MailService;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import models.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;

import static dao.MailService.MAIL;

@PermitAll
@Path("/client")
public class ClientResource {
    /**
     * Handles an incoming request for all the information necessary.
     *
     * @param formBean this is all the data transmit by the client.
     */
    @PermitAll
    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleSubmit(FormBean formBean) {
        try {
            int client_id = ClientDao.I.addClient(formBean.getClientBean());
            formBean.getEventBean().setClient_id(client_id);
            int event_id = ClientDao.I.addEvent(formBean.getEventBean());
            for (RequiredCrewBean required : formBean.getRequiredCrewBeans()) {
                required.setEvent_id(event_id);
                ClientDao.I.addRequirement(required);
            }
            formBean.getEventBean().setId(event_id);
            sendConfirmation(formBean);
            sendConfirmationToMe(formBean.getClientBean());

        } catch (NullPointerException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sends a confirmation email to the client
     *
     * @param formBean using this as information
     */
    private void sendConfirmation(FormBean formBean) {
        ClientBean client = formBean.getClientBean();
        EventBean event = formBean.getEventBean();
        String subject = String.format("Confirmation Booking of event - %s", event.getName());
        String body = String.format(
                """
                        Hi %s!
                                       
                        This is a confirmation email based on the event you have booked on %s.
                        Your event id is %s. Your client_id is %s.
                        Shotmaniacs will get in contact with you.
                                       
                        Sincerely,
                        The Shotmaniacs Team
                        """, client.getForename(), event.getStart(), event.getId(), event.getClient_id());
        String recipient = client.getEmailAddress();
        try {
            MAIL.sendMessage(recipient, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Send a confirmation email to a client who has used multiple bookings.
     *
     * @param client the client it needs to send it to
     */
    private void sendConfirmationMultiple(ClientBean client) {
        String subject = "Confirmation Booking of multiple events";
        String body = String.format(
                """
                        Hi %s!
                                        
                        This is a confirmation email based on the events you have booked.
                                        
                        Your information.
                        - Client-id: %s
                        - Name: %s %s
                        - Telephone: %s
                        - Email: %s
                                        
                        Shotmaniacs will get in contact with you.
                                        
                        Sincerely,
                        The Shotmaniacs Team
                        """, client.getForename(), client.getId(), client.getForename(),
                client.getSurname(), client.getPhone_number(), client.getEmailAddress());
        String recipient = client.getEmailAddress();
        try {
            MAIL.sendMessage(recipient, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sends a confirmation of a booking to shotmaniacs itself.
     *
     * @param client the client information is sent in the body.
     */
    private void sendConfirmationToMe(ClientBean client) {
        String subject = "New Booking has arrived.";
        String body = String.format(
                """
                        Dear Shotmaniacs Team!
                                        
                        A client has signed up with bookings. Please consult the admin dashboard to edit the event.
                                        
                        The client information:
                        - Client-id: %s
                        - Name: %s %s
                        - Telephone: %s
                        - Email: %s

                        Sincerely,
                        The computer behind Shotmaniacs.
                        """, client.getId(), client.getForename(),
                client.getSurname(), client.getPhone_number(), client.getEmailAddress());
        try {
            MAIL.sendMessage(MailService.SHOTMANIACS_MAIL, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            System.err.println(e.getMessage());
        }

    }

    /**
     * Handles the file uploading in a form submit multiple
     *
     * @param excelStream    the Excel/CSV file
     * @param fileDetails    the metadata about the file
     * @param clientDataJson the client data in json format.
     */
    @Path("/submit-form-multiple")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void handleSubmitMultiple(@FormDataParam("excel_template") InputStream excelStream,
                                     @FormDataParam("excel_template") FormDataContentDisposition fileDetails,
                                     @FormDataParam("client_data") String clientDataJson) {
        try {
            if (fileDetails.getFileName() == null) throw new IOException("No file has been included.");
            ClientBean clientBean = new ObjectMapper().readValue(clientDataJson, ClientBean.class);
            int client_id = ClientDao.I.addClient(clientBean);
            switch (fileDetails.getFileName().split("\\.")[1]) {
                case "xlsx", "xlx" -> handleExcelFile(excelStream, client_id);
                case "csv" -> handleCsvFile(excelStream, client_id);
                default -> throw new IOException("Incorrect file has been uploaded.");
            }
            clientBean.setId(client_id);

            sendConfirmationMultiple(clientBean);
            sendConfirmationToMe(clientBean);

        } catch (SQLException | IOException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * A method that can handle a file input stream from excel and adds uses ClientDAO to add it to the database.
     *
     * @param excel     the excel file
     * @param client_id the client_id it belongs to
     * @throws IOException  an error with the communication and file uploading
     * @throws SQLException an error with the database
     */
    public void handleExcelFile(InputStream excel, int client_id) throws IOException, SQLException {
        Workbook booking = new XSSFWorkbook(excel);
        Sheet sheet = booking.getSheetAt(0);
        int i = 1;
        try {
            while (true) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < 14; j++) {
                    if (row.getCell(j).getCellType() == CellType.BLANK) return;
                }
                EventBean eventBean = new EventBean(
                        row.getCell(0).getStringCellValue(),
                        row.getCell(5).getStringCellValue(),
                        new Timestamp(row.getCell(2).getDateCellValue().getTime()),
                        (int) row.getCell(3).getNumericCellValue(),
                        row.getCell(4).getStringCellValue(),
                        EventType.valueOf(row.getCell(1).getStringCellValue()),
                        BookingType.valueOf(row.getCell(6).getStringCellValue())
                );
                eventBean.setClient_id(client_id);
                int event_id = ClientDao.I.addEvent(eventBean);
                RoleType[] roles = {RoleType.PHOTOGRAPHER, RoleType.VIDEOGRAPHER, RoleType.EDITOR, RoleType.ASSISTANT,
                        RoleType.DATA_HANDLER, RoleType.PLANNER, RoleType.PRODUCER};
                int j = 7;
                for (RoleType role : roles) {
                    RequiredCrewBean requiredCrewBean = new RequiredCrewBean(
                            event_id, role, (int) row.getCell(j).getNumericCellValue()
                    );
                    ClientDao.I.addRequirement(requiredCrewBean);
                    j++;
                }
                i++;
            }
        } catch (NullPointerException e) {
            System.err.println("Could not parse Excel File");
        }
    }

    /**
     * A method that can handle a CSV file and parse it to fit in the database
     *
     * @param csv       the file it needs to parse
     * @param client_id the client_id the file belongs to
     * @throws SQLException an error with the databased
     * @throws IOException  an error with the communication or file uploading
     */
    public void handleCsvFile(InputStream csv, int client_id) throws SQLException, IOException {
        var br = new BufferedReader(new InputStreamReader(csv));
        br.readLine(); // skip the first two lines
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values.length < 14) {
                break;
            }
            EventBean eventBean = new EventBean(
                    values[0],
                    values[5],
                    Timestamp.valueOf(values[2]+":00.000"),
                    Integer.parseInt(values[3]),
                    values[4],
                    EventType.valueOf(values[1]),
                    BookingType.valueOf(values[6])
            );
            eventBean.setClient_id(client_id);
            int event_id = ClientDao.I.addEvent(eventBean);
            RoleType[] roles = {RoleType.PHOTOGRAPHER, RoleType.VIDEOGRAPHER, RoleType.EDITOR, RoleType.ASSISTANT,
                    RoleType.DATA_HANDLER, RoleType.PLANNER, RoleType.PRODUCER};
            int j = 7;
            for (RoleType role : roles) {
                RequiredCrewBean requiredCrewBean = new RequiredCrewBean(
                        event_id, role, Integer.parseInt(values[j])
                );
                ClientDao.I.addRequirement(requiredCrewBean);
            }
        }
    }
}