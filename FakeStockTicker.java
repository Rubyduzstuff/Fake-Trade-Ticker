import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FakeStockTicker extends JFrame {

    private final String tickerText = String.join("  ",
            "EGGS: $30.25", "PMPRS: $90.75", "WATR: $1050.02",
            "SOCK (left only): $412.00", "NAPS: $0.01", "BRKN UMBRL: $88.88",
            "GOOSE: $666.13", "VIBES: \u25B2 $9000.01", "PIGEON: $3.14",
            "AIR (bagged): $59.99", "SPORK: $1234.56", "MONDAY: \u25BC $0.00",
            "TOAST: $77.77", "LOST REMOTE: $350.00", "WIFI 1 BAR: $0.50",
            "CAT TAX: $420.69", "GLITTER: $12.00", "CRUMBS: $1.99",
            "BANANA (for scale): $19.95", "HAT ON HAT: $2.22", "WET SOCK: -$5.00",
            "LLAMA: $8008.13", "RUBBER DUCK: $42.00") + "  ";
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
        // Start at half the screen width so there's room to move it left and right.
        // Drag the left or right edge to resize it.
        setSize(screenSize.width / 2, 30);
        setLocation((screenSize.width - getWidth()) / 2, screenSize.height - getHeight()); // Bottom, centered
        getContentPane().setLayout(null);

        tickerLabel = new JLabel(tickerText);
        tickerLabel.setForeground(Color.GREEN);
        tickerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        tickerLabel.setSize(tickerLabel.getPreferredSize().width, 30); // fit the whole line in the new font
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

    /**
     * Click and drag anywhere on the ticker to move it around the screen.
     * Drag the left or right edge to make it wider or narrower.
     */
    private void installDragToMove() {
        final int edge = 8;       // how close to a side (in pixels) counts as grabbing the edge
        final int minWidth = 150;

        MouseAdapter dragger = new MouseAdapter() {
            // 0 = move, -1 = resizing from the left edge, 1 = resizing from the right edge
            private int mode;
            private Rectangle startBounds;
            private Point startMouse;

            private int edgeAt(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), FakeStockTicker.this);
                if (p.x < edge) return -1;
                if (p.x >= getWidth() - edge) return 1;
                return 0;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int side = edgeAt(e);
                Cursor cursor = side < 0 ? Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)
                        : side > 0 ? Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)
                        : Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                getContentPane().setCursor(cursor);
                tickerLabel.setCursor(cursor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mode = edgeAt(e);
                    startBounds = getBounds();
                    startMouse = e.getLocationOnScreen();
                    dragOffset = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), FakeStockTicker.this);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOffset == null || !SwingUtilities.isLeftMouseButton(e)) return;
                Point onScreen = e.getLocationOnScreen();
                int dx = onScreen.x - startMouse.x;
                if (mode == 0) {
                    setLocation(onScreen.x - dragOffset.x, onScreen.y - dragOffset.y);
                } else if (mode > 0) {
                    setBounds(startBounds.x, startBounds.y,
                            Math.max(minWidth, startBounds.width + dx), startBounds.height);
                } else {
                    int width = Math.max(minWidth, startBounds.width - dx);
                    setBounds(startBounds.x + startBounds.width - width, startBounds.y,
                            width, startBounds.height);
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
            setLocation(getX(), screen.y + screen.height - getHeight());
        });
        menu.add(bottomItem);

        JMenuItem fullWidthItem = new JMenuItem("Full Width");
        fullWidthItem.addActionListener(e -> {
            Rectangle screen = getGraphicsConfiguration().getBounds();
            setBounds(screen.x, getY(), screen.width, getHeight());
        });
        menu.add(fullWidthItem);

        JMenuItem halfWidthItem = new JMenuItem("Half Width (centered)");
        halfWidthItem.addActionListener(e -> {
            Rectangle screen = getGraphicsConfiguration().getBounds();
            setBounds(screen.x + screen.width / 4, getY(), screen.width / 2, getHeight());
        });
        menu.add(halfWidthItem);

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
