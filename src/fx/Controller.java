package fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class Controller {
    @FXML
    private Label label;
    @FXML
    private BorderPane mainPane;

    private void loadPage(String fxmlName) {
        Pane view = Loader.getPage(fxmlName);
        mainPane.setCenter(view);
    }

    public void Fixed_Point(ActionEvent e) {
        System.out.println("Fixed Point Method");
        loadPage("FixedPoint");
    }
    public void Newton_Raphson(ActionEvent e) {
        System.out.println("Newton-Raphson Method");
        loadPage("NewtonRaphson");
    }
    public void Secant(ActionEvent e) {
        System.out.println("Secant Method");
        loadPage("Secant"); 
    }
    public void Bisection(ActionEvent e) {
        System.out.println("Bisection Method");
        loadPage("Bisection");
    }
    public void False_Position(ActionEvent e) {
        System.out.println("False Position Method");
        loadPage("FalsePosition");
    }
    public void Matrix(ActionEvent e) {
        System.out.println("Matrix Method");
        loadPage("Matrix");
    }
    public void Cramers_Rule(ActionEvent e) {
        System.out.println("Cramer's Rule Method");
        loadPage("CramersRule");
    }
    public void Gaussian_Elimination(ActionEvent e) {
        System.out.println("Gaussian Elimination Method");
        loadPage("Gaussian");
    }
    public void Jacobi(ActionEvent e) {
        System.out.println("Jacobi Method");
        loadPage("Jacobi");
    }
    public void Gauss_Seidel(ActionEvent e) {
        System.out.println("Gauss-Seidel Method");
        loadPage("GaussSeidel");
    }
    // Solution Controller

    public void SolutionWindow (ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/Solution.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Solution");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}



