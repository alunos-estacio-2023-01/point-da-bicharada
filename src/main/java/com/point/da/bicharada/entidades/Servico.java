package com.point.da.bicharada.entidades;

public class Servico {
    private String nome;

    public Servico(String nome) {
        if (!nome.equals("Banho") && !(nome.equals("Tosa"))) {
            throw new RuntimeException("servico so pode ser banho ou tosa");
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (!nome.equals("Banho") && !(nome.equals("Tosa"))) {
            throw new RuntimeException("servico so pode ser banho ou tosa");
        }

        this.nome = nome;
    }

    private Animal animal;
    private long id;

    public double getValor() {
        if (nome.equals("Banho")) {
            return 70;
        } else if (nome.equals("Tosa")) {
            return 150;
        }
        return 0;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Animal getAnimal() {
        return this.animal;
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getID() {
        return this.id;
    }

    public void print() {
        System.out.println(
                "---------------------------------\n" +
                        "Nome do servico: " + this.nome);

        this.animal.print();

        System.out.println("---------------------------------\n");
    }
}
