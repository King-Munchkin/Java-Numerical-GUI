package gui_gui;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class GUI_GUI extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final HashMap<String, JPanel> methodPanels = new HashMap<>();

    public GUI_GUI() {
        setTitle("Numerical Methods GUI");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Dark mode colors
        Color bgDark = new Color(33, 33, 33);
        Color textColor = new Color(230, 230, 230);
        Color btnColor = new Color(55, 71, 79);
        Color btnHover = new Color(69, 90, 100);

        // Sidebar
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(10, 1, 5, 5));
        sidePanel.setBackground(bgDark);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Method names
        String[] methods = {
            "Fixed Point Iteration",
            "Newton-Raphson Method",
            "Secant Method",
            "Bisection Method",
            "False Position Method",
            "Matrix Operations",
            "Cramer's Rule",
            "Gaussian Elimination",
            "Jacobi Method",
            "Gauss-Seidel Method"
        };

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(bgDark);

        // Add method panels and buttons
        for (String method : methods) {
            JButton button = createSidebarButton(method, btnColor, btnHover, textColor);
            sidePanel.add(button);

            JPanel methodPanel = createMethodPanel(method, bgDark, textColor);
            methodPanels.put(method, methodPanel);
            contentPanel.add(methodPanel, method);

            // Show panel on button click (not hover!)
            button.addActionListener(e -> showPanel(method));
        }

        add(sidePanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Show first panel by default
        showPanel(methods[0]);
    }

    private JButton createSidebarButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private JPanel createMethodPanel(String title, Color bg, Color text) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(text);
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.add(header, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        inputPanel.setBackground(bg);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(48, 48, 48));
        resultArea.setForeground(Color.WHITE);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JTextField exprField = null; // For f(x) or g(x)
        JTextField xInput = new JTextField("1.0");

        switch (title) {
            case "Fixed Point Iteration":
                inputPanel.add(createLabel("g(x) =", text));
                exprField = new JTextField("e^-x = 0");
                inputPanel.add(exprField);

                inputPanel.add(createLabel("Initial Guess x₀:", text));
                JTextField fx0 = new JTextField("0");
                inputPanel.add(fx0);

                inputPanel.add(createLabel("Tolerance (ε):", text));
                JTextField tolField = new JTextField("0.001");
                inputPanel.add(tolField);

                JButton runFixedPoint = new JButton("Run Fixed Point Iteration");
                inputPanel.add(runFixedPoint);
                inputPanel.add(new JLabel("")); // spacer

                JTextField finalExprFieldFPI = exprField;
                runFixedPoint.addActionListener(e -> {
                    try {
                        String tempt = finalExprFieldFPI.getText();
                        String gexpr = cleanExpression(tempt);
                        double x0 = Double.parseDouble(fx0.getText());
                        double tol = Double.parseDouble(tolField.getText());

                        StringBuilder sb = new StringBuilder();
                        String result = runFixedPointIteration(gexpr, x0, tol, 1, 100, sb);

                        resultArea.setText(result);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });

                break;

            case "Newton-Raphson Method":
                inputPanel.add(createLabel("f(x) =", text));
                exprField = new JTextField("2^x - 5*x + 2 = 0");
                inputPanel.add(exprField);

                inputPanel.add(createLabel("Initial Guess x₀:", text));
                JTextField newtonX0 = new JTextField("0");
                inputPanel.add(newtonX0);

                inputPanel.add(createLabel("Tolerance (ε):", text));
                JTextField newtonTol = new JTextField("0.0001");
                inputPanel.add(newtonTol);

                JButton runNewton = new JButton("Run Newton-Raphson");
                inputPanel.add(runNewton);
                inputPanel.add(new JLabel("")); // spacer

                JTextField finalExprFieldNewton = exprField;
                runNewton.addActionListener(e -> {
                    try {
                        String tempt = finalExprFieldNewton.getText();
                        String fExpr = cleanExpression(tempt);
                        Expression f = new ExpressionBuilder(fExpr).variable("x").build();
                        double x0 = Double.parseDouble(newtonX0.getText());
                        double tol = Double.parseDouble(newtonTol.getText());
                        String output = runNewtonRaphsonAuto(f, x0, tol);
                        resultArea.setText(output);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;


            case "Secant Method":
                inputPanel.add(createLabel("f(x) =", text));
                exprField = new JTextField("x^3 - x - 1 = 0");
                inputPanel.add(exprField);

                inputPanel.add(createLabel("x₀:", text));
                JTextField secX0 = new JTextField("1.2");
                inputPanel.add(secX0);

                inputPanel.add(createLabel("x₁:", text));
                JTextField secX1 = new JTextField("1.4");
                inputPanel.add(secX1);

                inputPanel.add(createLabel("Tolerance (ε):", text));
                JTextField secTol = new JTextField("0.0001");
                inputPanel.add(secTol);

                JButton runSecant = new JButton("Run Secant Method");
                inputPanel.add(runSecant);
                inputPanel.add(new JLabel("")); // spacer

                JTextField finalExprFieldSec = exprField;
                runSecant.addActionListener(e -> {
                    try {
                        String tempt = finalExprFieldSec.getText();
                        String fExpr = cleanExpression(tempt);
                        double x0 = Double.parseDouble(secX0.getText());
                        double x1 = Double.parseDouble(secX1.getText());
                        double tol = Double.parseDouble(secTol.getText());
                        String output = runSecantMethod(fExpr, x0, x1, tol);
                        resultArea.setText(output);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
                break;
            case "Bisection Method":
                inputPanel.add(createLabel("f(x) =", text));
                exprField = new JTextField("x^3 + 4x^2 - 10 = 0");
                inputPanel.add(exprField);

                inputPanel.add(createLabel("a:", text));
                JTextField aField = new JTextField("1");
                inputPanel.add(aField);

                inputPanel.add(createLabel("b:", text));
                JTextField bField = new JTextField("2");
                inputPanel.add(bField);

                inputPanel.add(createLabel("Tolerance (ε):", text));
                JTextField bisectTol = new JTextField("0.0001");
                inputPanel.add(bisectTol);

                JButton runBisection = new JButton("Run Bisection Method");
                inputPanel.add(runBisection);
                inputPanel.add(new JLabel("")); // spacer

                JTextField finalExprFieldBisect = exprField;
                runBisection.addActionListener(e -> {
                    try {
                        String tempt = finalExprFieldBisect.getText();
                        String fExpr = cleanExpression(tempt);
                        double a = Double.parseDouble(aField.getText());
                        double b = Double.parseDouble(bField.getText());
                        double tol = Double.parseDouble(bisectTol.getText());
                        String output = runBisectionMethod(fExpr, a, b, tol);
                        resultArea.setText(output);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;
            case "False Position Method":
                inputPanel.add(createLabel("f(x) =", text));
                exprField = new JTextField("x^3 - 4cos(x) = 0");
                inputPanel.add(exprField);

                inputPanel.add(createLabel("x₀:", text));
                JTextField x0Field = new JTextField("1");
                inputPanel.add(x0Field);

                inputPanel.add(createLabel("x₁:", text));
                JTextField x1Field = new JTextField("2");
                inputPanel.add(x1Field);

                inputPanel.add(createLabel("Tolerance (ε):", text));
                JTextField gstolField = new JTextField("0.001");
                inputPanel.add(gstolField);

                JButton runFalsePosition = new JButton("Run False-Position Method");
                inputPanel.add(runFalsePosition);
                inputPanel.add(new JLabel("")); // spacer

                JTextField finalExprFieldFalsePos = exprField;
                runFalsePosition.addActionListener(e -> {
                    try {
                        String tempt = finalExprFieldFalsePos.getText();
                        String fExpr = cleanExpression(tempt);
                        double x0 = Double.parseDouble(x0Field.getText());
                        double x1 = Double.parseDouble(x1Field.getText());
                        double tol = Double.parseDouble(gstolField.getText());
                        String output = runFalsePositionMethod(fExpr, x0, x1, tol);
                        resultArea.setText(output);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;
            case "Gaussian Elimination":
                inputPanel.add(createLabel("Equation 1 (e.g. 2x - y + 3z = 5):", text));
                JTextField gEq1Field = new JTextField("2x - y + 3z = 5");
                inputPanel.add(gEq1Field);

                inputPanel.add(createLabel("Equation 2:", text));
                JTextField gEq2Field = new JTextField("x + 4y - 2z = 1");
                inputPanel.add(gEq2Field);

                inputPanel.add(createLabel("Equation 3:", text));
                JTextField gEq3Field = new JTextField("3x + y + 5z = 2");
                inputPanel.add(gEq3Field);

                JButton runGaussian = new JButton("Solve using Gaussian Elimination");
                inputPanel.add(runGaussian);
                inputPanel.add(new JLabel(""));

                runGaussian.addActionListener(e -> {
                    try {
                        double[][] A = new double[3][3];
                        double[] B = new double[3];

                        parseEquation(gEq1Field.getText(), A, B, 0);
                        parseEquation(gEq2Field.getText(), A, B, 1);
                        parseEquation(gEq3Field.getText(), A, B, 2);

                        double[] solution = gaussianElimination(A, B);
                        resultArea.setText("Solution:\n" +
                                "x = " + solution[0] + "\n" +
                                "y = " + solution[1] + "\n" +
                                "z = " + solution[2]);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;



            case "Gauss-Seidel Method":
                inputPanel.add(createLabel("Equation 1 (e.g. 10x + 2y + z = 9):", text));
                JTextField gsEq1 = new JTextField("10x + 2y + z = 9");
                inputPanel.add(gsEq1);

                inputPanel.add(createLabel("Equation 2 (e.g. 2x + 20y - 2z = -44):", text));
                JTextField gsEq2 = new JTextField("2x + 20y - 2z = -44");
                inputPanel.add(gsEq2);

                inputPanel.add(createLabel("Equation 3 (e.g. -2x + 3y + 10z = 22):", text));
                JTextField gsEq3 = new JTextField("-2x + 3y + 10z = 22");
                inputPanel.add(gsEq3);

                inputPanel.add(createLabel("Tolerance (e.g. 1e-3):", text));
                
                JTextField epsilonField = new JTextField("1e-3");
                inputPanel.add(epsilonField);

                inputPanel.add(createLabel("Initial guess x(0):", text));
                JTextField x01Field = new JTextField("0");
                inputPanel.add(x01Field);

                inputPanel.add(createLabel("Initial guess y(0):", text));
                JTextField y0Field = new JTextField("0");
                inputPanel.add(y0Field);

                inputPanel.add(createLabel("Initial guess z(0):", text));
                JTextField z0Field = new JTextField("0");
                inputPanel.add(z0Field);


                JButton runGaussSeidel = new JButton("Solve using Gauss-Seidel Method");
                inputPanel.add(runGaussSeidel);
                inputPanel.add(new JLabel("")); // spacer

                runGaussSeidel.addActionListener(e -> {
                    try {
                        double[][] A = new double[3][3];
                        double[] B = new double[3];

                        parseEquation(gsEq1.getText(), A, B, 0);
                        parseEquation(gsEq2.getText(), A, B, 1);
                        parseEquation(gsEq3.getText(), A, B, 2);

                        double epsilon = Double.parseDouble(epsilonField.getText());

                        double[] initialGuess = new double[3];
                        initialGuess[0] = Double.parseDouble(x01Field.getText());
                        initialGuess[1] = Double.parseDouble(y0Field.getText());
                        initialGuess[2] = Double.parseDouble(z0Field.getText());

                        String result = solveGaussSeidelWithConvergence(A, B, epsilon, initialGuess);
                        resultArea.setText(result);

                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });

            break;


            case "Matrix Operations":
                inputPanel.add(createLabel("Matrix A (e.g. [5,6,7,8]):", text));
                JTextField matrixAField = new JTextField("[5,6,7,8]");
                inputPanel.add(matrixAField);

                inputPanel.add(createLabel("Matrix B (e.g. [1,2,3,4]):", text));
                JTextField matrixBField = new JTextField("[1,2,3,4]");
                inputPanel.add(matrixBField);

                JButton runMatrixProduct = new JButton("Find Product A × B");
                inputPanel.add(runMatrixProduct);
                inputPanel.add(new JLabel("")); // spacer

                runMatrixProduct.addActionListener(e -> {
                    try {
                        double[][] A = parseMatrix(matrixAField.getText());
                        double[][] B = parseMatrix(matrixBField.getText());
                        double[][] result = multiplyMatrices(A, B);
                        resultArea.setText("Product of A and B:\n" + matrixToString(result));
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;

            case "Cramer's Rule":
                inputPanel.add(createLabel("Equation 1 (e.g. 2x + y - z = 1):", text));
                JTextField eq1Field = new JTextField("2x + y - z = 1");
                inputPanel.add(eq1Field);

                inputPanel.add(createLabel("Equation 2 (e.g. 3x - y + z = 4):", text));
                JTextField eq2Field = new JTextField("3x - y + z = 4");
                inputPanel.add(eq2Field);

                inputPanel.add(createLabel("Equation 3 (e.g. 2x + 3y + z = 3):", text));
                JTextField eq3Field = new JTextField("2x + 3y + z = 3");
                inputPanel.add(eq3Field);

                JButton runCramer = new JButton("Solve using Cramer's Rule");
                inputPanel.add(runCramer);
                inputPanel.add(new JLabel("")); // spacer

                runCramer.addActionListener(e -> {
                    try {
                        double[][] A = new double[3][3];
                        double[] B = new double[3];

                        parseEquation(eq1Field.getText(), A, B, 0);
                        parseEquation(eq2Field.getText(), A, B, 1);
                        parseEquation(eq3Field.getText(), A, B, 2);

                        double[] solution = solveCramer(A, B);
                        resultArea.setText("Solution:\n" +
                                "x = " + solution[0] + "\n" +
                                "y = " + solution[1] + "\n" +
                                "z = " + solution[2]);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;
            case "Jacobi Method":
                inputPanel.add(createLabel("Equation 1 (e.g. 4x + 22y - 13z = -128):", text));
                JTextField jacobiEq1 = new JTextField("4x + 22y - 13z = -128");
                inputPanel.add(jacobiEq1);

                inputPanel.add(createLabel("Equation 2 (e.g. 19x - 13y + 4z = 111):", text));
                JTextField jacobiEq2 = new JTextField("19x - 13y + 4z = 111");
                inputPanel.add(jacobiEq2);

                inputPanel.add(createLabel("Equation 3 (e.g. 8x + 8y + 17z = 10):", text));
                JTextField jacobiEq3 = new JTextField("8x + 8y + 17z = 10");
                inputPanel.add(jacobiEq3);

                inputPanel.add(createLabel("Iterations:", text));
                JTextField iterationField = new JTextField("5");
                inputPanel.add(iterationField);

                JButton runJacobi = new JButton("Solve using Jacobi Method");
                inputPanel.add(runJacobi);
                inputPanel.add(new JLabel("")); // spacer

                runJacobi.addActionListener(e -> {
                    try {
                        double[][] A = new double[3][3];
                        double[] B = new double[3];

                        parseEquation(jacobiEq1.getText(), A, B, 0);
                        parseEquation(jacobiEq2.getText(), A, B, 1);
                        parseEquation(jacobiEq3.getText(), A, B, 2);

                        int iterations = Integer.parseInt(iterationField.getText());
                        double[] solution = solveJacobi(A, B, iterations);

                        resultArea.setText("After " + iterations + " iterations:\n" +
                            "x = " + solution[0] + "\n" +
                            "y = " + solution[1] + "\n" +
                            "z = " + solution[2]);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
            break;


            

            default:
                // For other methods, just simple expression evaluation
                inputPanel.add(createLabel("Expression (f(x)):", text));
                exprField = new JTextField("x^2 + 2*x + 1");
                inputPanel.add(exprField);

                inputPanel.add(createLabel("Evaluate at x =", text));
                inputPanel.add(xInput);

                JButton runButton = new JButton("Evaluate Expression");
                inputPanel.add(runButton);
                inputPanel.add(new JLabel("")); // spacer

                JTextField finalExprField = exprField;
                runButton.addActionListener(e -> {
                    try {
                        String expr = finalExprField.getText();
                        double xVal = Double.parseDouble(xInput.getText());
                        double result = MathParser.evaluate(expr, xVal);
                        resultArea.setText("Result at x = " + xVal + ":\n" + result);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });
                break;
        }

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String text, Color fg) {
        JLabel label = new JLabel(text);
        label.setForeground(fg);
        return label;
    }

    private void showPanel(String methodName) {
        JPanel panel = methodPanels.get(methodName);
        if (panel != null) {
            // No animation for now (or you can keep your fade animation if preferred)
            cardLayout.show(contentPanel, methodName);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------//
    //                                                Methods Formulas                                                 //
    // ----------------------------------------------------------------------------------------------------------------//

    // Gauss-Seidel Method
    public static String solveGaussSeidelWithConvergence(double[][] A, double[] B, double epsilon, double[] initialGuess) {
        int n = B.length;
        double[] x = new double[n];
        System.arraycopy(initialGuess, 0, x, 0, n);  // Initialize with initial guesses

        double[] xOld = new double[n];
        StringBuilder output = new StringBuilder();

        int iteration = 0;
        boolean converged;

        do {
            iteration++;
            System.arraycopy(x, 0, xOld, 0, n);

            for (int i = 0; i < n; i++) {
                double sum = B[i];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum -= A[i][j] * x[j];
                    }
                }
                x[i] = sum / A[i][i];
            }

            // Print current iteration results
            output.append(String.format("Iteration %d:\n", iteration));
            output.append(String.format("x = %.6f, y = %.6f, z = %.6f\n\n", x[0], x[1], x[2]));

            // Check convergence (max absolute difference < epsilon)
            converged = true;
            for (int i = 0; i < n; i++) {
                if (Math.abs(x[i] - xOld[i]) > epsilon) {
                    converged = false;
                    break;
                }
            }
        } while (!converged);

        output.append("Converged solution:\n");
        output.append(String.format("x ≈ %.4f, y ≈ %.4f, z ≈ %.4f", x[0], x[1], x[2]));

        return output.toString();
    }




    // Newton Derivative
    private double derivative(Expression expr, double x) {
        double h = 1e-6;
        return (expr.setVariable("x", x + h).evaluate() - expr.setVariable("x", x - h).evaluate()) / (2 * h);
    }
    // Newton Support
    private String runNewtonRaphsonAuto(Expression fExpr, double x0, double tol) {
        StringBuilder sb = new StringBuilder();
        int maxIter = 100;
        double x = x0;

        for (int i = 1; i <= maxIter; i++) {
            double fx = fExpr.setVariable("x", x).evaluate();
            double dfx = (fExpr.setVariable("x", x + 1e-6).evaluate() - fExpr.setVariable("x", x - 1e-6).evaluate()) / (2e-6);

            if (dfx == 0) {
                return "Derivative is zero. Method fails.";
            }

            double x1 = x - fx / dfx;
            sb.append(String.format("Iteration %d: x = %.6f\n", i, x1));

            if (Math.abs(x1 - x) < tol) {
                sb.append("Root found: ").append(x1);
                return sb.toString();
            }

            x = x1;
        }
        return "Method did not converge within max iterations.";
    }



    // Secant Method implementation
    private String runSecantMethod(String fExpr, double x0, double x1, double tol) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("Secant Method:\n");
        int maxIter = 100;
        int iter = 0;
        double f0 = MathParser.evaluate(fExpr, x0);
        double f1 = MathParser.evaluate(fExpr, x1);
        while (iter < maxIter) {
            if (f1 - f0 == 0) throw new Exception("Division by zero in secant formula");
            double x2 = x1 - f1 * (x1 - x0) / (f1 - f0);
            sb.append(String.format("Iter %d: x = %.6f\n", iter + 1, x2));
            if (Math.abs(x2 - x1) < tol) {
                sb.append("Converged to root: ").append(String.format("%.6f", x2));
                return sb.toString();
            }
            x0 = x1;
            f0 = f1;
            x1 = x2;
            f1 = MathParser.evaluate(fExpr, x1);
            iter++;
        }
        sb.append("Did not converge within ").append(maxIter).append(" iterations.");
        return sb.toString();
    }
    // Bisection Method
    public static String runBisectionMethod(String fExpr, double a, double b, double tol) throws Exception {
        StringBuilder sb = new StringBuilder();
        int maxIter = 100;
        double fa = MathParser.evaluate(fExpr, a);
        double fb = MathParser.evaluate(fExpr, b);

        if (fa * fb > 0) {
            return "f(a) and f(b) must have opposite signs.";
        }

        sb.append("Iter\t a\t b\t c\t f(c)\n");

        double c = a;
        for (int i = 1; i <= maxIter; i++) {
            c = (a + b) / 2;
            double fc = MathParser.evaluate(fExpr, c);
            sb.append(String.format("%d\t %.6f\t %.6f\t %.6f\t %.6f\n", i, a, b, c, fc));

            if (Math.abs(fc) < tol || (b - a) / 2 < tol) {
                sb.append("\nRoot ≈ ").append(c);
                return sb.toString();
            }

            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }

        sb.append("\nMaximum iterations reached. Final approximation: ").append(c);
        return sb.toString();
    }
    // Gaussian Elimination Method
    public static double[] runGaussianElimination(double[][] A, double[] b) {
        int n = A.length;

        for (int i = 0; i < n; i++) {
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) {
                    max = j;
                }
            }

            double[] temp = A[i]; A[i] = A[max]; A[max] = temp;
            double t = b[i]; b[i] = b[max]; b[max] = t;

            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                b[j] -= factor * b[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }

        return x;
    }

    // Gauss Method Support
    public static double[] solveGaussSeidel(double[][] A, double[] B, int maxIterations) {
        double[] x = new double[3]; // Initial guess: x=0, y=0, z=0
        double epsilon = 1e-3;

        for (int iter = 0; iter < maxIterations; iter++) {
            double x0 = x[0], x1 = x[1], x2 = x[2];

            x[0] = (B[0] - A[0][1] * x[1] - A[0][2] * x[2]) / A[0][0];
            x[1] = (B[1] - A[1][0] * x[0] - A[1][2] * x[2]) / A[1][1];
            x[2] = (B[2] - A[2][0] * x[0] - A[2][1] * x[1]) / A[2][2];

            if (Math.abs(x[0] - x0) < epsilon &&
                Math.abs(x[1] - x1) < epsilon &&
                Math.abs(x[2] - x2) < epsilon) {
                break;
            }
        }

        return x;
    }

    // False Position Method
    public static String runFalsePositionMethod(String expression, double x0, double x1, double tol) throws Exception {
        StringBuilder sb = new StringBuilder();
        Expression e = new ExpressionBuilder(expression).variable("x").build();

        double f0 = e.setVariable("x", x0).evaluate();
        double f1 = e.setVariable("x", x1).evaluate();

        if (f0 * f1 > 0) {
            return "Error: f(x0) and f(x1) must have opposite signs.";
        }

        double x2, f2;
        int iter = 0;

        sb.append(String.format("%-15s %-15s %-20s %-20s %-25s\n", "Iter", "x0", "x1", "x2", "f(x2)"));
        do {
            x2 = x1 - (f1 * (x1 - x0)) / (f1 - f0);
            f2 = e.setVariable("x", x2).evaluate();
            sb.append(String.format("%-5d %-15f %-15f %-15f %-15f\n", iter, x0, x1, x2, f2));

            if (f0 * f2 < 0) {
                x1 = x2;
                f1 = f2;
            } else {
                x0 = x2;
                f0 = f2;
            }

            iter++;
        } while (Math.abs(f2) > tol);

        sb.append("\nApproximate Root: ").append(x2);
        return sb.toString();
    }
    // Matrix Method
    public static double[][] multiplyMatrices(double[][] A, double[][] B) throws Exception {
        if (A[0].length != B.length && B.length != 1) {
            throw new Exception("Matrix dimensions mismatch: Cannot multiply A (" +
                    A.length + "x" + A[0].length + ") and B (" +
                    B.length + "x" + B[0].length + ")");
        }

        // Transpose B if it is also a 1D row vector
        if (B.length == 1) {
            double[][] B_T = new double[B[0].length][1];
            for (int i = 0; i < B[0].length; i++) {
                B_T[i][0] = B[0][i];
            }
            B = B_T;
        }

        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }
    // Matrix to String
    public static String matrixToString(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }
    // Matrix Parser
    public static double[][] parseMatrix(String input) throws Exception {
        input = input.trim().replaceAll("[\\[\\]]", ""); // remove brackets
        String[] values = input.split(",");
        double[][] matrix = new double[1][values.length]; // row vector
        for (int i = 0; i < values.length; i++) {
            matrix[0][i] = Double.parseDouble(values[i].trim());
        }
        return matrix;
    }
    // Cramer's Rule Method
    public static double[] solveCramer(double[][] A, double[] B) throws Exception {
        double detA = determinant3x3(A);
        if (detA == 0) throw new Exception("System has no unique solution (det = 0)");

        double[][] Ax = replaceColumn(A, B, 0);
        double[][] Ay = replaceColumn(A, B, 1);
        double[][] Az = replaceColumn(A, B, 2);

        return new double[] {
            determinant3x3(Ax) / detA,
            determinant3x3(Ay) / detA,
            determinant3x3(Az) / detA
        };
    }
    public static double determinant3x3(double[][] m) {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1]) -
            m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0]) +
            m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }
    public static double[][] replaceColumn(double[][] matrix, double[] column, int colIndex) {
        double[][] copy = new double[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, 3);
            copy[i][colIndex] = column[i];
        }
        return copy;
    }

    // Cramer Equation parser
    public static void parseEquation(String equation, double[][] A, double[] B, int row) throws Exception {
        equation = equation.replaceAll("\\s+", "").toLowerCase();

        String[] sides = equation.split("=");
        if (sides.length != 2) throw new Exception("Equation must have an '=' sign.");

        String lhs = sides[0];
        double rhs = Double.parseDouble(sides[1]);

        double[] coeffs = new double[3]; // For x, y, z

        Pattern termPattern = Pattern.compile("([+-]?[^+-]+)");
        Matcher matcher = termPattern.matcher(lhs);

        while (matcher.find()) {
            String term = matcher.group(1);

            double coefficient = 1.0;
            if (term.contains("x")) {
                term = term.replace("x", "");
                if (!term.equals("") && !term.equals("+") && !term.equals("-"))
                    coefficient = Double.parseDouble(term);
                else if (term.equals("-")) coefficient = -1.0;
                coeffs[0] += coefficient;
            } else if (term.contains("y")) {
                term = term.replace("y", "");
                if (!term.equals("") && !term.equals("+") && !term.equals("-"))
                    coefficient = Double.parseDouble(term);
                else if (term.equals("-")) coefficient = -1.0;
                coeffs[1] += coefficient;
            } else if (term.contains("z")) {
                term = term.replace("z", "");
                if (!term.equals("") && !term.equals("+") && !term.equals("-"))
                    coefficient = Double.parseDouble(term);
                else if (term.equals("-")) coefficient = -1.0;
                coeffs[2] += coefficient;
            } else {
                throw new Exception("Invalid term: " + term);
            }
        }

        // Store into matrix
        A[row][0] = coeffs[0];
        A[row][1] = coeffs[1];
        A[row][2] = coeffs[2];
        B[row] = rhs;
    }
    // Jacobi Method
    public static double[] solveJacobi(double[][] A, double[] B, int maxIterations) {
        double[] x = new double[3];       // current estimates
        double[] prevX = new double[3];   // previous estimates
        double epsilon = 1e-3;

        for (int iter = 0; iter < maxIterations; iter++) {
            System.arraycopy(x, 0, prevX, 0, 3);

            x[0] = (B[0] - A[0][1] * prevX[1] - A[0][2] * prevX[2]) / A[0][0];
            x[1] = (B[1] - A[1][0] * prevX[0] - A[1][2] * prevX[2]) / A[1][1];
            x[2] = (B[2] - A[2][0] * prevX[0] - A[2][1] * prevX[1]) / A[2][2];

            // Optional early stopping
            if (Math.abs(x[0] - prevX[0]) < epsilon &&
                Math.abs(x[1] - prevX[1]) < epsilon &&
                Math.abs(x[2] - prevX[2]) < epsilon) {
                break;
            }
        }

        return x;
    }


    // Cramer Support
    private static double parseTerm(String s) {
        if (s.equals("") || s.equals("+")) return 1;
        if (s.equals("-")) return -1;
        return Double.parseDouble(s);
    }
    // Gaussian Elimination Method
        public static double[] gaussianElimination(double[][] A, double[] B) throws Exception {
        int n = A.length;

        System.out.println("Input Matrix A:");
        for (double[] row : A) System.out.println(Arrays.toString(row));
        System.out.println("Input Vector B:");
        System.out.println(Arrays.toString(B));

        // Forward Elimination with Partial Pivoting
        for (int i = 0; i < n; i++) {
            // Pivoting
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(A[k][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = k;
                }
            }

            // Swap rows in A using recursive function
            recursiveSwapRows(A, i, maxRow, 0);

            // Swap vector B entries manually
            double tempVal = B[i];
            B[i] = B[maxRow];
            B[maxRow] = tempVal;

            if (Math.abs(A[i][i]) < 1e-10)
                throw new Exception("No unique solution (zero pivot encountered)");

            // Eliminate
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
                B[j] -= factor * B[i];
            }

            // Debug after each step
            System.out.println("After pivoting step " + i + ":");
            for (int r = 0; r < n; r++) {
                System.out.println(Arrays.toString(A[r]) + " | " + B[r]);
            }
        }

        // Back substitution (assume recursive implemented)
        double[] x = new double[n];
        x = backSubstitution(A, B, n - 1, x);

        return x;
    }



    // = 0 Cleaning Cleaner hehe
    private String cleanExpression(String expr) {
        if (expr == null) return "";
        // Remove trailing "= 0" or "=0" with optional spaces before 0
        return expr.replaceAll("\\s*=\\s*0\\s*$", "").trim();
    }
    //--------------------------------------------------------------------------------------------------------------\\
    //                                              Recursive Functions                                             \\
    //--------------------------------------------------------------------------------------------------------------\\

    private static double[] backSubstitution(double[][] A, double[] B, int i, double[] x) {
        int n = A.length;
        if (i < 0) return x; // base case: done
        
        double sum = B[i];
        for (int j = i + 1; j < n; j++) {
            sum -= A[i][j] * x[j];
        }
        x[i] = sum / A[i][i];
        
        return backSubstitution(A, B, i - 1, x);
    }
    public static void recursiveSwapRows(double[][] A, int row1, int row2, int col) {
        if (col >= A[0].length) return;

        double temp = A[row1][col];
        A[row1][col] = A[row2][col];
        A[row2][col] = temp;

        recursiveSwapRows(A, row1, row2, col + 1);
    }

    private String runFixedPointIteration(String gExpr, double xPrev, double tol, int iter, int maxIter, StringBuilder sb) throws Exception {
        if (iter > maxIter) {
            sb.append("Did not converge within ").append(maxIter).append(" iterations.");
            return sb.toString();
        }
        
        double xNext = MathParser.evaluate(gExpr, xPrev);
        sb.append(String.format("Iter %d: x = %.6f\n", iter, xNext));
        
        if (Math.abs(xNext - xPrev) < tol) {
            sb.append("Converged to root: ").append(String.format("%.6f", xNext));
            return sb.toString();
        }
        
        return runFixedPointIteration(gExpr, xNext, tol, iter + 1, maxIter, sb);
    }
    public static void recursiveForwardElimination(double[][] A, double[] B, int n, int i) throws Exception {
        if (i >= n) return;

        // Partial pivoting
        int maxRow = i;
        for (int k = i + 1; k < n; k++) {
            if (Math.abs(A[k][i]) > Math.abs(A[maxRow][i])) {
                maxRow = k;
            }
        }

        if (maxRow != i) {
            recursiveSwapRows(A, i, maxRow, 0);

            double temp = B[i];
            B[i] = B[maxRow];
            B[maxRow] = temp;

        }

        if (Math.abs(A[i][i]) < 1e-10) {
            throw new Exception("No unique solution (zero pivot encountered)");
        }

        for (int j = i + 1; j < n; j++) {
            double factor = A[j][i] / A[i][i];
            for (int k = i; k < n; k++) {
                A[j][k] -= factor * A[i][k];
            }
            B[j] -= factor * B[i];
        }

        // Recursive call for next row
        recursiveForwardElimination(A, B, n, i + 1);
    }
    public static int gcdRecursive(int a, int b) {
        if (b == 0) return a;
        return gcdRecursive(b, a % b);
    }



//------------------------------------------------------------------------------------------------------------------------\\
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI_GUI().setVisible(true);
        });
    }
}
//------------------------------------------------------------------------------------------------------------------------\\
                      // ANOTHER CLASS HEHE \\
class MathParser {
    public static double evaluate(String expression, double x) throws Exception {
        try {
            Expression e = new ExpressionBuilder(expression)
                .variable("x")
                .build()
                .setVariable("x", x);
            return e.evaluate();
        } catch (Exception ex) {
            throw new Exception("Invalid expression: " + ex.getMessage());
        }
    }
}
