import java.awt.*;
import javax.swing.*;

class ColoredButton {

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JButton b1 = new JButton("Button 1");
                b1.setBackground(Color.RED);
                // these next two lines do the magic..
                b1.setContentAreaFilled(false);
                b1.setOpaque(true);

                JOptionPane.showMessageDialog(null, b1);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}