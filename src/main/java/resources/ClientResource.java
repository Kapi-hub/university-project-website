package resources;

import dao.ClientDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import models.FormBean;
import models.RequiredCrewBean;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
