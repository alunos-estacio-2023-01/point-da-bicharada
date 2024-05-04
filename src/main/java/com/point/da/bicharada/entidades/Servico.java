package com.point.da.bicharada.entidades;

public enum Servico {
    Banho,
    Tosa;

    private Animal animal;
    private long id;

    public double getValor() {
        String nome = name();
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
}
