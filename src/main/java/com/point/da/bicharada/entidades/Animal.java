package com.point.da.bicharada.entidades;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Animal {
    protected Connection conn;
    protected String nome;
    protected String raca;
    protected LocalDate nascimento;
    protected Cliente tutor;
    protected long id;

    public String getNome() {
        return nome;
    }

    public String getRaca() {
        return raca;
    }

    public LocalDate getNasicamento() {
        return nascimento;
    }

    public Cliente getTutor() {
        return tutor;
    }

    public long getID() {
        return id;
    }

    public abstract String getEspecie();

    public static DateTimeFormatter getNascimentoFormatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public abstract void atualizar(
            String nome,
            String raca,
            LocalDate nascimento) throws SQLException;

    public abstract void delete() throws SQLException;

    public void print() {
        DateTimeFormatter formatter = Animal.getNascimentoFormatter();
        String nascimento = this.nascimento.format(formatter);
        System.out.println(
                "\nNome: " + this.nome + "\n" +
                        "CPF do Tutor: " + this.tutor.getCPF() + "\n" +
                        "Especie: " + this.getEspecie() + "\n" +
                        "Raca: " + this.raca + "\n" +
                        "Nacimento: " + nascimento + "\n");
    }
}
