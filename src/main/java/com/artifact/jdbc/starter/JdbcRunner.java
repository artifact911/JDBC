package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import com.artifact.jdbc.starter.util.pool.ConnectionPoolManager;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {

    public static void main(String[] args) throws SQLException {
//        Long flightId = 2L
//        var result = getTicketsByFlightIs(flightId);
//        System.out.println(result);

//        var result = getFlightsBetween(LocalDate.of(2020, 1, 1).atStartOfDay(), LocalDateTime.now());
//        System.out.println(result);

        try {
            checkMetaData();
        } finally {
            ConnectionPoolManager.closePool();
        }
    }

    @SneakyThrows
    public static void checkMetaData() {
        try (var connection = ConnectionPoolManager.get()) {

            // позволит нам доставать метаинформацию
            var metaData = connection.getMetaData();
            // можем получить все подключенные БД
            var catalogs = metaData.getCatalogs();

            while (catalogs.next()) {
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();

                while (schemas.next()) {
                    var schema = schemas.getString("TABLE_SCHEM");

                    // вернул все сущности
//                    var tables = metaData.getTables(catalog, schema, "%", null);

                    // хочу получить только таблицы
                    var tables = metaData.getTables(catalog, schema, "%", new String[] {"TABLE"});

                    if (schema.equals("public")) {
                        while (tables.next()) {
                            // параметр взят из доки к методу getTables();
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }

    @SneakyThrows
    private static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) {
        String sql = """
                select id
                from flight
                where departure_date between ? and ?
                """;
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {

            // этот параметр устанавливает количество строк, которые мы возьмём из базы за один раз, чтоб не взять лишнего и не положить
            // приложение из-за недотсатка памяти. Данные будем получать итерационно
            preparedStatement.setFetchSize(50);

            // установка тайм-аута для выполнения запроса
            preparedStatement.setQueryTimeout(10);

            // устанавливаем лимит на количество принимаемых строк
            preparedStatement.setMaxRows(100);

            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(preparedStatement);

            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
        }
        return result;
    }

    @SneakyThrows
    private static List<Long> getTicketsByFlightIs(Long flightId) {
        String sql = """
                select id
                from ticket
                where flight_id = ?
                """;
        List<Long> result = new ArrayList<>();
        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, flightId);

            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // getObject позволяет нам, в случает если придет null получиль его безопасно и распрарсить в обертку Long, т.к.
                // по-умолчнию вернется примитив long и мы не сможем положить туда null
                result.add(resultSet.getObject("id", Long.class));
            }
        }
        return result;
    }
}

