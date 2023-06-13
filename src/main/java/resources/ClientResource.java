package resources;

//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dao.MailService;
import models.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ClientDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import models.RoleType.*;

import javax.mail.MessagingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dao.MailService.MAIL;

@Path("/client")
public class ClientResource {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
               """, client.getForename(), event.getStart(), event.getId() ,event.getClient_id());
        String recipient = client.getEmail_address();
        try {
            MAIL.sendMessage(recipient, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            e.printStackTrace();
        }
    }

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
                """, client.getForename(), client.getAccountId(), client.getForename(),
                client.getSurname(), client.getPhone_number(), client.getEmail_address());
        String recipient = client.getEmail_address();
        try {
            MAIL.sendMessage(recipient, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            e.printStackTrace();
        }
    }

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
                """, client.getAccountId(), client.getForename(),
                client.getSurname(), client.getPhone_number(), client.getEmail_address());
        try {
            MAIL.sendMessage(MailService.SHOTMANIACS_MAIL, subject, body);
        } catch (MessagingException | IOException e) {
            System.err.println("An error has occurred when sending the confirmation message.");
            e.printStackTrace();
        }

    }

    @Path("/submit-form-multiple")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void handleSubmitMultiple(@FormDataParam("excel_template") InputStream excelStream,
                                     @FormDataParam("excel_template") FormDataContentDisposition fileDetails,
                                     @FormDataParam("client_data") String clientDataJson) {
        System.out.println("RECEIVED POST REQUEST");
        try {
            ClientBean clientBean = new ObjectMapper().readValue(clientDataJson, ClientBean.class);
            int client_id = ClientDao.I.addClient(clientBean);
            switch (fileDetails.getFileName().split("\\.")[1]) {
                case "xlsx", "xlx" -> handleExcelFile(excelStream, client_id);
                case "csv" -> handleCsvFile(excelStream, client_id);
                default -> throw new IOException("Incorrect file has been uploaded.");
            }
            clientBean.setAccountId(client_id);
            sendConfirmationMultiple(clientBean);
            sendConfirmationToMe(clientBean);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    private void handleExcelFile(InputStream excel, int client_id) throws IOException, SQLException {
        Workbook booking = new XSSFWorkbook(excel);
        Sheet sheet = booking.getSheetAt(0);
        int i = 1;
        while (true) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < 13; j++) {
                if (row.getCell(j).getCellType() == CellType.BLANK) return;
            }
            EventBean eventBean = new EventBean(
                    row.getCell(0).getStringCellValue(),
                    row.getCell(5).getStringCellValue(),
                    Timestamp.valueOf(row.getCell(2).getStringCellValue()+":00.000"),
                    (int) row.getCell(3).getNumericCellValue(),
                    row.getCell(4).getStringCellValue(),
                    EventType.valueOf(row.getCell(1).getStringCellValue())
            );
            eventBean.setClient_id(client_id);
            int event_id = ClientDao.I.addEvent(eventBean);
            RoleType[] roles = {RoleType.PHOTOGRAPHER, RoleType.VIDEOGRAPHER, RoleType.EDITOR, RoleType.ASSISTANT,
                    RoleType.DATA_HANDLER, RoleType.PLANNER, RoleType.PRODUCER};
            int j = 6;
            for (RoleType role : roles) {
                RequiredCrewBean requiredCrewBean = new RequiredCrewBean(
                        event_id, role, (int) row.getCell(j).getNumericCellValue()
                );
                ClientDao.I.addRequirement(requiredCrewBean);
                j++;
            }
            i++;

        }


    }

    private void handleCsvFile(InputStream csv, int client_id) throws SQLException, IOException {
        var br = new BufferedReader(new InputStreamReader(csv));
        String line = br.readLine();
        line = br.readLine();
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values.length < 13) {
                break;
            }
            EventBean eventBean = new EventBean(
                    values[0],
                    values[5],
                    Timestamp.valueOf(values[2]+":00.000"),
                    Integer.parseInt(values[3]),
                    values[4],
                    EventType.valueOf(values[1])
            );
            eventBean.setClient_id(client_id);
            int event_id = ClientDao.I.addEvent(eventBean);
            RoleType[] roles = {RoleType.PHOTOGRAPHER, RoleType.VIDEOGRAPHER, RoleType.EDITOR, RoleType.ASSISTANT,
                    RoleType.DATA_HANDLER, RoleType.PLANNER, RoleType.PRODUCER};
            int j = 6;
            for (RoleType role : roles) {
                RequiredCrewBean requiredCrewBean = new RequiredCrewBean(
                        event_id, role, Integer.parseInt(values[j])
                );
                ClientDao.I.addRequirement(requiredCrewBean);
            }
        }


    }
}