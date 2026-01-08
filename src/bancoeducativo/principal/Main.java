package bancoeducativo.principal;

import javax.swing.SwingUtilities;
import bancoeducativo.vista.EsteticaUI;
import bancoeducativo.vista.LoginFrame;

public class Main {

    public static void main(String[] args) {

        // Aplicar estética global ANTES de crear cualquier ventana
        EsteticaUI.aplicarTema();

        // Lanzar la interfaz en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}

