package serverclient.server; // Ou o nome do seu pacote

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements Runnable {

    private final JTextArea logArea;
    private final JLabel statusLabel;
    private static final int PORTA = 13000; // <- PORTA ATUALIZADA
    private List<ClientHandler> clients = new CopyOnWriteArrayList<>(); // Lista segura para threads

    public Server(JTextArea logArea, JLabel statusLabel) {
        this.logArea = logArea;
        this.statusLabel = statusLabel;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Servidor online na porta: " + PORTA);
                logArea.append("Servidor iniciado em: " + new Date() + "\n");
            });

            while (true) {
                Socket clientSocket = serverSocket.accept();
                SwingUtilities.invokeLater(() -> {
                    logArea.append("Novo cliente conectado: " + clientSocket.getInetAddress().getHostAddress() + "\n");
                });

                // Cria o handler para o novo cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket, this, logArea);
                // Adiciona o novo cliente à lista
                clients.add(clientHandler);
                // Inicia a thread para o novo cliente
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                logArea.append("Erro ao iniciar o servidor: " + e.getMessage() + "\n");
                statusLabel.setText("Erro no servidor.");
            });
        }
    }

    // Método para enviar uma mensagem a todos os clientes
    public void broadcastMessage(String message, ClientHandler sender) {
        String fullMessage = "Broadcast: " + message;
        for (ClientHandler client : clients) {
            // Envia para todos, exceto para quem mandou a mensagem original
            if (client != sender) {
                client.sendMessage(fullMessage);
            }
        }
    }

    // Método para remover um cliente da lista
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}