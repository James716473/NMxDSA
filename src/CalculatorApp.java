import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.Border;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.math.BigDecimal;
import java.util.Stack;


public class CalculatorApp {
    private static final String[] METHODS = {
            "Fixed-Point", "Newton-Raphson", "Secant", "Bisection", "False Position",
            "Matrix", "Cramer's Rule", "Jacobi", "Gaussian Elimination", "Gauss-Seidel"
    };
    private Methods methods = new Methods(100);
    private Stack<Tuple<String, String>> history = new Stack<>(); // wtf is a type safety 🗣🔥🔥 
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
        ImageIcon icon = new ImageIcon("./img/freaky_alden.jpg");
        frame.setIconImage(icon.getImage());
        // Top navigation bar
        JPanel navBar = new JPanel(new MigLayout("insets 16 0 16 0, fill, align center", "push[][][]push", "push[]push"));
        navBar.setBackground(Color.WHITE);
        JButton homeBtn = navButton("Home");
        JButton calcBtn = navButton("Calculator"); // calc is short for calculator btw
        JButton histBtn = navButton("History");
        histBtn.addActionListener(e -> {
            System.out.println(history);
        });
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
        JPanel panel = new JPanel(new MigLayout("fill, insets 40 40 40 40", "[grow 30][grow 70]", ""));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("History");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 24));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "wrap, span 2");

        // Left: List of equations
        JList<String> historyList = new JList<>(historyListModel);
        historyList.setFont(new Font("Monospaced", Font.PLAIN, 16));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollList = new JScrollPane(historyList);
        scrollList.setPreferredSize(new Dimension(240, 400));
        panel.add(scrollList, "growy, width 240:240:320");

        // Right: Solution display
        JTextArea solutionArea = new JTextArea();
        solutionArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        solutionArea.setEditable(false);
        solutionArea.setLineWrap(false);
        solutionArea.setWrapStyleWord(false);
        solutionArea.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solutionArea.setBackground(Color.WHITE);
        JScrollPane scrollSolution = new JScrollPane(solutionArea);
        scrollSolution.setPreferredSize(new Dimension(600, 400));
        scrollSolution.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollSolution, "grow, pushx, span 1");

        // Show solution when an equation is selected
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = historyList.getSelectedValue();
                if (selected != null) {
                    // Find the most recent solution for this equation
                    for (int i = history.size() - 1; i >= 0; i--) {
                        Tuple<String, String> entry = history.get(i);
                        if (entry.getX().equals(selected)) {
                            solutionArea.setText(entry.getY());
                            break;
                        }
                    }
                } else {
                    solutionArea.setText("");
                }
            }
        });

        // Add clear history button
        JButton clearButton = new JButton("Clear History");
        clearButton.setFont(new Font("Bodoni MT", Font.PLAIN, 14));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(0x7B3742));
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            history.clear();
            historyListModel.clear();
            solutionArea.setText("");
        });
        panel.add(clearButton, "span 2, align right, gaptop 10");

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
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20 30 20 20", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Fixed-Point Iteration Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("This method solves equations of the form x = g(x) by iteratively computing x₁ = g(x₀), x₂ = g(x₁), etc.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, gap 10 2", "[]", "[]"));
        leftPanel.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("e^-x", 32);
        JTextField guess = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        guess.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(labeledField("Function (in form x = g(x))", func), "");
        leftPanel.add(labeledField("Initial Guess", guess), "");
        leftPanel.add(labeledField("Tolerance", tol), "");
        leftPanel.add(labeledField("Max Iterations", maxIter), "");
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]", "[][][grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Solution:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            try{
                solution.setText("");
                String funcStr = func.getText();
                String guessStr = guess.getText();
                String tolStr = tol.getText();
                String maxIterStr = maxIter.getText();
                methods.setMaxIteration(Integer.parseInt(maxIterStr));
                methods.setTolerance(new BigDecimal(tolStr));
                List<Double> answer = methods.fixedPoint(methods.parseEquation(funcStr), Double.parseDouble(guessStr), new LinkedList<Double>());
                
                historyListModel.insertElementAt(funcStr, 0);
                System.out.println(answer);
                String solutionStr = "";
                solutionStr += "Initial Guess:\n";
                solutionStr += "x0: " + answer.get(0) + "\n\n";
                for(int i = 1; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + i + ":\n";
                    solutionStr += "x" + (i)  +" = " + funcStr.replace("x", "(" + answer.get(i) + ")") + "\n";
                    solutionStr += "x" + (i)  +" = " + answer.get(i) + "\n\n";
                }
                solutionStr += "Table\n";
                for(int i = 0; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + i + ":\t\tx" + i + " = " + answer.get(i) + "\n";
                }
                solutionStr += "\nRoot: " + answer.get(answer.size() - 1) + "\n";
                
                solution.setText(solutionStr);
                history.push(new Tuple<>(funcStr, solutionStr));
            } catch(Exception ex){
                solution.setText("Error:\n" + ex.getMessage());
            }
            
        });
        panel.add(calc, "span 2, align left, gaptop 10");
        return panel;
    }

    private JPanel newtonRaphsonPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20 30 20 20", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Newton-Raphson Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("The Newton-Raphson method uses the formula x₁ = x₀ - f(x₀)/f'(x₀) to approximate roots of equations.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, gap 10", "[]", "[]"));
        leftPanel.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("2^x - 5x + 2", 32);
        JTextField guess = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        guess.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(labeledField("Function f(x)", func), "");
        leftPanel.add(labeledField("Initial Guess", guess), "");
        leftPanel.add(labeledField("Tolerance", tol), "");
        leftPanel.add(labeledField("Max Iterations", maxIter), "");
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]", "[][][grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Solution:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            try{
                solution.setText("");
                String funcStr = func.getText();
                String guessStr = guess.getText();
                String tolStr = tol.getText();
                String maxIterStr = maxIter.getText();
                methods.setMaxIteration(Integer.parseInt(maxIterStr));
                methods.setTolerance(new BigDecimal(tolStr));
                historyListModel.insertElementAt(funcStr, 0);
                List<Double> answer = methods.newtonRaphson(methods.parseEquation(funcStr), Double.parseDouble(guessStr), new LinkedList<Double>());
                
                String solutionStr = "";
                solutionStr += "Initial Guess:\n";
                solutionStr += "x0: " + answer.get(0) + "\n\n";
                for(int i = 1; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + i + ":\n";
                    solutionStr += "x" + (i)  +" = " + answer.get(i-1) + " - (" + funcStr.replace("x", "(" + answer.get(i-1) + ")") + ") / ((d/dx)(" + funcStr.replace("x", "x" + (i-1)) + "))" + "\n";
                    solutionStr += "x" + (i)  +" = " + answer.get(i) + "\n\n";
                }
                solutionStr += "Table\n";
                for(int i = 0; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + i + ":\t\tx" + i + " = " + answer.get(i) + "\n";
                }
                solutionStr += "\nRoot: " + answer.get(answer.size() - 1) + "\n";
                solution.setText(solutionStr);
                history.push(new Tuple<>(funcStr, solutionStr));
                System.out.println(answer);
            } catch(Exception ex){
                solution.setText("Error:\n" + ex.getMessage());
            }
            
        });
        panel.add(calc, "span 2, align left, gaptop 10");
        return panel;
    }

    private JPanel secantPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20 30 20 20", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Secant Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("The secant method uses the formula x₂ = x₁ - f(x₁)(x₁ - x₀)/(f(x₁) - f(x₀)) to approximate roots.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, gap 10", "[]", "[]"));
        leftPanel.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^3 - x - 1", 32);
        JTextField x0 = new JTextField(32);
        JTextField x1 = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        x0.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        x1.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(labeledField("Function f(x)", func), "");
        leftPanel.add(labeledField("Initial Guess (x₀)", x0), "");
        leftPanel.add(labeledField("Initial Guess (x₁)", x1), "");
        leftPanel.add(labeledField("Tolerance", tol), "");
        leftPanel.add(labeledField("Max Iterations", maxIter), "");
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]", "[][][grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Solution:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            try{
                solution.setText("");
                String funcStr = func.getText();
                String x0Str = x0.getText();
                String x1Str = x1.getText();
                String tolStr = tol.getText();
                String maxIterStr = maxIter.getText();
                methods.setMaxIteration(Integer.parseInt(maxIterStr));
                methods.setTolerance(new BigDecimal(tolStr));
                historyListModel.insertElementAt(funcStr, 0);
                List<Double> answer = methods.secant(methods.parseEquation(funcStr), Double.parseDouble(x0Str), Double.parseDouble(x1Str), new LinkedList<Double>());
                
                String solutionStr = "";
                solutionStr += "Initial Guesses:\n";
                solutionStr += "x0: " + answer.get(0) + "\n";
                solutionStr += "x1: " + answer.get(1) + "\n\n";
                for(int i = 2; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + (i-1) + ":\n";
                    solutionStr += "x" + (i)  +" = " + answer.get(i-1) + " - (" + funcStr.replace("x", "(" + answer.get(i-1) + ")") + ") * ((" + answer.get(i-1) + " - " + answer.get(i-2) + ") / (" + funcStr.replace("x", "(" + answer.get(i-1) + ")") + " - (" + funcStr.replace("x", "(" + answer.get(i-2) + ")") + "))" + "\n";
                    solutionStr += "x" + (i)  +" = " + answer.get(i) + "\n\n";
                }
                solutionStr += "Table\n";
                for(int i = 0; i < answer.size() - 1; i++){
                    solutionStr += "Iteration No. " + i + ":\t\tx0 = " + answer.get(i) + "\t\tx1 = " + answer.get(i+1) + "\n";
                }
                solutionStr += "\nRoot: " + answer.get(answer.size() - 1) + "\n";
                solution.setText(solutionStr);
                history.push(new Tuple<>(funcStr, solutionStr));
                System.out.println(answer);
            } catch(Exception ex){
                solution.setText("Error:\n" + ex.getMessage());
            }
            
        });
        panel.add(calc, "span 2, align left, gaptop 10");
        return panel;
    }

    private JPanel bisectionPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20 30 20 20", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Bisection Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("The bisection method finds a root by repeatedly dividing an interval and selecting the subinterval where the function changes sign.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, gap 10", "[]", "[]"));
        leftPanel.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^3 + 4x^2 - 10", 32);
        JTextField a = new JTextField(32);
        JTextField b = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        a.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        b.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(labeledField("Function f(x)", func), "");
        leftPanel.add(labeledField("Initial Guess (x₀)", a), "");
        leftPanel.add(labeledField("Initial Guess (x₁)", b), "");
        leftPanel.add(labeledField("Tolerance", tol), "");
        leftPanel.add(labeledField("Max Iterations", maxIter), "");
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]", "[][][grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Solution:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            try{
                solution.setText("");
                String funcStr = func.getText();
                String aStr = a.getText();
                String bStr = b.getText();
                String tolStr = tol.getText();
                String maxIterStr = maxIter.getText();
                methods.setMaxIteration(Integer.parseInt(maxIterStr));
                methods.setTolerance(new BigDecimal(tolStr));
                historyListModel.insertElementAt(funcStr, 0);
                List<Double[]> answer = methods.bisection(methods.parseEquation(funcStr), Double.parseDouble(aStr), Double.parseDouble(bStr), new LinkedList<Double[]>());
                
                String solutionStr = "";
                solutionStr += "Initial Guesses:\n";
                solutionStr += "xL: " + answer.get(0)[0] + "\t\txR: " + answer.get(0)[1] + "\n\n";
                for(int i = 0; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + (i) + ":\n";
                    solutionStr += "xM = (" + answer.get(i)[0] + " + " + answer.get(i)[1] + ") / 2\n";
                    solutionStr += "xM = " + answer.get(i)[3] + "\n";
                    solutionStr += "f(xM) = " + funcStr.replace("x", "(" + answer.get(i)[3] + ")") + "\n";
                    solutionStr += "f(xM) = " + answer.get(i)[2] + "\n\n";
                }
                solutionStr += "Table\n";
                
    
                for(int i = 0; i < answer.size(); i++){
                    solutionStr += "Iteration No. " + (i) + ":\t\txL = " + answer.get(i)[0] + "\t\txR = " + answer.get(i)[1] + "\t\txM = " + answer.get(i)[3] + "\t\tf(xM) = " + answer.get(i)[2] + "\n";
                }
                solutionStr += "\nRoot: " + answer.get(answer.size() - 1)[0] + "\n";
                solution.setText(solutionStr);
                history.push(new Tuple<>(funcStr, solutionStr));
                System.out.println(answer);
            } catch(Exception ex){
                solution.setText("Error:\n" + ex.getMessage());
            }
            
        });
        panel.add(calc, "span 2, align left, gaptop 10");
        return panel;
    }

    private JPanel falsePositionPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20 30 20 20", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("False Position Method");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("The false position (regula falsi) method uses linear interpolation to find improved approximations to the roots.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, gap 10", "[]", "[]"));
        leftPanel.setBackground(new Color(0xF7F3F0));
        JTextField func = new JTextField("x^3 - 4cos(x)", 32);
        JTextField a = new JTextField(32);
        JTextField b = new JTextField(32);
        JTextField tol = new JTextField(32);
        JTextField maxIter = new JTextField(32);
        func.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        a.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        b.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        tol.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        maxIter.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(labeledField("Function f(x)", func), "");
        leftPanel.add(labeledField("Initial Guess (x₀)", a), "");
        leftPanel.add(labeledField("Initial Guess (x₁)", b), "");
        leftPanel.add(labeledField("Tolerance", tol), "");
        leftPanel.add(labeledField("Max Iterations", maxIter), "");
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]", "[][][grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Solution:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            try{
                solution.setText("");
            String funcStr = func.getText();
            String aStr = a.getText();
            String bStr = b.getText();
            String tolStr = tol.getText();
            String maxIterStr = maxIter.getText();
            methods.setMaxIteration(Integer.parseInt(maxIterStr));
            methods.setTolerance(new BigDecimal(tolStr));
            historyListModel.insertElementAt(funcStr, 0);
            List<Double[]> answer = methods.falsePosition(methods.parseEquation(funcStr), Double.parseDouble(aStr), Double.parseDouble(bStr), new LinkedList<Double[]>());
            
            String solutionStr = "";
            solutionStr += "Initial Guesses:\n";
            solutionStr += "xL: " + answer.get(0)[0] + "\t\txR: " + answer.get(0)[1] + "\n\n";
            for(int i = 0; i < answer.size(); i++){
                solutionStr += "Iteration No. " + (i) + ":\n";
                solutionStr += "x = " + answer.get(i)[0] + " + (" + "(" + answer.get(i)[1] + " - " + answer.get(i)[0] + ")" + "(-(" + funcStr.replace("x", "(" + answer.get(i)[0] + ")") + ")) / ((" + funcStr.replace("x", "(" + answer.get(i)[1] + ")") + " - (" + funcStr.replace("x", "(" + answer.get(i)[0] + ")") + "))" + "\n";
                solutionStr += "x = " + answer.get(i)[3] + "\n";
                solutionStr += "f(x) = " + funcStr.replace("x", "(" + answer.get(i)[3] + ")") + "\n";
                solutionStr += "f(x) = " + answer.get(i)[2] + "\n\n";
            }
            solutionStr += "Table\n";
            for(int i = 0; i < answer.size(); i++){
                solutionStr += "Iteration No. " + (i) + ":\t\txL = " + answer.get(i)[0] + "\t\txR = " + answer.get(i)[1] + "\t\tx = " + answer.get(i)[3] + "\t\tf(x) = " + answer.get(i)[2] + "\n";
            }
            solutionStr += "\nRoot: " + answer.get(answer.size() - 1)[0] + "\n";
            solution.setText(solutionStr);
            history.push(new Tuple<>(funcStr, solutionStr));
            System.out.println(Arrays.deepToString(answer.toArray()));
            } catch(Exception ex){
                solution.setText("Error:\n" + ex.getMessage());
            }
            
        });
        panel.add(calc, "span 2, align left, gaptop 10");
        return panel;
    }

    private JPanel equationInputPanel(String methodName) {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 20 30 20 20", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel(methodName);
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("Input equations as strings, one per line.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, gap 10", "[]", "[]"));
        leftPanel.setBackground(new Color(0xF7F3F0));
        JTextArea equations = new JTextArea(3, 40);
        equations.setFont(new Font("Monospaced", Font.PLAIN, 14));
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
                if (text.isEmpty()) return true;
                String[] lines = text.split("\\r?\\n", -1);
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
        leftPanel.add(scroll, "align left");
        // Add initial guess fields for Jacobi and Gauss-Seidel
        JLabel xLabel = new JLabel("x");
        JLabel yLabel = new JLabel("y");
        JLabel zLabel = new JLabel("z");
        JTextField guessX = new JTextField(8);
        JTextField guessY = new JTextField(8);
        JTextField guessZ = new JTextField(8);
        xLabel.setVisible(false);
        yLabel.setVisible(false);
        zLabel.setVisible(false);
        guessX.setVisible(false);
        guessY.setVisible(false);
        guessZ.setVisible(false);
        if (methodName.equals("Gauss-Seidel Method")) {
            JLabel guessLabel = new JLabel("Initial Guesses (x, y, z)");
            guessLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
            JPanel labelPanel = new JPanel(new MigLayout("wrap 3, gap 5, insets 0", "[grow][grow][grow]", "[]"));
            labelPanel.setBackground(new Color(0xF7F3F0));
            xLabel.setVisible(true);
            yLabel.setVisible(true);
            zLabel.setVisible(true);
            guessX.setVisible(true);
            guessY.setVisible(true);
            guessZ.setVisible(true);
            xLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
            yLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
            zLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
            xLabel.setHorizontalAlignment(SwingConstants.CENTER);
            yLabel.setHorizontalAlignment(SwingConstants.CENTER);
            zLabel.setHorizontalAlignment(SwingConstants.CENTER);
            labelPanel.add(xLabel, "growx");
            labelPanel.add(yLabel, "growx");
            labelPanel.add(zLabel, "growx");
            JPanel guessPanel = new JPanel(new MigLayout("wrap 3, gap 5, insets 0", "[grow][grow][grow]", "[]"));
            guessPanel.setBackground(new Color(0xF7F3F0));
            
            guessX.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
            guessY.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
            guessZ.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
            guessPanel.add(guessX, "growx");
            guessPanel.add(guessY, "growx");
            guessPanel.add(guessZ, "growx");
            leftPanel.add(guessLabel, "align left, gaptop 0");
            leftPanel.add(labelPanel, "align left, growx");
            leftPanel.add(guessPanel, "align left, growx");
        }
        // Add tolerance field below equations or guesses
        JLabel tolLabel = new JLabel("Tolerance");
        tolLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
        JTextField tolField = new JTextField(36);
        tolField.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(tolLabel, "align left, gaptop 5");
        leftPanel.add(tolField, "align left");
        // Add max iterations field for Jacobi and Gauss-Seidel
        JLabel maxIterLabel = new JLabel("Max Iterations");
        JTextField maxIterField = new JTextField(36);
        maxIterLabel.setVisible(false);
        maxIterField.setVisible(false);
        if (methodName.equals("Gauss-Seidel Method") || methodName.equals("Jacobi Method")) {
            maxIterLabel.setVisible(true);
            maxIterField.setVisible(true);
            maxIterLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
            
            maxIterField.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
            leftPanel.add(maxIterLabel, "align left, gaptop 5");
            leftPanel.add(maxIterField, "align left");
        }
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]", "[][][grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Solution:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            solution.setText("");
            try {
                double[][] matrix = methods.parseMatrixEquation(equations.getText());
                historyListModel.insertElementAt(equations.getText(), 0);
                if (methodName.equals("Cramer's Rule")) {
                    double[] answer = methods.cramer(matrix, solution);
                    methods.setTolerance(new BigDecimal(tolField.getText()));
                    solution.append("x = " + answer[0] + "\n");
                    solution.append("y = " + answer[1] + "\n");
                    solution.append("z = " + answer[2] + "\n");
                    history.push(new Tuple<>(equations.getText(), solution.getText()));
                } else if (methodName.equals("Jacobi Method")) {
                    methods.setTolerance(new BigDecimal(tolField.getText()));
                    methods.setTolerance(new BigDecimal(tolField.getText()));
                    methods.setMaxIteration(Integer.parseInt(maxIterField.getText()));
                    List<Double[]> answer = methods.jacobi(matrix, new LinkedList<Double[]>());
                    for(int i = 0; i < answer.size(); i++){
                        solution.append("Iteration No. " + (i) + ":\tx" + i +": " + answer.get(i)[0] + "\ty" + i + ": " + answer.get(i)[1] + "\tz" + i + ": " + answer.get(i)[2] + "\n");
                    }
                    
                    solution.append("\nRoot: " + answer.get(answer.size() - 1)[0] + ", " + answer.get(answer.size() - 1)[1] + ", " + answer.get(answer.size() - 1)[2] + "\n");
                    history.push(new Tuple<>(equations.getText(), solution.getText()));

                } else if (methodName.equals("Gaussian Elimination")) {
                    double[] answer = methods.gaussianElimination(matrix, solution);
                    methods.setTolerance(new BigDecimal(tolField.getText()));
                    solution.append("x = " + answer[0] + "\n");
                    solution.append("y = " + answer[1] + "\n");
                    solution.append("z = " + answer[2] + "\n");
                    history.push(new Tuple<>(equations.getText(), solution.getText()));
                } else if (methodName.equals("Gauss-Seidel Method")) {
                    Double[] guess = {Double.parseDouble(guessX.getText()), Double.parseDouble(guessY.getText()), Double.parseDouble(guessZ.getText())};
                    methods.setTolerance(new BigDecimal(tolField.getText()));
                    methods.setMaxIteration(Integer.parseInt(maxIterField.getText()));
                    List<Double[]> answer = methods.gaussSeidel(matrix, guess, new LinkedList<Double[]>());
                    
                    for(int i = 0; i < answer.size(); i++){
                        solution.append("Iteration No. " + (i) + ":\tx" + i +": " + answer.get(i)[0] + "\ty" + i + ": " + answer.get(i)[1] + "\tz" + i + ": " + answer.get(i)[2] + "\n");
                    }
                    solution.append("\nRoot: " + answer.get(answer.size() - 1)[0] + ", " + answer.get(answer.size() - 1)[1] + ", " + answer.get(answer.size() - 1)[2] + "\n");
                    history.push(new Tuple<>(equations.getText(), solution.getText()));
                }
                
            } catch (Exception ex) {
                solution.setText("Error:\n" + ex.getMessage());
            }
        });
        panel.add(calc, "span 2, align left, gaptop 10");
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
    private JButton calcButton() { // calc is short for calculator btw
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
        JPanel panel = new JPanel(new MigLayout("wrap 2, insets 20 30 20 20, gapx 10", "[grow][grow]", "[]10[]10[]"));
        panel.setBackground(new Color(0xF7F3F0));
        JLabel header = new JLabel("Matrix Multiplication");
        header.setFont(new Font("Bodoni MT", Font.BOLD, 20));
        header.setForeground(new Color(0x6B232C));
        panel.add(header, "span 2");
        JLabel desc = new JLabel("Enter your matrices A and B below. For 1D arrays, enter values separated by spaces. For 2D matrices, enter each row on a new line with values separated by spaces.");
        desc.setFont(new Font("Bodoni MT", Font.PLAIN, 13));
        panel.add(desc, "span 2, wrap, gapbottom 10");

        // Left side - Input form
        JPanel leftPanel = new JPanel(new MigLayout("wrap 1, fillx, gap 10", "[grow]", ""));
        leftPanel.setBackground(new Color(0xF7F3F0));

        // Label and text area for A
        JLabel labelA = new JLabel("Matrix A");
        labelA.setFont(new Font("Bodoni MT", Font.BOLD, 18));
        labelA.setHorizontalAlignment(SwingConstants.LEFT);
        leftPanel.add(labelA, "align left, gapbottom 2");
        JTextArea areaA = new RoundedTextArea(3, 30, 18);
        areaA.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaA.setLineWrap(true);
        areaA.setWrapStyleWord(true);
        areaA.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        areaA.setBackground(Color.WHITE);
        JScrollPane scrollA = new JScrollPane(areaA);
        scrollA.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollA.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollA.setBorder(BorderFactory.createEmptyBorder());
        scrollA.setPreferredSize(new Dimension(360, 80));
        scrollA.setOpaque(false);
        scrollA.getViewport().setOpaque(false);
        leftPanel.add(scrollA, "gapbottom 10");

        // Label and text area for B
        JLabel labelB = new JLabel("Matrix B");
        labelB.setFont(new Font("Bodoni MT", Font.BOLD, 18));
        labelB.setHorizontalAlignment(SwingConstants.LEFT);
        leftPanel.add(labelB, "align left, gapbottom 2");
        JTextArea areaB = new RoundedTextArea(3, 30, 18);
        areaB.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaB.setLineWrap(true);
        areaB.setWrapStyleWord(true);
        areaB.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        areaB.setBackground(Color.WHITE);
        JScrollPane scrollB = new JScrollPane(areaB);
        scrollB.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollB.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollB.setBorder(BorderFactory.createEmptyBorder());
        scrollB.setPreferredSize(new Dimension(360, 80));
        scrollB.setOpaque(false);
        scrollB.getViewport().setOpaque(false);
        leftPanel.add(scrollB, "gapbottom 10");

        // Add matrix type selection
        JLabel typeLabel = new JLabel("Matrix Type");
        typeLabel.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
        String[] types = {"1D Arrays", "2D Matrices"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(new Font("Bodoni MT", Font.PLAIN, 12));
        typeCombo.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12), BorderFactory.createEmptyBorder(8, 6, 8, 6)));
        leftPanel.add(typeLabel, "align left, gaptop 10");
        leftPanel.add(typeCombo, "align left");
        panel.add(leftPanel, "grow");

        // Right side - Solution display
        JPanel rightPanel = new JPanel(new MigLayout("fillx, wrap 1, gap 10", "[grow]"));
        rightPanel.setBackground(new Color(0xF7F3F0));
        JLabel solutionLabel = new JLabel("Result:");
        solutionLabel.setFont(new Font("Bodoni MT", Font.BOLD, 14));
        rightPanel.add(solutionLabel);
        
        JTextArea solution = new JTextArea(20, 30);
        solution.setFont(new Font("Monospaced", Font.PLAIN, 14));
        solution.setEditable(false);
        solution.setLineWrap(false);
        solution.setWrapStyleWord(false);
        solution.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));
        solution.setBackground(new Color(0xFFFAF7));
        JScrollPane solutionScroll = new JScrollPane(solution);
        solutionScroll.setBorder(BorderFactory.createEmptyBorder());
        solutionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(solutionScroll, "grow");
        panel.add(rightPanel, "grow");

        JButton calc = calcButton();
        calc.addActionListener(e -> {
            try {
                String matrixType = (String) typeCombo.getSelectedItem();
                solution.setText("");

                if (matrixType.equals("1D Arrays")) {
                    // Parse 1D arrays
                    String[] valuesA = areaA.getText().trim().split("\\s+");
                    String[] valuesB = areaB.getText().trim().split("\\s+");
                    
                    int[] arrayA = new int[valuesA.length];
                    int[] arrayB = new int[valuesB.length];
                    
                    for (int i = 0; i < valuesA.length; i++) {
                        arrayA[i] = Integer.parseInt(valuesA[i]);
                    }
                    for (int i = 0; i < valuesB.length; i++) {
                        arrayB[i] = Integer.parseInt(valuesB[i]);
                    }

                    // Perform multiplication
                    int[][] result = methods.matrixMultiplication(arrayA, arrayB);
                    
                    // Display result
                    solution.append("Result Matrix:\n");
                    for (int i = 0; i < result.length; i++) {
                        for (int j = 0; j < result[i].length; j++) {
                            solution.append(String.format("%6d", result[i][j]));
                        }
                        solution.append("\n");
                    }

                    // Add to history
                    String historyEntry = String.format("Matrix A: [%s] × Matrix B: [%s]", 
                        String.join(", ", valuesA), 
                        String.join(", ", valuesB));
                    historyListModel.insertElementAt(historyEntry, 0);
                    history.push(new Tuple<>(historyEntry, solution.getText()));
                } else {
                    // Parse 2D matrices
                    String[] matrixARows = areaA.getText().split("\\n");
                    String[] matrixBRows = areaB.getText().split("\\n");
                    
                    int[][] matrixA = new int[matrixARows.length][];
                    int[][] matrixB = new int[matrixBRows.length][];
                    
                    for (int i = 0; i < matrixARows.length; i++) {
                        String[] values = matrixARows[i].trim().split("\\s+");
                        matrixA[i] = new int[values.length];
                        for (int j = 0; j < values.length; j++) {
                            matrixA[i][j] = Integer.parseInt(values[j]);
                        }
                    }
                    
                    for (int i = 0; i < matrixBRows.length; i++) {
                        String[] values = matrixBRows[i].trim().split("\\s+");
                        matrixB[i] = new int[values.length];
                        for (int j = 0; j < values.length; j++) {
                            matrixB[i][j] = Integer.parseInt(values[j]);
                        }
                    }

                    // Check if matrices can be multiplied
                    if (matrixA[0].length != matrixB.length) {
                        solution.setText("Error: Number of columns in Matrix A must equal number of rows in Matrix B");
                        return;
                    }

                    // Perform multiplication
                    int[][] result = methods.matrixMultiplication(matrixA, matrixB);
                    
                    // Display result
                    solution.append("Result Matrix:\n");
                    for (int i = 0; i < result.length; i++) {
                        for (int j = 0; j < result[i].length; j++) {
                            solution.append(String.format("%6d", result[i][j]));
                        }
                        solution.append("\n");
                    }

                    // Add to history
                    StringBuilder historyEntry = new StringBuilder("Matrix A:\n");
                    for (String row : matrixARows) {
                        historyEntry.append(row).append("\n");
                    }
                    historyEntry.append("×\nMatrix B:\n");
                    for (String row : matrixBRows) {
                        historyEntry.append(row).append("\n");
                    }
                    historyListModel.insertElementAt(historyEntry.toString().trim(), 0);
                    history.push(new Tuple<>(historyEntry.toString().trim(), solution.getText()));
                }
                
            } catch (Exception ex) {
                solution.setText("Error:\n" + ex.getMessage());
            }
        });
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