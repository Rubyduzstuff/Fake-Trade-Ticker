import javax.swing.*;

public class FakeStockTicker extends JFrame {

    public FakeStockTicker() {
        setTitle("Fake Stock Ticker");
        setSize(600, 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        JLabel label = new JLabel("EGGS: $30.25");
        getContentPane().add(label);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FakeStockTicker());
    }
}