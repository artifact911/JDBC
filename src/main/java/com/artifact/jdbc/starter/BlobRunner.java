package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.util.ConnectionManager;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class BlobRunner {

    public static void main(String[] args) {
//        saveImage();
        getImage();
    }

    @SneakyThrows
    private static void getImage() {
        var sql = """
                SELECT image
                FROM aircraft
                WHERE id = ?
                """;

        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, 1);
            var resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                var image = resultSet.getBytes("image");
                Files.write(Path.of("resources", "777-banner-new.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }

    @SneakyThrows
    private static void saveImage() {

        var sql = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 1
                """;

        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {

            var bytes = Files.readAllBytes(Path.of("resources", "777-banner.jpg"));

            preparedStatement.setBytes(1, bytes);
            preparedStatement.executeUpdate();
        }
    }


   /* @SneakyThrows
    // это не сработаем в PostgreSQL
    private static void saveImage() {

        var sql = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 1
                """;

        try (var connection = ConnectionManager.open();
             var preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            var blob = connection.createBlob();
            blob.setBytes(1, Files.readAllBytes(Path.of("resources", "777-banner.jpg")));

            preparedStatement.setBlob(1, blob);
            preparedStatement.executeUpdate();
            connection.commit();
        }
    }*/
}
