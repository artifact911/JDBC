package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
        String flightId = "2";
        String flightIdHack = "2 OR '' = ''; DROP TABLE game.info";
        System.out.println(getTicketsByFlightIs(flightIdHack));

    }

    @SneakyThrows
    private static List<Long> getTicketsByFlightIs(String flightId) {
        String sql = """
                select id
                from ticket
                where flight_id = %s
                """.formatted(flightId);
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                // getObject позволяет нам, в случает если придет null получиль его безопасно и распрарсить в обертку Long, т.к.
                // по-умолчнию вернется примитив long и мы не сможем положить туда null
                result.add(resultSet.getObject("id", Long.class));
            }
        }
        return result;
    }
}

