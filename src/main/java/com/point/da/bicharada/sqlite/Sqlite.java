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

        statement.executeUpdate(
                "create table if not exists animais (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tutor_cpf TEXT," +
                        "nome TEXT," +
                        "raca TEXT," +
                        "nascimento TEXT," +
                        "especie TEXT," +
                        "FOREIGN KEY(tutor_cpf) REFERENCES clientes(cpf)" +
                        ")");

        statement.executeUpdate(
                "create table if not exists agendamentos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "tutor_cpf TEXT," +
                        "data TEXT," +
                        "FOREIGN KEY(tutor_cpf) REFERENCES clientes(cpf)," +
                        "UNIQUE(tutor_cpf, data)" +
                        ")");

        statement.executeUpdate(
                "create table if not exists agendamentos_servicos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "agendamento_id INTEGER," +
                        "animal_id INTEGER," +
                        "animal_especie TEXT," +
                        "servico TEXT," +
                        "FOREIGN KEY(agendamento_id) REFERENCES agendamentos(id)," +
                        "FOREIGN KEY(animal_id) REFERENCES animais(id)" +
                        ")");

        return conn;
    }
}
