package resources;

import dao.ClientDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import models.ClientBean;
import models.EventBean;
import models.EventType;
import models.FormBean;


import java.sql.Date;
import java.sql.Time;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/client")
public class ClientResource {
    @Path("/submit-form")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void handleSubmit(FormBean formBean) {
        try {
            int client_id = ClientDao.I.addClient(formBean.getClientBean());
            formBean.getEventBean().setClient_id(client_id);
            ClientDao.I.addEvent(formBean.getEventBean());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
