package com.artifact.jdbc.starter.dao;

import com.artifact.jdbc.starter.entity.Flight;
import com.artifact.jdbc.starter.exception.DaoException;
import com.artifact.jdbc.starter.util.pool.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {

    private static final FlightDao INSTANCE = new FlightDao();

    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                flight_no,
                departure_date,
                departure_airport_code,
                arrival_date,
                arrival_airport_code,
                status,
                aircraft_id    
            FROM flight
            WHERE id = ?
            """;

    private FlightDao() {
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public Flight save(Flight ticket) {
        return null;
    }

    @Override
    public Flight update(Flight ticket) {
        return null;
    }

    // Перегрузили метод, чтобы использовать один и тот же Connection и передавать его в другой метод
    @Override
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionPoolManager.get()) {

            return findById(id, connection);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Flight> findById(Long id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            Flight flight = null;
            if (resultSet.next()) {
                flight = new Flight(
                        resultSet.getLong("id"),
                        resultSet.getString("flight_no"),
                        resultSet.getTimestamp("departure_date").toLocalDateTime(),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getInt("aircraft_id"),
                        resultSet.getString("status")
                );
            }
            return Optional.ofNullable(flight);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Flight> findAll() {
        return null;
    }
}
