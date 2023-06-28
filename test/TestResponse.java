import jakarta.ws.rs.core.Response;

public class TestResponse {
    private final Response response;

    public TestResponse(Response response) {
        this.response = response;
    }

    public Object getEntity() {
        return response.getEntity();
    }

    public int getStatus() {
        return response.getStatus();
    }

    // And so on, for all other methods you want to delegate...

    @Override
    public String toString() {
        // Your custom toString implementation here
        return "TestResponse with status: " + response.getStatus();
    }
}