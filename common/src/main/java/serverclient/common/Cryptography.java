package serverclient.common;

public class Cryptography {
    // A chave precisa ser a mesma no cliente e no servidor!
    private static final int CHAVE = 3;

    public static String criptografar(String texto) {
        StringBuilder textoCifrado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            // Adiciona a chave ao código ASCII do caractere
            textoCifrado.append((char) (caractere + CHAVE));
        }
        return textoCifrado.toString();
    }

    public static String descriptografar(String textoCifrado) {
        StringBuilder texto = new StringBuilder();
        for (char caractere : textoCifrado.toCharArray()) {
            // Subtrai a chave do código ASCII do caractere
            texto.append((char) (caractere - CHAVE));
        }
        return texto.toString();
    }
}