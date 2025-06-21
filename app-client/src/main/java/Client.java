import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import serverclient.common.Cryptography;

public class Client {
    public static void main(String[] args) {
        final String HOST = "127.0.0.1";
        final int PORTA = 13000;

        try (
            // Estabelec conexão com o servidor
            Socket socket = new Socket(HOST, PORTA);
            // Envia mensagens para o servidor
            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
            // Lê o que o usuário digita no console
            Scanner leitorConsole = new Scanner(System.in)
        ) {
            System.out.println("Conectado ao servidor de chat!");

            // Inicia uma thread para receber mensagens do servidor
            MessageReceiver recebedor = new MessageReceiver(socket);
            new Thread(recebedor).start();

            // Loop para enviar mensagens
            System.out.println("Digite suas mensagens (ou 'sair' para desconectar):");
            while (leitorConsole.hasNextLine()) {
                String mensagem = leitorConsole.nextLine();

                if ("sair".equalsIgnoreCase(mensagem)) {
                    break;
                }

                // Criptografa a mensagem antes de enviar
                String mensagemCriptografada = Cryptography.criptografar(mensagem);
                escritor.println(mensagemCriptografada);
            }

        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }

        System.out.println("Desconectado.");
    }
}
