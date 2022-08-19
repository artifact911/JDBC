package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        Class<Driver> driverClass = Driver.class;

        String sql = """
               select *
               from ticket
                """;

        try (var connection = ConnectionManager.open();
             var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

            System.out.println(connection.getSchema());
            System.out.println(connection.getTransactionIsolation());

            // ResultSet может быть использован не только для SELECT. Для этого в конструктор Statement нужно передать
            // connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE), т.к. по-умолчания это ReadOnly.  ->
            // -> Плохая практика.
            // ResultSet должен быть закрыт, как и Statement. Но в доке написано, что он будет закрыт автоматически, как только закроется
            // Statement, который его открыл.
            var executeResult = statement.executeQuery(sql);
            while (executeResult.next()) {
                System.out.println(executeResult.getLong("id"));
                System.out.println(executeResult.getString("passenger_no"));
                System.out.println(executeResult.getBigDecimal("cost"));
                System.out.println("---------");
            }
        }
    }
}

