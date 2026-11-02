import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FakeStockTicker extends JFrame {

    private final String tickerText = "EGGS: $30.25  PMPRS: $90.75  WATR: $1050.02  ";
    private final JLabel tickerLabel;
    private int textOffset = 0;
    private final Timer timer;
    private final int scrollSpeed = 2;

    // Where inside the window the mouse was pressed, so dragging keeps the grab point under the cursor
    private Point dragOffset;

    public FakeStockTicker() {
        setUndecorated(true);
        // Alpha of 1 (instead of 0) looks fully transparent but still lets the
        // whole strip receive mouse events, so it can be grabbed anywhere.
        setBackground(new Color(0, 0, 0, 1));
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

        installDragToMove();
        installControls();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /** Click and drag anywhere on the ticker to move it around the screen. */
    private void installDragToMove() {
        MouseAdapter dragger = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragOffset = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), FakeStockTicker.this);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOffset != null && SwingUtilities.isLeftMouseButton(e)) {
                    Point onScreen = e.getLocationOnScreen();
                    setLocation(onScreen.x - dragOffset.x, onScreen.y - dragOffset.y);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragOffset = null;
            }
        };
        for (Component c : new Component[]{getContentPane(), tickerLabel}) {
            c.addMouseListener(dragger);
            c.addMouseMotionListener(dragger);
        }
    }

    /** Right-click menu and keyboard shortcuts: R = restart, Space = pause/resume, Esc = exit. */
    private void installControls() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem restartItem = new JMenuItem("Restart");
        restartItem.addActionListener(e -> restart());
        menu.add(restartItem);

        JMenuItem pauseItem = new JMenuItem("Pause / Resume");
        pauseItem.addActionListener(e -> togglePause());
        menu.add(pauseItem);

        JMenuItem bottomItem = new JMenuItem("Snap to Bottom");
        bottomItem.addActionListener(e -> {
            Rectangle screen = getGraphicsConfiguration().getBounds();
            setLocation(screen.x, screen.y + screen.height - getHeight());
        });
        menu.add(bottomItem);

        menu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(exitItem);

        getContentPane().addMouseListener(popupTrigger(menu));
        tickerLabel.addMouseListener(popupTrigger(menu));

        JComponent root = getRootPane();
        InputMap keys = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = root.getActionMap();
        keys.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restart");
        keys.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "pause");
        keys.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
        actions.put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
        actions.put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
        actions.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private MouseAdapter popupTrigger(JPopupMenu menu) {
        return new MouseAdapter() {
            // Popup trigger fires on press on macOS/Linux and on release on Windows, so check both.
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShow(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShow(e);
            }

            private void maybeShow(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
    }

    /** Scroll the text back to its starting position and make sure it's running. */
    private void restart() {
        textOffset = 0;
        tickerLabel.setLocation(0, 0);
        timer.restart();
    }

    private void togglePause() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FakeStockTicker());
    }
}
