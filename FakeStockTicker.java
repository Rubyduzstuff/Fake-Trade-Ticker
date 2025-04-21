import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FakeStockTicker extends JFrame {

    private final String tickerText = "EGGS: $30.25  PMPRS: $90.75  WATR: $1050.02  ";
    private final JLabel tickerLabel;
    private int textOffset = 0;
    private final Timer timer;

    public FakeStockTicker() {
        setTitle("Fake Stock Ticker");
        setSize(600, 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tickerLabel = new JLabel(tickerText);
        getContentPane().add(tickerLabel);

        int delay = 100; // Adjust for speed
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textOffset++;
                tickerLabel.setText(tickerText.substring(textOffset % tickerText.length()));
            }
        });
        timer.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FakeStockTicker());
    }
}