import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SistemaBancario {

    private static List<Cliente> clientes = new ArrayList<>();
    private static JFrame frame;
    private static JTextArea outputArea;

    public static void main(String[] args) {
        // janela principal
        frame = new JFrame("Sistema Bancário");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        // painel de saída
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // painel de botões
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        frame.add(buttonPanel, BorderLayout.EAST);

        // botões
        JButton cadastrarButton = new JButton("Cadastrar Cliente");
        JButton listarButton = new JButton("Listar Clientes");
        JButton depositarButton = new JButton("Depositar");
        JButton sacarButton = new JButton("Sacar");
        JButton saldoButton = new JButton("Consultar Saldo");
        JButton transferirButton = new JButton("Transferir");

        
        buttonPanel.add(cadastrarButton);
        buttonPanel.add(listarButton);
        buttonPanel.add(depositarButton);
        buttonPanel.add(sacarButton);
        buttonPanel.add(saldoButton);
        buttonPanel.add(transferirButton);

        // ações dos botões
        cadastrarButton.addActionListener(e -> cadastrarCliente());
        listarButton.addActionListener(e -> listarClientes());
        depositarButton.addActionListener(e -> operacaoBancaria("depositar"));
        sacarButton.addActionListener(e -> operacaoBancaria("sacar"));
        saldoButton.addActionListener(e -> consultarSaldo());
        transferirButton.addActionListener(e -> transferir());

        // mostra a janela
        frame.setVisible(true);
    }

    private static void cadastrarCliente() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        
        JTextField nomeField = new JTextField();
        JTextField cpfField = new JTextField();
        JTextField saldoInicialField = new JTextField();

        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);
        panel.add(new JLabel("Saldo Inicial:"));
        panel.add(saldoInicialField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Cadastrar Cliente", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String nome = nomeField.getText();
                String cpf = cpfField.getText();
                double saldoInicial = Double.parseDouble(saldoInicialField.getText());

                if (nome.isEmpty() || cpf.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Nome e CPF são obrigatórios!");
                    return;
                }

                Cliente cliente = new Cliente(nome, cpf, saldoInicial);
                clientes.add(cliente);
                outputArea.append("Cliente cadastrado com sucesso: " + nome + "\n");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Saldo inicial deve ser um número válido!");
            }
        }
    }

    private static void listarClientes() {
        if (clientes.isEmpty()) {
            outputArea.append("Nenhum cliente cadastrado.\n");
            return;
        }

        outputArea.append("=== LISTA DE CLIENTES ===\n");
        for (Cliente cliente : clientes) {
            outputArea.append(cliente.toString() + "\n");
        }
        outputArea.append("=========================\n");
    }

    private static void operacaoBancaria(String tipo) {
        if (clientes.isEmpty()) {
            outputArea.append("Nenhum cliente cadastrado para realizar operação.\n");
            return;
        }

        // selecionar cliente
        Cliente cliente = selecionarCliente();
        if (cliente == null) return;

        // valor da operação
        String valorStr = JOptionPane.showInputDialog(frame, "Digite o valor para " + tipo + ":");
        if (valorStr == null || valorStr.isEmpty()) return;

        try {
            double valor = Double.parseDouble(valorStr);
            
            if (tipo.equals("depositar")) {
                cliente.depositar(valor);
                outputArea.append("Depósito de R$" + valor + " realizado para " + cliente.getNome() + "\n");
            } else if (tipo.equals("sacar")) {
                if (cliente.sacar(valor)) {
                    outputArea.append("Saque de R$" + valor + " realizado para " + cliente.getNome() + "\n");
                } else {
                    outputArea.append("Saldo insuficiente para saque de R$" + valor + "\n");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Valor inválido! Digite um número.");
        }
    }

    private static void consultarSaldo() {
        if (clientes.isEmpty()) {
            outputArea.append("Nenhum cliente cadastrado.\n");
            return;
        }

        Cliente cliente = selecionarCliente();
        if (cliente == null) return;

        outputArea.append("Saldo de " + cliente.getNome() + ": R$" + String.format("%.2f", cliente.getSaldo()) + "\n");
    }

    private static void transferir() {
        if (clientes.size() < 2) {
            outputArea.append("É necessário ter pelo menos 2 clientes cadastrados para transferência.\n");
            return;
        }

        // cliente origem
        outputArea.append("Selecione o cliente de ORIGEM:\n");
        Cliente origem = selecionarCliente();
        if (origem == null) return;

        // cliente destino
        outputArea.append("Selecione o cliente de DESTINO:\n");
        Cliente destino = selecionarCliente();
        if (destino == null || destino == origem) {
            outputArea.append("Selecione um cliente destino diferente da origem.\n");
            return;
        }

        // valor da transferência
        String valorStr = JOptionPane.showInputDialog(frame, "Digite o valor para transferir:");
        if (valorStr == null || valorStr.isEmpty()) return;

        try {
            double valor = Double.parseDouble(valorStr);
            
            if (origem.sacar(valor)) {
                destino.depositar(valor);
                outputArea.append("Transferência de R$" + String.format("%.2f", valor) + 
                        " realizada de " + origem.getNome() + " para " + destino.getNome() + "\n");
            } else {
                outputArea.append("Saldo insuficiente para transferência de R$" + valor + "\n");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Valor inválido! Digite um número.");
        }
    }

    private static Cliente selecionarCliente() {
        String[] opcoes = new String[clientes.size()];
        for (int i = 0; i < clientes.size(); i++) {
            opcoes[i] = clientes.get(i).getNome() + " - CPF: " + clientes.get(i).getCpf() + 
                    " - Saldo: R$" + String.format("%.2f", clientes.get(i).getSaldo());
        }

        String selecionado = (String) JOptionPane.showInputDialog(frame, 
                "Selecione o cliente:", "Seleção de Cliente",
                JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (selecionado == null) return null;

        for (Cliente cliente : clientes) {
            if (selecionado.contains(cliente.getCpf())) {
                return cliente;
            }
        }
        return null;
    }

    static class Cliente {
        private String nome;
        private String cpf;
        private double saldo;

        public Cliente(String nome, String cpf, double saldoInicial) {
            this.nome = nome;
            this.cpf = cpf;
            this.saldo = saldoInicial;
        }

        public String getNome() {
            return nome;
        }

        public String getCpf() {
            return cpf;
        }

        public double getSaldo() {
            return saldo;
        }

        public void depositar(double valor) {
            if (valor > 0) {
                saldo += valor;
            }
        }

        public boolean sacar(double valor) {
            if (valor > 0 && valor <= saldo) {
                saldo -= valor;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("Cliente{nome='%s', cpf='%s', saldo=R$%.2f}", nome, cpf, saldo);
        }
    }
}