package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String sql = """
                update game.info 
                set data = 'TestTest'
                where id = 5
                returning *
               
                """;

        try (var connection = ConnectionManager.open();
             var statement = connection.createStatement()) {

            System.out.println(connection.getTransactionIsolation());
            final var executeResult = statement.execute(sql);
            System.out.println(executeResult);
            System.out.println(statement.getUpdateCount());
        }
    }
}

