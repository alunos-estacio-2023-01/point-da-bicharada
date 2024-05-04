package com.point.da.bicharada.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Sqlite {
    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:point-data-bicharada.db");

        Statement statement = conn.createStatement();
        statement.setQueryTimeout(5);
        statement.executeUpdate(
                "create table if not exists clientes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "cpf TEXT UNIQUE," +
                        "nome TEXT," +
                        "endereco TEXT," +
                        "telefone TEXT" +
                        ")");

        return conn;
    }
}
