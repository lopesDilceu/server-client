import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import serverclient.common.Cryptography;

public class MessageReceiver implements Runnable {
    private Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                // Objeto para ler as mensagens que o servidor envia
                BufferedReader leitorServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String mensagemDoServidor;
            // Loop para ficar ouvindo o servidor
            while ((mensagemDoServidor = leitorServidor.readLine()) != null) {
                // Descriptografa a mensagem recebida
                String mensagemOriginal = Cryptography.descriptografar(mensagemDoServidor);
                System.out.println("Recebido: " + mensagemOriginal);
            }
        } catch (Exception e) {
            // Se o servidor cair ou a conexão for fechada, a exceção será capturada
            System.out.println("Conexão com o servidor perdida.");
        }
    }
}