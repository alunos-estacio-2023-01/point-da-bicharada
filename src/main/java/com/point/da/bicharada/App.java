package com.point.da.bicharada;

import com.point.da.bicharada.entidades.Agendamento;
import com.point.da.bicharada.entidades.Animal;
import com.point.da.bicharada.entidades.Cachorro;
import com.point.da.bicharada.entidades.Cliente;
import com.point.da.bicharada.entidades.Gato;
import com.point.da.bicharada.entidades.Servico;
import com.point.da.bicharada.sqlite.Sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            Connection conn = Sqlite.connect();
            if (!conn.isValid(5)) {
                System.err.println("nao consegui me conectar ao banco de dados");
                return;
            }

            if (args.length != 2) {
                System.out.println("O uso correto e <cmd> <acao> <classe>");
                return;
            }

            String acao = args[0];
            String classe = args[1];

            scanner = new Scanner(System.in);

            switch (acao.toLowerCase()) {
                case "criar":
                case "cadastrar":
                    switch (classe.toLowerCase()) {
                        case "cliente":
                            Cliente cliente = novoCliente(conn, scanner);
                            System.out.println("\n\nCliente criado com sucesso");
                            cliente.print();
                            break;
                        case "animal":
                        case "pet":
                            Animal animal = novoAnimal(conn, scanner);
                            if (animal != null) {
                                System.out.println("\n\nPet criado com sucesso");
                                animal.print();
                            } else {
                                System.out.println("\n\nFalha ao criar o animal");
                            }
                            break;
                        case "agendamento":
                            Agendamento agendamento = novoAgendamento(conn, scanner);
                            if (agendamento != null) {
                                System.out.println("\n\nAgendamento criado com sucesso");
                                agendamento.print();
                            } else {
                                System.out.println("\n\nFalha ao criar o agendamento");
                            }
                            break;
                    }
                    break;
                case "selecionar":
                    switch (classe.toLowerCase()) {
                        case "cliente":
                            Cliente cliente = getCliente(conn, scanner);
                            if (cliente == null) {
                                System.out.println("Cliente nao encontrado");
                                return;
                            }
                            System.out.println("\n\nCliente encontrado com sucesso");
                            cliente.print();
                            break;
                        case "pet":
                        case "animal":
                            Animal animal = getAnimal(conn, scanner);
                            if (animal == null) {
                                System.out.println("Pet nao encontrado");
                                return;
                            }
                            System.out.println("\n\nAnimal encontrado com sucesso");
                            animal.print();
                            break;
                        case "pets":
                        case "animais":
                            Animal[] animals = getAnimalsOfCliente(conn, scanner);
                            if (animals == null) {
                                System.out.println("Nenhum animal encontrado");
                                return;
                            }
                            System.out.println("\n\nAnimais encontrados com sucesso");
                            for (Animal _animal : animals) {
                                _animal.print();
                            }
                            break;
                        case "agendamento":
                            Agendamento agendamento = getAgendamento(conn, scanner);
                            if (agendamento == null) {
                                System.out.println("Agendamento nao encontrado");
                                return;
                            }
                            System.out.println("\n\nAgendamento encontrado com sucesso");
                            agendamento.print();
                            break;
                    }
                    break;
                case "atualizar":
                    switch (classe.toLowerCase()) {
                        case "cliente":
                            Cliente cliente = getCliente(conn, scanner);
                            if (cliente == null) {
                                System.out.println("Cliente nao encontrado");
                                return;
                            }

                            updateCliente(cliente, scanner);
                            System.out.println("\n\nCliente atualizado com sucesso");
                            cliente.print();
                            break;
                        case "pet":
                        case "animal":
                            Animal animal = getAnimal(conn, scanner);
                            if (animal == null) {
                                System.out.println("Animal nao encontrado");
                                return;
                            }

                            updateAnimal(animal, scanner);
                            System.out.println("\n\nAnimal atualizado com sucesso");
                            animal.print();
                            break;
                        case "agendamento":
                            Agendamento agendamento = getAgendamento(conn, scanner);
                            if (agendamento == null) {
                                System.out.println("Agendamento nao encontrado");
                                return;
                            }

                            updateAgendamento(agendamento, scanner);
                            System.out.println("\n\nAgendamento atualizado com sucesso");
                            agendamento.print();
                            break;
                    }
                    break;
                case "apagar":
                    switch (classe.toLowerCase()) {
                        case "cliente":
                            Cliente cliente = getCliente(conn, scanner);
                            if (cliente == null) {
                                System.out.println("Cliente nao encontrado");
                                return;
                            }

                            deleteCliente(cliente, scanner);
                            System.out.println("\n\nCliente apagado com sucesso");
                            break;
                        case "pet":
                        case "animal":
                            Animal animal = getAnimal(conn, scanner);
                            if (animal == null) {
                                System.out.println("Animal nao encontrado");
                                return;
                            }

                            deleteAnimal(animal, scanner);
                            System.out.println("\n\nAnimal apagado com sucesso");
                            break;
                        case "agendamento":
                            Agendamento agendamento = getAgendamento(conn, scanner);
                            if (agendamento == null) {
                                System.out.println("Agendamento nao encontrado");
                                return;
                            }

                            deleteAgendamento(agendamento, scanner);
                            System.out.println("\n\nAgendamento apagado com sucesso");
                            break;
                    }
                    break;
                default:
                    System.err.println("Acao desconhecida: " + acao);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static Agendamento novoAgendamento(Connection conn, Scanner scanner) throws SQLException {
        Cliente cliente = getCliente(conn, scanner);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado");
            return null;
        }

        System.out.println("Para que dia e hora eh o agendamento? (dd-mm-yyyy hh:mm)");
        String dataStr = scanner.nextLine() + ":00";
        DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();

        LocalDateTime data = null;
        try {
            data = LocalDateTime.parse(dataStr, formatter);
        } catch (DateTimeParseException ex) {
            System.err.println("Data de agendamento invalida");
            return null;
        } catch (RuntimeException ex) {
            System.err.println("Data de agendamento invalida");
            return null;
        }

        List<Servico> servicos = new ArrayList<>();

        while (true) {
            Animal animal = getAnimal(conn, scanner, cliente);
            if (animal == null) {
                System.out.println("Animal nao encontrado");
                return null;
            }

            System.out.println("O pet tomara banho? (s/n)");
            String sn = scanner.nextLine();

            if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
                Servico servico = Servico.Banho;
                servico.setAnimal(animal);
                servicos.add(servico);
            }

            System.out.println("O pet sera tosado? (s/n)");
            sn = scanner.nextLine();

            if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
                Servico servico = Servico.Tosa;
                servico.setAnimal(animal);
                servicos.add(servico);
            }

            System.out.println("Deseja marcar outro pet desse cliente no mesmo horario? (s/n)");
            sn = scanner.nextLine();

            if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
                continue;
            }

            break;
        }

        if (servicos.size() == 0) {
            System.out.println("Nenhum servico foi passado");
            return null;
        }

        Agendamento agendamento = Agendamento.novo(conn, servicos.toArray(new Servico[0]), data);
        return agendamento;
    }

    private static Animal novoAnimal(Connection conn, Scanner scanner) throws SQLException {
        Cliente cliente = getCliente(conn, scanner);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado");
            return null;
        }

        System.out.println("Qual a especie do animal? (cachorro ou gato)");
        String especie = scanner.nextLine().trim().toLowerCase();

        Animal animal = null;

        System.out.println("Qual o nome do pet?");
        String nome = scanner.nextLine().trim().toLowerCase();

        System.out.println("Qual a raca do pet?");
        String raca = scanner.nextLine().trim().toLowerCase();

        System.out.println("Qual a data de nascimento do pet? (dd-mm-aaaa)");
        String nascimentoStr = scanner.nextLine().trim().toLowerCase();

        DateTimeFormatter formatter = Animal.getNascimentoFormatter();
        LocalDate nascimento = null;

        try {
            nascimento = LocalDate.parse(nascimentoStr, formatter);
        } catch (DateTimeParseException ex) {
            System.err.println("Data de nascimento invalida");
            return null;
        } catch (RuntimeException ex) {
            System.err.println("Data de nascimento invalida");
            return null;
        }

        if (especie.equals("cachorro")) {
            animal = Cachorro.novo(conn, nome, raca, nascimento, cliente);
        } else if (especie.equals("gato")) {
            animal = Gato.novo(conn, nome, raca, nascimento, cliente);
        } else {
            System.out.println("A especie " + especie + " e desconhecida");
            return null;
        }

        return animal;
    }

    private static Cliente novoCliente(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Qual o CPF do cliente? ");
        String cpf = scanner.nextLine();

        System.out.println("Qual o nome do cliente? ");
        String nome = scanner.nextLine();

        System.out.println("Qual o endereco do cliente? ");
        String endereco = scanner.nextLine();

        System.out.println("Qual o telefone do cliente? ");
        String telefone = scanner.nextLine();

        Cliente cliente = Cliente.novo(conn, cpf, nome, endereco, telefone);
        return cliente;
    }

    private static Agendamento getAgendamento(Connection conn, Scanner scanner) throws SQLException {
        Cliente cliente = getCliente(conn, scanner);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado");
            return null;
        }

        System.out.println("Para que dia e hora eh o agendamento? (dd-mm-yyyy hh:mm)");
        String dataStr = scanner.nextLine() + ":00";
        DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();

        LocalDateTime data = null;
        try {
            data = LocalDateTime.parse(dataStr, formatter);
        } catch (DateTimeParseException ex) {
            System.err.println("Data de agendamento invalida");
            return null;
        } catch (RuntimeException ex) {
            System.err.println("Data de agendamento invalida");
            return null;
        }

        Agendamento agendamento = Agendamento.get(conn, cliente.getCPF(), data);
        return agendamento;
    }

    private static Animal getAnimal(Connection conn, Scanner scanner) throws SQLException {
        Cliente cliente = getCliente(conn, scanner);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado");
            return null;
        }

        System.out.println("Qual a especie do animal? (cachorro ou gato)");
        String especie = scanner.nextLine().trim().toLowerCase();

        Animal animal = null;

        if (especie.equals("cachorro")) {
            System.out.println("Qual o nome do pet?");
            String nome = scanner.nextLine().trim().toLowerCase();
            animal = Cachorro.get(conn, cliente, nome);
        } else if (especie.equals("gato")) {
            System.out.println("Qual o nome do pet?");
            String nome = scanner.nextLine().trim().toLowerCase();
            animal = Gato.get(conn, cliente, nome);
        } else {
            System.out.println("A especie " + especie + " e desconhecida");
            return null;
        }

        return animal;
    }

    private static Animal getAnimal(Connection conn, Scanner scanner, Cliente cliente) throws SQLException {
        System.out.println("Qual a especie do animal? (cachorro ou gato)");
        String especie = scanner.nextLine().trim().toLowerCase();

        Animal animal = null;

        if (especie.equals("cachorro")) {
            System.out.println("Qual o nome do pet?");
            String nome = scanner.nextLine().trim().toLowerCase();
            animal = Cachorro.get(conn, cliente, nome);
        } else if (especie.equals("gato")) {
            System.out.println("Qual o nome do pet?");
            String nome = scanner.nextLine().trim().toLowerCase();
            animal = Gato.get(conn, cliente, nome);
        } else {
            System.out.println("A especie " + especie + " e desconhecida");
            return null;
        }

        return animal;
    }

    private static Animal[] getAnimalsOfCliente(Connection conn, Scanner scanner) throws SQLException {
        Cliente cliente = getCliente(conn, scanner);
        if (cliente == null) {
            System.out.println("Cliente nao encontrado");
            return null;
        }

        return cliente.getAnimais();
    }

    private static Cliente getCliente(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Qual o CPF do cliente? ");
        String cpf = scanner.nextLine();

        Cliente cliente = Cliente.get(conn, cpf);
        return cliente;
    }

    private static void updateAgendamento(Agendamento agendamento, Scanner scanner) throws SQLException {
        System.out.println("Voce deseja atualizar a data do agendamento? (s/n)");
        String sn = scanner.nextLine();

        LocalDateTime data = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Para que dia e hora eh o agendamento? (dd-mm-yyyy hh:mm)");
            String dataStr = scanner.nextLine() + ":00";
            DateTimeFormatter formatter = Agendamento.getAgendamentoFormatter();

            try {
                data = LocalDateTime.parse(dataStr, formatter);
            } catch (DateTimeParseException ex) {
                System.err.println("Data de agendamento invalida");
                return;
            } catch (RuntimeException ex) {
                System.err.println("Data de agendamento invalida");
                return;
            }
        }

        System.out.println("Voce deseja atualizar os servicos do agendamento? (s/n)");
        sn = scanner.nextLine();

        Servico[] servicos = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            List<Servico> servicosList = new ArrayList<>();

            while (true) {
                Animal animal = getAnimal(agendamento.getConn(), scanner,
                        agendamento.getServicos()[0].getAnimal().getTutor());
                if (animal == null) {
                    System.out.println("Animal nao encontrado");
                    return;
                }

                System.out.println("O pet tomara banho? (s/n)");
                sn = scanner.nextLine();

                if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
                    Servico servico = Servico.Banho;
                    servico.setAnimal(animal);
                    servicosList.add(servico);
                }

                System.out.println("O pet sera tosado? (s/n)");
                sn = scanner.nextLine();

                if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
                    Servico servico = Servico.Tosa;
                    servico.setAnimal(animal);
                    servicosList.add(servico);
                }

                System.out.println("Deseja marcar outro pet desse cliente no mesmo horario? (s/n)");
                sn = scanner.nextLine();

                if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
                    continue;
                }

                break;
            }

            if (servicosList.size() == 0) {
                System.out.println("Nenhum servico foi passado");
                return;
            }

            servicos = servicosList.toArray(new Servico[0]);
        }

        agendamento.atualizar(servicos, data);
    }

    private static void updateAnimal(Animal animal, Scanner scanner) throws SQLException {
        System.out.println("Voce deseja atualizar o nome do pet? (s/n)");
        String sn = scanner.nextLine();

        String nome = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Qual o novo nome do pet? ");

            nome = scanner.nextLine();
        }

        System.out.println("Voce deseja atualizar a raca do pet? (s/n)");
        sn = scanner.nextLine();

        String raca = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Qual a nova raca do pet? ");

            raca = scanner.nextLine();
        }

        System.out.println("Voce deseja atualizar a data de nascimento do pet? (s/n)");
        sn = scanner.nextLine();

        LocalDate nascimento = null;

        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Qual a nova data de nascimento? ");

            String nascimentoStr = scanner.nextLine();
            DateTimeFormatter formatter = Animal.getNascimentoFormatter();

            try {
                nascimento = LocalDate.parse(nascimentoStr, formatter);
            } catch (DateTimeParseException ex) {
                System.err.println("Data de nascimento invalida");
                return;
            } catch (RuntimeException ex) {
                System.err.println("Data de nascimento invalida");
                return;
            }
        }

        animal.atualizar(nome, raca, nascimento);
    }

    private static void updateCliente(Cliente cliente, Scanner scanner) throws SQLException {
        System.out.println("Voce deseja atualizar o nome do cliente? (s/n)");
        String sn = scanner.nextLine();

        String nome = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Qual o novo nome do cliente? ");

            nome = scanner.nextLine();
        }

        System.out.println("Voce deseja atualizar o endereco do cliente? (s/n)");
        sn = scanner.nextLine();

        String endereco = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Qual o novo endereco do cliente? ");

            endereco = scanner.nextLine();
        }

        System.out.println("Voce deseja atualizar o telefone do cliente? (s/n)");
        sn = scanner.nextLine();

        String telefone = null;
        if (sn.toLowerCase().trim().equals("s") || sn.toLowerCase().trim().equals("sim")) {
            System.out.println("Qual o novo telefone do cliente? ");

            telefone = scanner.nextLine();
        }

        cliente.atualizar(nome, endereco, telefone);
    }

    private static void deleteAgendamento(Agendamento agendamento, Scanner scanner) throws SQLException {
        agendamento.delete();
    }

    private static void deleteAnimal(Animal animal, Scanner scanner) throws SQLException {
        animal.delete();
    }

    private static void deleteCliente(Cliente cliente, Scanner scanner) throws SQLException {
        cliente.delete();
    }
}
