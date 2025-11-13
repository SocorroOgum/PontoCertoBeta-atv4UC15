package ponto.certo.beta;

import javax.swing.SwingUtilities;
import view.TelaLoguin; // 👈 importa sua tela

public class PontoCertoBeta {

    public static void main(String[] args) {

        // Inicia a tela de login na thread da interface gráfica
        SwingUtilities.invokeLater(() -> {
            TelaLoguin tela = new TelaLoguin();
            tela.setVisible(true);
        });
    }
}