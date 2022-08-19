package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String sql = """
                create table if not exists game.info (
                    id serial primary key,
                    data text not null 
                    );
                """;

        try (var connection = ConnectionManager.open();
             var statement = connection.createStatement()) {

            System.out.println(connection.getTransactionIsolation());
            final var executeResult = statement.execute(sql);
            System.out.println(executeResult);
        }
    }
}

