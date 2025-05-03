import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FakeStockTicker extends JFrame {

    private final String tickerText = "EGGS: $30.25  PMPRS: $90.75  WATR: $1050.02  ";
    private final JLabel tickerLabel;
    private int textOffset = 0;
    private final Timer timer;
    private final int scrollSpeed = 2;

    public FakeStockTicker() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, 30);
        setLocation(0, screenSize.height - getHeight()); // Position at the bottom
        getContentPane().setLayout(null);

        tickerLabel = new JLabel(tickerText);
        tickerLabel.setSize(tickerText.length() * 10, 30);
        tickerLabel.setForeground(Color.GREEN);
        tickerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        getContentPane().add(tickerLabel);

        int delay = 50;
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textOffset += scrollSpeed;
                tickerLabel.setLocation(-textOffset, 0);
            }
        });
        timer.start();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FakeStockTicker());
    }
}