package dao;

import misc.ConnectionFactory;

import java.sql.Connection;

/**
 * Singleton Pattern
 */
public enum ClientDao {
    I;

    Connection connection;

    ClientDao() {
        connection = ConnectionFactory.getConnection()
    }
}
