package client;

import misc.ConnectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNull;

public class TestConnectionFactory {
    Connection connection;

    @Test
    public void TestSetup() {
        assertNull(connection);
        ConnectionFactory.setup();
        connection = ConnectionFactory.getConnection();
        Assertions.assertNotNull(connection);

    }
}
