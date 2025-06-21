package serverclient.server;

import javax.swing.*;

public class ServerScreen {
    private JPanel panel1;
    private JTextArea logArea;
    private JLabel statusLabel;

    public ServerScreen() {
        // Cria uma nova instância da classe do server, passando os componentes da GUI que o backend precisará atualizar.
        Server server = new Server(logArea, statusLabel);

        // Inicia o server em uma nova thread para não travar a interface.
        new Thread(server).start();
    }

    public static void main(String[] args) {
        // Garante que a GUI seja criada e atualizada na thread de eventos do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Servidor");
            frame.setContentPane(new ServerScreen().panel1); // Define o painel da nossa tela como o conteúdo da janela
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha a aplicação ao clicar no X
//            frame.pack(); // Ajusta o tamanho da janela ao conteúdo
            // Define um tamanho padrão (largura, altura) em pixels
            frame.setSize(600, 400); //
            frame.setLocationRelativeTo(null); // Centraliza a janela na tela
            frame.setVisible(true); // Torna a janela visível
        });
    }
}