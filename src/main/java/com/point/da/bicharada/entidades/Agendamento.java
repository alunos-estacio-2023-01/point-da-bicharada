package com.point.da.bicharada.entidades;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Agendamento {
    private Connection conn;
    private Servico[] servicos;
    private LocalDateTime data;
    private long id;

    public long getID() {
        return id;
    }

    public Connection getConn() {
        return conn;
    }

    public Servico[] getServicos() {
        return servicos;
    }

    public static Agendamento novo(
            Connection conn,
            Servico[] servicos,
            LocalDateTime data) throws SQLException {
        if (servicos.length == 0) {
            return null;
        }

        Agendamento agendamento = new Agendamento();
        agendamento.conn = conn;
        agendamento.servicos = servicos;
        agendamento.data = data;

        String query = "INSERT INTO agendamentos (tutor_cpf, data) VALUES (?, ?) RETURNING \"id\"";
        DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, servicos[0].getAnimal().getTutor().getCPF());
        statement.setString(2, data.format(formatter));

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            agendamento.id = resultSet.getLong("id");
        }

        resultSet.close();
        statement.close();

        query = "INSERT INTO agendamentos_servicos (agendamento_id, animal_id, servico, animal_especie) VALUES (?, ?, ?, ?) RETURNING \"id\"";

        for (Servico servico : servicos) {
            statement = conn.prepareStatement(query);
            statement.setQueryTimeout(5);
            statement.setLong(1, agendamento.id);
            statement.setLong(2, servico.getAnimal().getID());
            statement.setString(3, servico.name());
            statement.setString(4, servico.getAnimal().getEspecie());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                servico.setID(resultSet.getLong("id"));
            }

            statement.close();
            resultSet.close();
        }

        return agendamento;
    }

    public static Agendamento get(Connection conn, String cpf, LocalDateTime data) throws SQLException {
        Agendamento agendamento = new Agendamento();
        agendamento.conn = conn;
        agendamento.data = data;

        String query = "SELECT id FROM agendamentos WHERE tutor_cpf = ? AND data = ?";
        DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setString(1, cpf);
        statement.setString(2, data.format(formatter));

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            agendamento.id = resultSet.getLong("id");
        } else {
            resultSet.close();
            statement.close();
            return null;
        }

        resultSet.close();
        statement.close();

        query = "SELECT id, animal_id, animal_especie, servico FROM agendamentos_servicos WHERE agendamento_id = ?";
        statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setLong(1, agendamento.id);

        Cliente tutor = null;
        List<Servico> servicos = new ArrayList<>();

        resultSet = statement.executeQuery();
        while (resultSet.next()) {
            if (tutor == null) {
                tutor = Cliente.get(conn, cpf);
            }

            long servicoID = resultSet.getLong("id");
            long animalID = resultSet.getLong("animal_id");
            String especie = resultSet.getString("animal_especie");
            String servicoStr = resultSet.getString("servico");

            Animal animal = null;
            if (especie.equals("cachorro")) {
                animal = Cachorro.getByID(conn, tutor, animalID);
            } else if (especie.equals("gato")) {
                animal = Gato.getByID(conn, tutor, animalID);
            }

            Servico servico = Servico.valueOf(servicoStr);
            servico.setAnimal(animal);
            servico.setID(servicoID);

            servicos.add(servico);
        }

        agendamento.servicos = servicos.toArray(new Servico[0]);
        return agendamento;
    }

    public void atualizar(
            Servico[] servicos,
            LocalDateTime data) throws SQLException {
        if (servicos != null) {
            if (servicos.length == 0) {
                return;
            }

            this.servicos = servicos;

            String query = "DELETE FROM agendamentos_servicos WHERE agendamento_id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setQueryTimeout(5);
            statement.setLong(1, this.id);
            statement.executeUpdate();
            statement.close();

            query = "INSERT INTO agendamentos_servicos (agendamento_id, animal_id, servico) VALUES (?, ?, ?)";

            for (Servico servico : servicos) {
                statement = conn.prepareStatement(query);
                statement.setQueryTimeout(5);
                statement.setLong(1, this.id);
                statement.setLong(2, servico.getAnimal().getID());
                statement.setString(3, servico.name());
                statement.executeUpdate();
                statement.close();
            }
        }

        if (data != null) {
            this.data = data;

            DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();
            String query = "UPDATE agendamentos SET data = ? WHERE id = ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setQueryTimeout(5);
            statement.setString(1, this.data.format(formatter));
            statement.setLong(2, this.id);
            statement.executeUpdate();
            statement.close();
        }
    }

    public void delete() throws SQLException {
        String query = "DELETE FROM agendamentos_servicos WHERE agendamento_id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setLong(1, this.id);
        statement.executeUpdate();
        statement.close();

        query = "DELETE FROM agendamentos WHERE id = ?";
        statement = conn.prepareStatement(query);
        statement.setQueryTimeout(5);
        statement.setLong(1, this.id);
        statement.executeUpdate();
        statement.close();
    }

    public void print() {
        DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();
        String data = this.data.format(formatter);
        Cliente tutor = this.servicos[0].getAnimal().getTutor();

        System.out.println("\nDia do agendamento: " + data);

        tutor.print();

        System.out.println("Servicos:");

        double orcamento = 0;

        for (Servico servico : this.servicos) {
            System.out.println(servico.name());
            servico.getAnimal().print();
            orcamento += servico.getValor();
        }

        System.out.println("Orcamento final: R$" + orcamento);
    }

    public static DateTimeFormatter getAgendamentoFormatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    }
}
