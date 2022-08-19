package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String sql = """
                insert into game.info (data)
                values 
                ('Test1'),
                ('Test2'),
                ('Test3'),
                ('Test4');
                """;

        try (var connection = ConnectionManager.open();
             var statement = connection.createStatement()) {

            System.out.println(connection.getTransactionIsolation());
            final var executeResult = statement.executeUpdate(sql);
            System.out.println(executeResult);
            System.out.println(statement.getUpdateCount());
        }
    }
}

