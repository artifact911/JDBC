package com.artifact.jdbc.starter.util.pool;

import com.artifact.jdbc.starter.util.PropertiesUtil;
import lombok.SneakyThrows;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPoolManager {

    private static final String USER_NAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String URL_KEY = "db.url";
    private static final String POLL_SIZE_KEY = "db.pool.size";
    public static final Integer DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;

    // для работы с Java < 1.7 -> мы явно указали какой драйвер юзать - загрузили его в память (метаСпейс)
    static {
        loadDriver();
        initConnectionPool();
    }

    private ConnectionPoolManager() {
    }

    private static void initConnectionPool() {
        var poolSize = PropertiesUtil.get(POLL_SIZE_KEY);
        var size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            var connection = open();
            var proxyConnection = (Connection)
                    Proxy.newProxyInstance(ConnectionPoolManager.class.getClassLoader(), new Class[]{Connection.class},
                                   (proxy, method, args) -> method.getName().equals("close")
                                                            ? pool.add((Connection) proxy)
                                                            : method.invoke(connection, args));
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }

    @SneakyThrows
    public static Connection get() {
        return pool.take();
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_NAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
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

    @SneakyThrows
    public static void closePool() {
        for (Connection sourceConnection : sourceConnections) {
            sourceConnection.close();
        }
    }
}
