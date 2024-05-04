package com.point.da.bicharada.entidades;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cachorro extends Animal {
    public static Cachorro novo(
            Connection conn,
            String nome,
            String raca,
            LocalDate nascimento,
            Cliente tutor) throws SQLException {
        Cachorro cachorro = new Cachorro();

        cachorro.conn = conn;
        cachorro.nome = nome;
        cachorro.raca = raca;
        cachorro.nascimento = nascimento;
        cachorro.tutor = tutor;

        String query = "INSERT INTO animais (tutor_cpf, nome, raca, nascimento, especie) VALUES (?, ?, ?, ?, ?) RETURNING \"id\"";

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, tutor.getCPF());
        statement.setString(2, nome.toLowerCase());
        statement.setString(3, raca);
        statement.setString(4, nascimento.format(formatter));
        statement.setString(5, cachorro.getEspecie());

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            cachorro.id = resultSet.getLong("id");
        }

        return cachorro;
    }

    public static Cachorro get(
            Connection conn,
            Cliente tutor,
            String nome) throws SQLException {
        Cachorro cachorro = new Cachorro();
        cachorro.conn = conn;
        cachorro.tutor = tutor;
        cachorro.nome = nome;

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        String query = "SELECT raca, nascimento, especie, id FROM animais WHERE tutor_cpf = ? AND nome = ? AND especie = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, tutor.getCPF());
        statement.setString(2, nome);
        statement.setString(3, cachorro.getEspecie());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            cachorro.raca = resultSet.getString("raca");
            String nascimentoStr = resultSet.getString("nascimento");
            LocalDate nascimento = LocalDate.parse(nascimentoStr, formatter);
            cachorro.nascimento = nascimento;
            cachorro.id = resultSet.getLong("id");
        } else {
            return null;
        }

        resultSet.close();
        statement.close();

        return cachorro;
    }

    public static Cachorro getByID(
            Connection conn,
            Cliente tutor,
            long id) throws SQLException {
        Cachorro cachorro = new Cachorro();
        cachorro.conn = conn;
        cachorro.tutor = tutor;

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        String query = "SELECT nome, raca, nascimento FROM animais WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            cachorro.nome = resultSet.getString("nome");
            cachorro.raca = resultSet.getString("raca");
            String nascimentoStr = resultSet.getString("nascimento");
            LocalDate nascimento = LocalDate.parse(nascimentoStr, formatter);
            cachorro.nascimento = nascimento;
        } else {
            return null;
        }

        resultSet.close();
        statement.close();

        return cachorro;
    }

    public void atualizar(
            String nome,
            String raca,
            LocalDate nascimento) throws SQLException {
        String oldName = this.nome;
        if (nome != null) {
            this.nome = nome;
        }

        if (raca != null) {
            this.raca = raca;
        }

        if (nascimento != null) {
            this.nascimento = nascimento;
        }

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();
        String query = "UPDATE animais SET nome = ?, raca = ?, nascimento = ? WHERE tutor_cpf = ? AND nome = ? AND especie = ?";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, this.nome);
        statement.setString(2, this.raca);
        statement.setString(3, this.nascimento.format(formatter));
        statement.setString(4, this.tutor.getCPF());
        statement.setString(5, oldName);
        statement.setString(6, this.getEspecie());

        statement.executeUpdate();
    }

    public void delete() throws SQLException {
        String query = "DELETE FROM animais WHERE tutor_cpf = ? AND nome = ? AND especie = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, this.tutor.getCPF());
        statement.setString(2, this.nome);
        statement.setString(3, this.getEspecie());

        statement.executeUpdate();
    }

    public String getEspecie() {
        return "cachorro";
    }

    public static Cachorro populated(
            Connection conn,
            String nome,
            String raca,
            LocalDate nascimento,
            Cliente tutor) {
        Cachorro cachorro = new Cachorro();
        cachorro.conn = conn;
        cachorro.nome = nome;
        cachorro.raca = raca;
        cachorro.nascimento = nascimento;
        cachorro.tutor = tutor;
        return cachorro;
    }
}
