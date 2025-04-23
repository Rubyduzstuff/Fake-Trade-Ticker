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
        setTitle("Fake Stock Ticker");
        setSize(600, 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null); // Use null layout for manual positioning

        tickerLabel = new JLabel(tickerText);
        tickerLabel.setSize(tickerText.length() * 10, 30); // Approximate label width
        getContentPane().add(tickerLabel);

        int delay = 50;
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textOffset += scrollSpeed;
                tickerLabel.setLocation(-textOffset, 10); // Adjust Y position as needed
            }
        });
        timer.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FakeStockTicker());
    }
}