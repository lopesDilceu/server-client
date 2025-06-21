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
            // Lê as mensagens que o servidor envia
            BufferedReader leitorServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String mensagemDoServidor;
            // Loop para ficar ouvindo o servidor
            while ((mensagemDoServidor = leitorServidor.readLine()) != null) {
                String mensagemFinal;
                if (mensagemDoServidor.startsWith("CRYPTO:")) {
                    String dadosCriptografados = mensagemDoServidor.substring(7);
                    String mensagemOriginal = Cryptography.descriptografar(dadosCriptografados);
                    mensagemFinal = "[SECRETO] " + mensagemOriginal;
                } else if (mensagemDoServidor.startsWith("PLAIN:")) {
                    String mensagemPlana = mensagemDoServidor.substring(6);
                    mensagemFinal = "[NORMAL] " + mensagemPlana;
                } else {
                    // Se o servidor enviar algo sem prefixo (ex: mensagens de status)
                    mensagemFinal = "[SERVER] " + mensagemDoServidor;
                }
                System.out.println(mensagemFinal);
            }
        } catch (Exception e) {
            // Se o servidor cair ou a conexão for fechada, a exceção será capturada
            System.out.println("Conexão com o servidor perdida.");
        }
    }
}