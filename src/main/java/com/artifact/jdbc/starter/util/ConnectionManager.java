package com.artifact.jdbc.starter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private ConnectionManager() {
    }

    // для работы с Java < 1.7 -> мы явно указали какой драйвер юзать - загрузили его в память (метаСпейс)
    static {
        loadDriver();
    }

    private static final String USER_NAME = "postgres";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:postgresql://localhost:5432/company_repository";

    public static Connection open() {
        try {
            return DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
