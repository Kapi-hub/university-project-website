package resources;

import dao.ClientDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import models.ClientBean;
import models.EventBean;
import dao.ClientDao;
import models.EventType;


import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/client")
public class ClientResource {
    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleSubmit(Map<String, Object> combinedData) {
        try {
            LinkedHashMap<String, String> client = (LinkedHashMap<String, String>) combinedData.get("client");
            ClientBean clientBean = createClientBean(client);
            int client_id = ClientDao.I.addClient(clientBean);
            LinkedHashMap<String, String> event = (LinkedHashMap<String, String>) combinedData.get("event");
            EventBean eventBean = createEventBean(event, client_id);
            ClientDao.I.addEvent(eventBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClientBean createClientBean(LinkedHashMap<String, String> client) {
        return new ClientBean(client.get("first_name"), client.get("last_name"), client.get("email"), client.get("phone_number"));
    }

    private EventBean createEventBean(LinkedHashMap<String, String> event, int client_id) {
        return new EventBean(event.get("name"), EventType.valueOf(event.get("type")), Date.valueOf(event.get("date")),
                Time.valueOf(event.get("start_time")),
                Integer.parseInt(event.get("duration")),
                event.get("location"), Integer.parseInt(event.get("crew_members")), client_id);
    }


}
