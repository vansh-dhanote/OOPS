import ui.LoginFrame;

import javax.swing.UIManager;

public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Default look and feel will be used.");
        }

        new LoginFrame().setVisible(true);
    }
}
