import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.Border;
import java.awt.BasicStroke;

public class CalculatorApp {
    private static final String[] METHODS = {
            "Fixed-Point", "Newton-Raphson", "Secant", "Bisection", "False Position",
            "Matrix", "Cramer's Rule", "Jacobi", "Gaussian Elimination", "Gauss-Seidel"
    };

    private JFrame frame;
    private JPanel methodPanel;
    private CardLayout cardLayout;
    private CardLayout mainCardLayout;
    private JPanel mainCardPanel;
    private DefaultListModel<String> historyListModel = new DefaultListModel<>();

    public CalculatorApp() {
        FlatLightLaf.setup();
        frame = new JFrame("Numerical Methods Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(1024, 768));
        frame.setResizable(false);

        // Top navigation bar
        JPanel navBar = new JPanel(new MigLayout("insets 16 0 16 0, fill, align center", "push[][][]push", "push[]push"));
        navBar.setBackground(Color.WHITE);
        JButton homeBtn = navButton("Home");
        JButton calcBtn = navButton("Calculator");
        JButton histBtn = navButton("History");
        navBar.add(homeBtn, "align center, gapright 20");
        navBar.add(calcBtn, "align center, gapright 20");
        navBar.add(histBtn, "align center");
        frame.add(navBar, BorderLayout.NORTH);

        // Main card panel for Home, Calculator, History
        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);
        mainCardPanel.setBackground(new Color(0xF7F3F0));
        mainCardPanel.add(homePanel(), "Home");
        mainCardPanel.add(calculatorPanel(), "Calculator");
        mainCardPanel.add(historyPanel(), "History");
        frame.add(mainCardPanel, BorderLayout.CENTER);
        mainCardLayout.show(mainCardPanel, "Calculator");

        // Nav button actions
        homeBtn.addActionListener(e -> mainCardLayout.show(mainCardPanel, "Home"));
        calcBtn.addActionListener(e -> mainCardLayout.show(mainCardPanel, "Calculator"));
        histBtn.addActionListener(e -> mainCardLayout.show(mainCardPanel, "History"));

        frame.setVisible(true);
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Bodoni MT", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(new Color(0x6B232C));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20)); // more vertical padding
        return btn;
    }

    private JPanel homePanel() {
        JPanel panel = new JPanel(new MigLayout("center, fill, insets 0", "[grow]", "[grow][][grow]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel welcome = new JLabel("Welcome to the Numerical Methods Calculator!");
        welcome.setFont(new Font("Bodoni MT", Font.BOLD, 28));
        welcome.setForeground(new Color(0x6B232C));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcome, "align center, wrap");
        JLabel subtitle = new JLabel("Select 'Calculator' to start solving equations, or 'History' to view your previous work.");
        subtitle.setFont(new Font("Bodoni MT", Font.PLAIN, 18));
        subtitle.setForeground(new Color(0x6B232C));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitle, "align center");
        return panel;
    }

