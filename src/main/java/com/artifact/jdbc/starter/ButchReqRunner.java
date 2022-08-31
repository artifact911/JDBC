package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.Statement;

public class ButchReqRunner {

    @SneakyThrows
    public static void main(String[] args) {

        long flightId = 9L;
        delFlightByIdV3(flightId);
    }

    @SneakyThrows
    public static void delFlightByIdV3(long flightId) {
        var deleteFlightSql = "DELETE FROM flight WHERE id = " + flightId;
        var deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = " + flightId;

        Connection connection = null;
        Statement statement = null;

        // теперь не можем юзать try-with-resources и придется закрывать все соединения вречную после использования
        try {
            connection = ConnectionManager.open();

            // выключили автоКоммит и взяли управлени транзакциями на себя. !!! Делать это нужно в самом начале, до выполнения любых
            // запросов!!!
            // нужно не забывать возвращать в дефолтное состояние, если мы используем пулл соединений.
            connection.setAutoCommit(false);

            // prepareStatement() не могут делать Batch-запросы. Нужно юзать statement()
            statement = connection.createStatement();
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);


            // выполняются все batch-запросы. Возвращается массив результатов выполнения (т.е. int для каждой их наших команд), а
            // следовательно выполнение SELECT для Batch-запросов не используется, а только для DELETE/UPDATE/INSERT или DDL (создания
            // таблиц, индексов и т.п.) И т.к. это все выполнится в рамках одной транзакции, значит выполняться либо все запросы, либо ни
            // одного.
            var ints = statement.executeBatch();


            // теперь после всех запросов нам нужно делать коммит - это фиксирование нашей транзакции. !!! Нельзя вызвать методы commit() и
            // rollback(), если автоКоммит=true - будет исключение!!!
            connection.commit();

        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
}
