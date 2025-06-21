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

            String mensagemRecebidaComPrefixo;
            while ((mensagemRecebidaComPrefixo = leitor.readLine()) != null) {
                String logMessage;
                String mensagemOriginal;

                if (mensagemRecebidaComPrefixo.startsWith("CRYPTO:")) {
                    mensagemOriginal = Cryptography.descriptografar(mensagemRecebidaComPrefixo.substring(7));
                    logMessage = "Recebido (Cripto) de " + clientSocket.getInetAddress().getHostAddress() + ": " + mensagemOriginal + "\n";
                } else if (mensagemRecebidaComPrefixo.startsWith("PLAIN:")) {
                    mensagemOriginal = mensagemRecebidaComPrefixo.substring(6);
                    logMessage = "Recebido (Plano) de " + clientSocket.getInetAddress().getHostAddress() + ": " + mensagemOriginal + "\n";
                } else {
                    // Legado ou formato inesperado
                    mensagemOriginal = mensagemRecebidaComPrefixo;
                    logMessage = "Recebido (Formato Desconhecido) de " + clientSocket.getInetAddress().getHostAddress() + ": " + mensagemOriginal + "\n";
                }

                // Mostra a mensagem descriptografada no log do servidor
                final String finalLogMessage = logMessage;
                SwingUtilities.invokeLater(() -> logArea.append(finalLogMessage));

                // Reenvia a mensagem para todos os outros clientes COM O PREFIXO ORIGINAL
                server.broadcastMessage(mensagemRecebidaComPrefixo, this);
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
        escritor.println(message);
    }
}