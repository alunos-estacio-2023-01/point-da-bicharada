package com.point.da.bicharada.entidades;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private Connection conn;
    private String cpf;
    private String nome;
    private String endereco;
    private String telefone;

    public static Cliente novo(
            Connection conn,
            String cpf,
            String nome,
            String endereco,
            String telefone) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.conn = conn;
        cliente.cpf = cpf;
        cliente.nome = nome;
        cliente.endereco = endereco;
        cliente.telefone = telefone;

        String query = "INSERT INTO clientes (cpf, nome, endereco, telefone) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, cpf);
        statement.setString(2, nome);
        statement.setString(3, endereco);
        statement.setString(4, telefone);

        statement.executeUpdate();

        return cliente;
    }

    public static Cliente get(Connection conn, String cpf) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.conn = conn;
        cliente.cpf = cpf;

        String query = "SELECT nome, endereco, telefone FROM clientes WHERE cpf = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, cpf);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            cliente.nome = resultSet.getString("nome");
            cliente.endereco = resultSet.getString("endereco");
            cliente.telefone = resultSet.getString("telefone");
        } else {
            return null;
        }

        resultSet.close();
        statement.close();

        return cliente;
    }

    public void atualizar(
            String nome,
            String endereco,
            String telefone) throws SQLException {
        if (nome != null) {
            this.nome = nome;
        }

        if (endereco != null) {
            this.endereco = endereco;
        }

        if (telefone != null) {
            this.telefone = telefone;
        }

        String query = "UPDATE clientes SET nome = ?, endereco = ?, telefone = ? WHERE cpf = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, this.nome);
        statement.setString(2, this.endereco);
        statement.setString(3, this.telefone);
        statement.setString(4, this.cpf);

        statement.executeUpdate();
    }

    public void delete() throws SQLException {
        String query = "DELETE FROM clientes WHERE cpf = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, this.cpf);

        statement.executeUpdate();
    }

    public void print() {
        System.out.println(
                "\nNome: " + this.nome + "\n" +
                        "CPF: " + this.cpf + "\n" +
                        "Endereco: " + this.endereco + "\n" +
                        "Telefone: " + this.telefone + "\n");
    }

    public String getCPF() {
        return cpf;
    }

    public Animal[] getAnimais() throws SQLException {
        String query = "SELECT nome, raca, nascimento, especie FROM animais WHERE tutor_cpf = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, cpf);
        ResultSet resultSet = statement.executeQuery();

        List<Animal> animalList = new ArrayList<>();
        DateTimeFormatter formatter = Animal.getNascimentoFormatter();

        while (resultSet.next()) {
            String especie = resultSet.getString("especie");
            if (especie.equals("cachorro")) {
                String nome = resultSet.getString("nome");
                String raca = resultSet.getString("raca");
                String nascimentoStr = resultSet.getString("nascimento");
                LocalDate nascimento = LocalDate.parse(nascimentoStr, formatter);
                Cachorro cachorro = Cachorro.populated(conn, nome, raca, nascimento, this);
                animalList.add(cachorro);
            } else if (especie.equals("gato")) {
                String nome = resultSet.getString("nome");
                String raca = resultSet.getString("raca");
                String nascimentoStr = resultSet.getString("nascimento");
                LocalDate nascimento = LocalDate.parse(nascimentoStr, formatter);
                Gato gato = Gato.populated(conn, nome, raca, nascimento, this);
                animalList.add(gato);
            }
        }

        resultSet.close();
        statement.close();

        Animal[] animals = animalList.toArray(new Animal[0]);
        return animals;
    }
}
