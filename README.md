import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class EcoCodeApp extends Application {

    private VBox reportContainer;
    private ProgressIndicator loadingIndicator;
    private Button scanButton;
    private TextField directoryPath;

    @Override
    public void start(Stage primaryStage) {
        // Load custom font
        Font.loadFont(getClass().getResourceAsStream("/fonts/Frutiger45.woff"), 14);

        // Main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f8f8;");

        // Header with title and leaf icon
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        ImageView leafIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/leaf.png")));
        leafIcon.setFitHeight(30);
        leafIcon.setFitWidth(30);
        
        Label title = new Label("ecoCode");
        title.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 28px; -fx-text-fill: #2e7d32;");
        
        Label subtitle = new Label("- Scan Clean. Code Green -");
        subtitle.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 16px; -fx-text-fill: #4caf50;");
        
        VBox titleBox = new VBox(5, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        
        header.getChildren().addAll(leafIcon, titleBox);

        // Directory selection
        HBox directoryBox = new HBox(10);
        directoryBox.setAlignment(Pos.CENTER_LEFT);
        
        directoryPath = new TextField();
        directoryPath.setPromptText("Select project directory...");
        directoryPath.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 14px; -fx-pref-width: 400px;");
        
        Button browseButton = new Button("Browse");
        browseButton.setStyle("-fx-font-family: 'Frutiger 45'; -fx-background-color: #4caf50; -fx-text-fill: white;");
        browseButton.setOnAction(e -> browseDirectory());
        
        directoryBox.getChildren().addAll(directoryPath, browseButton);

        // Scan button with loading indicator
        HBox scanBox = new HBox(10);
        scanBox.setAlignment(Pos.CENTER);
        
        scanButton = new Button("Scan Project");
        scanButton.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 16px; -fx-background-color: #2e7d32; -fx-text-fill: white;");
        scanButton.setOnAction(e -> startScan());
        
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setPrefSize(30, 30);
        
        scanBox.getChildren().addAll(scanButton, loadingIndicator);

        // Report container (initially hidden)
        reportContainer = new VBox(20);
        reportContainer.setVisible(false);
        reportContainer.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 5; -fx-border-color: #e0e0e0;");

        // Assemble main UI
        root.getChildren().addAll(header, directoryBox, scanBox, reportContainer);
        
        // Set up scene
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("ecoCode - Sustainable Code Analysis");
        primaryStage.show();
    }

    private void browseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Project Directory");
        File selectedDirectory = chooser.showDialog(null);
        if (selectedDirectory != null) {
            directoryPath.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void startScan() {
        // Show loading indicator
        scanButton.setDisable(true);
        loadingIndicator.setVisible(true);
        
        // Simulate scanning process
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate scan time
                
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    scanButton.setDisable(false);
                    showReport();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showReport() {
        reportContainer.getChildren().clear();
        reportContainer.setVisible(true);
        
        // Report title
        Label reportTitle = new Label("Scan Results");
        reportTitle.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 22px; -fx-text-fill: #2e7d32;");
        
        // Summary stats
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(10));
        
        addStat(statsGrid, 0, "Lines of Code", "15,842");
        addStat(statsGrid, 1, "Files Scanned", "247");
        addStat(statsGrid, 2, "Bugs Found", "32");
        addStat(statsGrid, 3, "Bugs Resolved", "12");
        addStat(statsGrid, 4, "Energy Savings", "4.2 kWh");
        addStat(statsGrid, 5, "CO₂ Reduction", "1.8 kg");
        
        // Pie chart
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Code Analysis");
        pieChart.getData().addAll(
            new PieChart.Data("Clean Code", 78),
            new PieChart.Data("Optimizable", 15),
            new PieChart.Data("Critical Issues", 7)
        );
        pieChart.setStyle("-fx-font-family: 'Frutiger 45';");
        pieChart.setLegendVisible(true);
        pieChart.setPrefSize(500, 300);
        
        // Recommendations
        Label recommendationsTitle = new Label("Recommendations for Greener Code");
        recommendationsTitle.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 18px; -fx-text-fill: #2e7d32;");
        
        TextArea recommendations = new TextArea();
        recommendations.setEditable(false);
        recommendations.setWrapText(true);
        recommendations.setText("1. Replace inefficient loops with stream operations\n" +
                              "2. Reduce database queries by 15%\n" +
                              "3. Cache frequently accessed resources\n" +
                              "4. Remove unused dependencies (3 found)\n" +
                              "5. Optimize image assets in /static folder");
        recommendations.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 14px;");
        
        // Add all components to report
        reportContainer.getChildren().addAll(
            reportTitle,
            statsGrid,
            pieChart,
            recommendationsTitle,
            recommendations
        );
    }

    private void addStat(GridPane grid, int row, String label, String value) {
        Label statLabel = new Label(label);
        statLabel.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 14px;");
        
        Label statValue = new Label(value);
        statValue.setStyle("-fx-font-family: 'Frutiger 45'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        grid.add(statLabel, 0, row);
        grid.add(statValue, 1, row);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



-- css--

.root {
    -fx-font-family: 'Frutiger 45';
    -fx-background-color: #f8f8f8;
}

.button {
    -fx-font-family: 'Frutiger 45';
    -fx-background-radius: 4px;
    -fx-padding: 8px 16px;
    -fx-cursor: hand;
}

.button:hover {
    -fx-opacity: 0.9;
}

.text-field {
    -fx-font-family: 'Frutiger 45';
    -fx-background-radius: 4px;
    -fx-border-radius: 4px;
    -fx-border-color: #e0e0e0;
    -fx-padding: 8px;
}

.chart-title {
    -fx-font-family: 'Frutiger 45';
    -fx-font-size: 16px;
    -fx-text-fill: #2e7d32;
}

.chart-legend-item {
    -fx-font-family: 'Frutiger 45';
    -fx-font-size: 14px;
}

.pie-chart-legend {
    -fx-font-family: 'Frutiger 45';
}

.data0.chart-pie { -fx-pie-color: #4caf50; }
.data1.chart-pie { -fx-pie-color: #ffc107; }
.data2.chart-pie { -fx-pie-color: #f44336; }
