package com.point.da.bicharada;

import com.point.da.bicharada.entidades.Cliente;
import com.point.da.bicharada.sqlite.Sqlite;

import java.sql.Connection;
import java.sql.SQLException;
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
                case "cadastrar":
                    switch (classe.toLowerCase()) {
                        case "cliente":
                            Cliente cliente = novoCliente(conn, scanner);
                            System.out.println("\n\nCliente criado com sucesso");
                            cliente.print();
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
                    }
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
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

    private static Cliente getCliente(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Qual o CPF do cliente? ");
        String cpf = scanner.nextLine();

        Cliente cliente = Cliente.get(conn, cpf);
        return cliente;
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

    private static void deleteCliente(Cliente cliente, Scanner scanner) throws SQLException {
        cliente.delete();
    }
}