    private JPanel historyPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 40 40 40 40"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("History");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 24));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "wrap");
        JList<String> historyList = new JList<>(historyListModel);
        historyList.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(historyList);
        scroll.setPreferredSize(new Dimension(800, 400));
        panel.add(scroll, "growx, growy");
        // Example static history
        if (historyListModel.isEmpty()) {
            historyListModel.addElement("x = cos(x)");
            historyListModel.addElement("x^3 - 2*x - 5");
            historyListModel.addElement("x^2 - 4");
            historyListModel.addElement("x^3 - x - 1");
            historyListModel.addElement("x^2 - 3");
        }
        return panel;
    }

    private JPanel calculatorPanel() {
        JPanel mainPanel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[grow]", "[]10[]10[grow]"));
        mainPanel.setBackground(new Color(0xF7F3F0));

        // Title section
        JPanel titlePanel = new JPanel(new MigLayout("fillx, insets 0"));
        titlePanel.setBackground(new Color(0x6B232C));
        JLabel title = new JLabel("Numerical Methods Calculator");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Bodoni MT", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title, "align center, growx, h 60!");
        mainPanel.add(titlePanel, "growx");

        // Method selection buttons (taller)
        JPanel methodButtonsPanel = new JPanel(new MigLayout("wrap 5, gap 10, insets 10 30 10 30", "[grow,fill]"));
        methodButtonsPanel.setBackground(new Color(0xF7F3F0));
        ButtonGroup methodGroup = new ButtonGroup();
        JToggleButton[] methodButtons = new JToggleButton[METHODS.length];
        for (int i = 0; i < METHODS.length; i++) {
            JToggleButton btn = new JToggleButton(METHODS[i]);
            btn.setFont(new Font("Bodoni MT", Font.PLAIN, 14));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(200, 40));
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setMinimumSize(new Dimension(120, 30));
            btn.setFocusable(false);
            btn.setBorderPainted(false);
            btn.setMargin(new Insets(0,0,0,0));
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setVerticalAlignment(SwingConstants.CENTER);

            // Color states
            final Color selectedColor = new Color(0x7B3742);
            final Color unselectedColor = new Color(0xC2877B);
            final Color hoverColor = new Color(0xB46C65); // hover color like Secant
            final boolean[] hovered = {false};

            btn.setUI(new BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    AbstractButton b = (AbstractButton) c;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int arc = 18;
                    Color bg;
                    if (b.isSelected()) {
                        bg = selectedColor;
                    } else if (hovered[0]) {
                        bg = hoverColor;
                    } else {
                        bg = unselectedColor;
                    }
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
                    g2.dispose();
                    super.paint(g, c);
                }
            });

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!btn.isSelected()) {
                        hovered[0] = true;
                        btn.repaint();
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!btn.isSelected()) {
                        hovered[0] = false;
                        btn.repaint();
                    }
                }
            });
            btn.addChangeListener(e -> {
                if (btn.isSelected()) {
                    hovered[0] = false;
                    btn.repaint();
                }
            });

            methodGroup.add(btn);
            methodButtons[i] = btn;
            methodButtonsPanel.add(btn, "growx");
        }
        mainPanel.add(methodButtonsPanel, "growx");

        // CardLayout for method panels
        cardLayout = new CardLayout();
        methodPanel = new JPanel(cardLayout);
        methodPanel.setBackground(new Color(0xF7F3F0));
        methodPanel.add(fixedPointPanel(), METHODS[0]);
        methodPanel.add(newtonRaphsonPanel(), METHODS[1]);
        methodPanel.add(secantPanel(), METHODS[2]);
        methodPanel.add(bisectionPanel(), METHODS[3]);
        methodPanel.add(falsePositionPanel(), METHODS[4]);
        methodPanel.add(equationInputPanel("Matrix Method"), METHODS[5]);
        methodPanel.add(equationInputPanel("Cramer's Rule"), METHODS[6]);
        methodPanel.add(equationInputPanel("Jacobi Method"), METHODS[7]);
        methodPanel.add(equationInputPanel("Gaussian Elimination"), METHODS[8]);
        methodPanel.add(equationInputPanel("Gauss-Seidel Method"), METHODS[9]);
        methodPanel.add(matrixInputPanel(), "Matrix Custom");
        mainPanel.add(methodPanel, "grow, push");

        // Button actions
        for (int i = 0; i < METHODS.length; i++) {
            final int idx = i;
            if (METHODS[i].equals("Matrix")) {
                methodButtons[i].addActionListener(e -> cardLayout.show(methodPanel, "Matrix Custom"));
            } else {
                methodButtons[i].addActionListener(e -> cardLayout.show(methodPanel, METHODS[idx]));
            }
        }
        methodButtons[0].setSelected(true);
        cardLayout.show(methodPanel, METHODS[0]);

        return mainPanel;
    }

    private JPanel fixedPointPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20 30 20 20", "[grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Fixed-Point Iteration Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header);
        JLabel desc = new JLabel("This method solves equations of the form x = g(x) by iteratively computing x₁ = g(x₀), x₂ = g(x₁), etc.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc);
        JPanel form = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]"));
        form.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x = cos(x)", 32);
        JTextField guess = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        guess.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        form.add(labeledField("Function (in form x = g(x))", func), "growx");
        form.add(labeledField("Initial Guess", guess), "growx");
        form.add(labeledField("Tolerance", tol), "growx");
        form.add(labeledField("Max Iterations", maxIter), "growx");
        panel.add(form);
        JButton calc = calcButton();
        panel.add(calc, "align left, gaptop 10");
        return panel;
    }

    private JPanel newtonRaphsonPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20 30 20 20", "[grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Newton-Raphson Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header);
        JLabel desc = new JLabel("The Newton-Raphson method uses the formula x₁ = x₀ - f(x₀)/f'(x₀) to approximate roots of equations.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc);
        JPanel form = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]"));
        form.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^3 - 2*x - 5", 32);
        JTextField guess = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        guess.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        form.add(labeledField("Function f(x)", func), "growx");
        form.add(labeledField("Initial Guess", guess), "growx");
        form.add(labeledField("Tolerance", tol), "growx");
        form.add(labeledField("Max Iterations", maxIter), "growx");
        panel.add(form);
        JButton calc = calcButton();
        panel.add(calc, "align left, gaptop 10");
        return panel;
    }

    private JPanel secantPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20 30 20 20", "[grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Secant Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header);
        JLabel desc = new JLabel("The secant method uses the formula x₂ = x₁ - f(x₁)(x₁ - x₀)/(f(x₁) - f(x₀)) to approximate roots.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc);
        JPanel form = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]"));
        form.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^2 - 4", 32);
        JTextField x0 = new JTextField(32);
        JTextField x1 = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        x0.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        x1.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        form.add(labeledField("Function f(x)", func), "growx");
        form.add(labeledField("Initial Guess (x₀)", x0), "growx");
        form.add(labeledField("Initial Guess (x₁)", x1), "growx");
        form.add(labeledField("Tolerance", tol), "growx");
        form.add(labeledField("Max Iterations", maxIter), "growx");
        panel.add(form);
        JButton calc = calcButton();
        panel.add(calc, "align left, gaptop 10");
        return panel;
    }

    private JPanel bisectionPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20 30 20 20", "[grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Bisection Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header);
        JLabel desc = new JLabel("The bisection method finds a root by repeatedly dividing an interval and selecting the subinterval where the function changes sign.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc);
        JPanel form = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]"));
        form.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^3 - x - 1", 32);
        JTextField a = new JTextField(32);
        JTextField b = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        a.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        b.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        form.add(labeledField("Function f(x)", func), "growx");
        form.add(labeledField("Initial Guess (x₀)", a), "growx");
        form.add(labeledField("Initial Guess (x₁)", b), "growx");
        form.add(labeledField("Tolerance", tol), "growx");
        form.add(labeledField("Max Iterations", maxIter), "growx");
        panel.add(form);
        JButton calc = calcButton();
        panel.add(calc, "align left, gaptop 10");
        return panel;
    }

    private JPanel falsePositionPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20 30 20 20", "[grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("False Position Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header);
        JLabel desc = new JLabel("The false position (regula falsi) method uses linear interpolation to find improved approximations to the roots.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc);
        JPanel form = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]"));
        form.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^2 - 3", 32);
        JTextField a = new JTextField(32);
        JTextField b = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        a.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        b.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        form.add(labeledField("Function f(x)", func), "growx");
        form.add(labeledField("Initial Guess (x₀)", a), "growx");
        form.add(labeledField("Initial Guess (x₁)", b), "growx");
        form.add(labeledField("Tolerance", tol), "growx");
        form.add(labeledField("Max Iterations", maxIter), "growx");
        panel.add(form);
        JButton calc = calcButton();
        panel.add(calc, "align left, gaptop 10");
        return panel;
    }

    private JPanel equationInputPanel(String methodName) {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20 30 20 20", "[grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel(methodName);
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header);
        JLabel desc = new JLabel("Input equations as strings, one per line.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc);
        JTextArea equations = new JTextArea(3, 40);
        equations.setFont(new Font("Monospaced", Font.PLAIN, 15));
        equations.setLineWrap(true);
        equations.setWrapStyleWord(true);
        equations.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        equations.setBackground(Color.WHITE);
        // Prevent Enter/Return if there are already 3 lines
        equations.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && equations.getLineCount() >= 3) {
                    e.consume();
                }
            }
        });
        // Limit each line to 40 characters max and 3 lines max
        ((javax.swing.text.AbstractDocument) equations.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            private final int MAX_COLS = 30;
            private final int MAX_LINES = 3;
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                StringBuilder sb = new StringBuilder(equations.getText());
                sb.insert(offset, string);
                if (allLinesWithinLimit(sb.toString())) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                StringBuilder sb = new StringBuilder(equations.getText());
                sb.replace(offset, offset + length, text);
                if (allLinesWithinLimit(sb.toString())) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            private boolean allLinesWithinLimit(String text) {
                if (text.isEmpty()) return true; // allow empty
                String[] lines = text.split("\\r?\\n", -1); // -1 to include trailing empty lines
                if (lines.length > MAX_LINES) return false;
                for (String line : lines) {
                    if (line.length() > MAX_COLS) return false;
                }
                return true;
            }
        });
        JScrollPane scroll = new JScrollPane(equations);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(equations.getPreferredSize());
        panel.add(scroll, "align left");
        JButton calc = calcButton();
        panel.add(calc, "align left, gaptop 10");
        return panel;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
        panel.add(l, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // Helper for Calculate button with hover effect
    private JButton calcButton() {
        JButton btn = new JButton("Calculate");
        btn.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(0,0,0,0));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        final Color normalColor = new Color(0x7B3742);
        final Color hoverColor = new Color(0xC2877B);
        final boolean[] hovered = {false};
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 18;
                Color bg = hovered[0] ? hoverColor : normalColor;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
                g2.dispose();
                super.paint(g, c);
            }
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hovered[0] = true;
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovered[0] = false;
                btn.repaint();
            }
        });
        return btn;
    }

    private JPanel matrixInputPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, insets 20 30 20 20, gapx 10", "[][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Matrix Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2, wrap");
        JLabel desc = new JLabel("Enter your matrix A (separated by ENTER) and vector B (separated by SPACE) below.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2, wrap, gapbottom 10");

        // Label and text area for A (tall, narrow)
        JLabel labelA = new JLabel("A =");
        labelA.setFont(new Font("Bodoni MT", Font.BOLD, 18));
        labelA.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextArea areaA = new RoundedTextArea(3, 7, 32); // 3 rows, 8 columns, 32px arc
        areaA.setFont(new Font("Monospaced", Font.PLAIN, 16));
        areaA.setLineWrap(true);
        areaA.setWrapStyleWord(true);
        areaA.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(32),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        areaA.setBackground(Color.WHITE);
        int rowHeight = areaA.getFontMetrics(areaA.getFont()).getHeight();
        int areaAHeight = rowHeight * 3 + 24; // 3 rows + padding
        areaA.setPreferredSize(new Dimension(90, areaAHeight));
        // Limit to 6 lines, 8 columns
        ((javax.swing.text.AbstractDocument) areaA.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            private final int MAX_COLS = 6;
            private final int MAX_LINES = 3;
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                StringBuilder sb = new StringBuilder(areaA.getText());
                sb.insert(offset, string);
                if (allLinesWithinLimit(sb.toString())) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                StringBuilder sb = new StringBuilder(areaA.getText());
                sb.replace(offset, offset + length, text);
                if (allLinesWithinLimit(sb.toString())) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            private boolean allLinesWithinLimit(String text) {
                if (text.isEmpty()) return true;
                String[] lines = text.split("\\r?\\n", -1);
                if (lines.length > MAX_LINES) return false;
                for (String line : lines) {
                    if (line.length() > MAX_COLS) return false;
                }
                return true;
            }
        });
        JScrollPane scrollA = new JScrollPane(areaA);
        scrollA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollA.setBorder(BorderFactory.createEmptyBorder());
        scrollA.setPreferredSize(areaA.getPreferredSize());
        scrollA.setOpaque(false);
        scrollA.getViewport().setOpaque(false);

        // Label and text area for B (short, wide)
        JLabel labelB = new JLabel("B =");
        labelB.setFont(new Font("Bodoni MT", Font.PLAIN, 18));
        labelB.setHorizontalAlignment(SwingConstants.RIGHT);
        JTextArea areaB = new RoundedTextArea(1, 30, 32); // 1 row, 30 columns, 32px arc
        areaB.setFont(new Font("Monospaced", Font.PLAIN, 16));
        areaB.setLineWrap(true);
        areaB.setWrapStyleWord(true);
        areaB.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(32),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        areaB.setBackground(Color.WHITE);
        areaB.setPreferredSize(new Dimension(320, 48));
        // Limit to 1 line, 30 columns
        ((javax.swing.text.AbstractDocument) areaB.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            private final int MAX_COLS = 30;
            private final int MAX_LINES = 1;
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                StringBuilder sb = new StringBuilder(areaB.getText());
                sb.insert(offset, string);
                if (allLinesWithinLimit(sb.toString())) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                StringBuilder sb = new StringBuilder(areaB.getText());
                sb.replace(offset, offset + length, text);
                if (allLinesWithinLimit(sb.toString())) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            private boolean allLinesWithinLimit(String text) {
                if (text.isEmpty()) return true;
                String[] lines = text.split("\\r?\\n", -1);
                if (lines.length > MAX_LINES) return false;
                for (String line : lines) {
                    if (line.length() > MAX_COLS) return false;
                }
                return true;
            }
        });
        JScrollPane scrollB = new JScrollPane(areaB);
        scrollB.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollB.setBorder(BorderFactory.createEmptyBorder());
        scrollB.setPreferredSize(areaB.getPreferredSize());
        scrollB.setOpaque(false);
        scrollB.getViewport().setOpaque(false);

        // Add to panel
        JPanel aPanel = new JPanel(new BorderLayout());
        aPanel.setBackground(new Color(0xF7F3F0));
        aPanel.setOpaque(false);
        aPanel.add(scrollA, BorderLayout.CENTER);
        panel.add(labelA, "align right, aligny center, gapx 15");
        panel.add(aPanel, "align left, gapbottom 0, wrap");

        JPanel bPanel = new JPanel(new BorderLayout());
        bPanel.setBackground(new Color(0xF7F3F0));
        bPanel.setOpaque(false);
        bPanel.add(scrollB, BorderLayout.CENTER);
        panel.add(labelB, "align right, gapy 20, gapx 15");
        panel.add(bPanel, "align left, gaptop 20, wrap");

        JButton calc = calcButton();
        panel.add(calc, "span 2, align left, gaptop 10");
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorApp::new);
    }

    class RoundedBorder implements Border {
        private int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
        public boolean isBorderOpaque() {
            return false;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (c.hasFocus()) {
                g2.setColor(new Color(0x7B3742)); // Calculate button color
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(x+1, y+1, width-3, height-3, radius, radius);
            } else {
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            }
            g2.dispose();
        }
    }

    // Custom JTextArea that paints a rounded background
    class RoundedTextArea extends JTextArea {
        private int arc;
        public RoundedTextArea(int rows, int cols, int arc) {
            super(rows, cols);
            this.arc = arc;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
} 