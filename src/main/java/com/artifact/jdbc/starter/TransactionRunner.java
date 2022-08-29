package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransactionRunner {

    @SneakyThrows
    public static void main(String[] args) {

        long flightId = 9L;
        delFlightByIdV3(flightId);
    }

    // не смогли удалить таким вариантом, т.к. на строку ссылается другая таблица - ticket. Значит нам нужно удалить все записи связанные
    // с удаляемой из таблицы тикет
    @SneakyThrows
    public static void delFlightByIdV1(long flightId) {
        var deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        try (var connection = ConnectionManager.open();
             var deleteFlightStatement = connection.prepareStatement(deleteFlightSql)) {
            deleteFlightStatement.setLong(1, flightId);
            deleteFlightStatement.executeUpdate();
        }
    }

    // это будет работатать, НО запроса два и транзакции две. Значит, если после удаления из таблица tickets в запросе по удалению
    // перелета случится ошибка, то из тикетс все удалится, а из полетов - нет
    @SneakyThrows
    public static void delFlightByIdV2(long flightId) {
        var deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        var deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = ?";

        try (var connection = ConnectionManager.open();
             var deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
             var deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql)) {
            deleteFlightStatement.setLong(1, flightId);
            deleteTicketsStatement.setLong(1, flightId);

            deleteTicketsStatement.executeUpdate();
            deleteFlightStatement.executeUpdate();
        }
    }

    // нам нужно начать управлять нашими тразакциями вручную! Для этого нужно убрать автоКоммитМод, т.е. чтобы каждый из наших запросов
    // не выполнялся автоматически
    @SneakyThrows
    public static void delFlightByIdV3(long flightId) {
        var deleteFlightSql = "DELETE FROM flight WHERE id = ?";
        var deleteTicketsSql = "DELETE FROM ticket WHERE flight_id = ?";

        // пришлось вынести эти переменные, чтоб заюзать в catch
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketsStatement = null;

        // теперь не можем юзать try-with-resources и придется закрывать все соединения вречную после использования
        try {
            connection = ConnectionManager.open();
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
            deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql);

            // выключили автоКоммит и взяли управлени транзакциями на себя. !!! Делать это нужно в самом начале, до выполнения любых
            // запросов!!!
            // нужно не забывать возвращать в дефолтное состояние, если мы используем пулл соединений.
            connection.setAutoCommit(false);

            deleteFlightStatement.setLong(1, flightId);
            deleteTicketsStatement.setLong(1, flightId);

            deleteTicketsStatement.executeUpdate();
            if (true) {
                throw new RuntimeException("Ooops");
            }
            deleteFlightStatement.executeUpdate();

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
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if(deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
            }
        }
    }
}
