package com.point.da.bicharada.entidades;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        statement.setString(1, nome);
        statement.setString(2, endereco);
        statement.setString(3, telefone);
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
                        "endereco: " + this.endereco + "\n" +
                        "Telefone: " + this.telefone + "\n");
    }
}
