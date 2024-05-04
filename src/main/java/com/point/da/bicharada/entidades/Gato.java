package com.point.da.bicharada.entidades;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Gato extends Animal {
    public static Gato novo(
            Connection conn,
            String nome,
            String raca,
            LocalDate nascimento,
            Cliente tutor) throws SQLException {
        Gato gato = new Gato();

        gato.conn = conn;
        gato.nome = nome;
        gato.raca = raca;
        gato.nascimento = nascimento;
        gato.tutor = tutor;

        String query = "INSERT INTO animais (tutor_cpf, nome, raca, nascimento, especie) VALUES (?, ?, ?, ?, ?) RETURNING \"id\"";

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, tutor.getCPF());
        statement.setString(2, nome.toLowerCase());
        statement.setString(3, raca);
        statement.setString(4, nascimento.format(formatter));
        statement.setString(5, gato.getEspecie());

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            gato.id = resultSet.getLong("id");
        }

        resultSet.close();
        statement.close();

        return gato;
    }

    public static Gato get(
            Connection conn,
            Cliente tutor,
            String nome) throws SQLException {
        Gato gato = new Gato();
        gato.conn = conn;
        gato.tutor = tutor;
        gato.nome = nome;

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        String query = "SELECT raca, nascimento, especie, id FROM animais WHERE tutor_cpf = ? AND nome = ? AND especie = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, tutor.getCPF());
        statement.setString(2, nome);
        statement.setString(3, gato.getEspecie());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            gato.raca = resultSet.getString("raca");
            String nascimentoStr = resultSet.getString("nascimento");
            LocalDate nascimento = LocalDate.parse(nascimentoStr, formatter);
            gato.nascimento = nascimento;
            gato.id = resultSet.getLong("id");
        } else {
            resultSet.close();
            statement.close();
            return null;
        }

        resultSet.close();
        statement.close();

        return gato;
    }

    public static Gato getByID(
            Connection conn,
            Cliente tutor,
            long id) throws SQLException {
        Gato gato = new Gato();
        gato.conn = conn;
        gato.tutor = tutor;

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        String query = "SELECT nome, raca, nascimento FROM animais WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            gato.nome = resultSet.getString("nome");
            gato.raca = resultSet.getString("raca");
            String nascimentoStr = resultSet.getString("nascimento");
            LocalDate nascimento = LocalDate.parse(nascimentoStr, formatter);
            gato.nascimento = nascimento;
        } else {
            resultSet.close();
            statement.close();
            return null;
        }

        resultSet.close();
        statement.close();

        return gato;
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
        return "gato";
    }

    public static Gato populated(
            Connection conn,
            String nome,
            String raca,
            LocalDate nascimento,
            Cliente tutor) {
        Gato gato = new Gato();
        gato.conn = conn;
        gato.nome = nome;
        gato.raca = raca;
        gato.nascimento = nascimento;
        gato.tutor = tutor;
        return gato;
    }
}
