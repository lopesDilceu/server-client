package serverclient.server; // Ou o nome do seu pacote

import serverclient.common.Cryptography; // Importa a classe da biblioteca
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private final JTextArea logArea;
    private PrintWriter escritor;

    public ClientHandler(Socket socket, Server server, JTextArea logArea) {
        this.clientSocket = socket;
        this.server = server;
        this.logArea = logArea;
    }

    @Override
    public void run() {
        try {
            // Prepara para ler mensagens do cliente
            BufferedReader leitor = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Prepara para enviar mensagens para o cliente
            this.escritor = new PrintWriter(clientSocket.getOutputStream(), true);

            String mensagemCriptografada;
            while ((mensagemCriptografada = leitor.readLine()) != null) {
                // Descriptografa a mensagem recebida
                String mensagemOriginal = Cryptography.descriptografar(mensagemCriptografada);

                // Mostra a mensagem no log do servidor
                final String logMessage = "Recebido de " + clientSocket.getInetAddress().getHostAddress() + ": " + mensagemOriginal + "\n";
                SwingUtilities.invokeLater(() -> logArea.append(logMessage));

                // Reenvia a mensagem para todos os outros clientes
                server.broadcastMessage(mensagemOriginal, this);
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> logArea.append("Cliente " + clientSocket.getInetAddress().getHostAddress() + " desconectado.\n"));
        } finally {
            // Remove o cliente da lista quando ele se desconectar
            server.removeClient(this);
            try {
                clientSocket.close();
            } catch (Exception e) {
                // ignorar
            }
        }
    }

    // Método para o servidor usar para enviar uma mensagem para ESTE cliente
    public void sendMessage(String message) {
        // Criptografa antes de enviar
        String mensagemCriptografada = Cryptography.criptografar(message);
        escritor.println(mensagemCriptografada);
    }
}