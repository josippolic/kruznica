import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KoordinatniSustav extends JFrame {
    private final DrawingPanel panel;
    private final JComboBox<String> gridSizeBox;
    private final JTextField xField, yField;
    private final JLabel radijusLabel, kutLabel;

    public KoordinatniSustav() {
        setTitle("Kružnica kroz točku (x, y)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new DrawingPanel();
        panel.setPreferredSize(new Dimension(800, 800));
        add(panel, BorderLayout.CENTER);

        // Kontrolni panel
        JPanel controlPanel = new JPanel();

        xField = new JTextField(4);
        yField = new JTextField(4);

        // Dodana opcija "15x15"
        gridSizeBox = new JComboBox<>(new String[]{
            "10x10", "15x15", "20x20", "30x30", "40x40", "50x50"
        });

        JButton izracunajButton = new JButton("Izračunaj");
        JButton resetButton = new JButton("Reset");
        JButton izlazButton = new JButton("Izlaz");

        radijusLabel = new JLabel("Radijus: ");
        kutLabel = new JLabel("Kut: ");

        controlPanel.add(new JLabel("X:"));
        controlPanel.add(xField);
        controlPanel.add(new JLabel("Y:"));
        controlPanel.add(yField);
        controlPanel.add(new JLabel("Mreža:"));
        controlPanel.add(gridSizeBox);
        controlPanel.add(izracunajButton);
        controlPanel.add(resetButton);
        controlPanel.add(izlazButton);
        controlPanel.add(radijusLabel);
        controlPanel.add(kutLabel);

        add(controlPanel, BorderLayout.NORTH);

        // Akcije
        izracunajButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());

                double r = Math.sqrt(x * x + y * y);
                double kut = Math.toDegrees(Math.atan2(y, x));
                if (kut < 0) kut += 360;

                radijusLabel.setText(String.format("Radijus: %.2f", r));
                kutLabel.setText(String.format("Kut: %.2f°", kut));
                panel.setPoint(x, y);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Unesite cijele brojeve!", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        });

        resetButton.addActionListener(e -> {
            xField.setText("");
            yField.setText("");
            gridSizeBox.setSelectedIndex(0);
            radijusLabel.setText("Radijus: ");
            kutLabel.setText("Kut: ");
            panel.clearPoint();
            panel.setGridCount(10);
        });

        izlazButton.addActionListener(e -> System.exit(0));

        gridSizeBox.addActionListener(e -> {
            String selected = (String) gridSizeBox.getSelectedItem();
            if (selected != null) {
                int count = Integer.parseInt(selected.split("x")[0]);
                panel.setGridCount(count);
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KoordinatniSustav().setVisible(true));
    }
}

class DrawingPanel extends JPanel {
    private Integer x = null, y = null;
    private int gridCount = 10;

    public void setPoint(int x, int y) {
        this.x = x;
        this.y = y;
        repaint();
    }

    public void clearPoint() {
        this.x = null;
        this.y = null;
        repaint();
    }

    public void setGridCount(int count) {
        this.gridCount = count;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int offsetX = width / 2;
        int offsetY = height / 2;
        int scale = Math.min(width, height) / (2 * gridCount);

        Graphics2D g2 = (Graphics2D) g;

        // Grid
        g2.setColor(Color.LIGHT_GRAY);
        for (int i = -gridCount; i <= gridCount; i++) {
            int xLine = offsetX + i * scale;
            int yLine = offsetY - i * scale;
            g2.drawLine(xLine, 0, xLine, height);  // vertical
            g2.drawLine(0, yLine, width, yLine);  // horizontal
        }

        // Axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, offsetY, width, offsetY); // x-axis
        g2.drawLine(offsetX, 0, offsetX, height); // y-axis

        // Draw point and circle
        if (x != null && y != null) {
            int px = offsetX + x * scale;
            int py = offsetY - y * scale;

            // Draw circle
            double r = Math.sqrt(x * x + y * y);
            int radijus = (int) (r * scale);
            g2.setColor(Color.BLUE);
            g2.drawOval(offsetX - radijus, offsetY - radijus, 2 * radijus, 2 * radijus);

            // Draw line from origin to point
            g2.setColor(Color.MAGENTA);
            g2.drawLine(offsetX, offsetY, px, py);

            // Draw point
            g2.setColor(Color.RED);
            g2.fillOval(px - 5, py - 5, 10, 10);
            g2.drawString("(" + x + "," + y + ")", px + 6, py - 6);
        }
    }
}
