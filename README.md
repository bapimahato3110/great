import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EcoCodeApp extends Application {

    private VBox darkRoot;
    private VBox lightContent;
    private boolean contentVisible = false;

    @Override
    public void start(Stage primaryStage) {
        // Dark theme container (initial view)
        darkRoot = new VBox(20);
        darkRoot.setAlignment(Pos.CENTER);
        darkRoot.setStyle("-fx-background-color: #121212;");
        darkRoot.setPadding(new Insets(40));

        // Title with clickable leaf icon
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        
        ImageView leafIcon = new ImageView(new Image(getClass().getResourceAsStream("/leaf.png")));
        leafIcon.setFitHeight(40);
        leafIcon.setFitWidth(40);
        leafIcon.setStyle("-fx-cursor: hand;");
        leafIcon.setOnMouseClicked(e -> toggleContent());
        
        Label title = new Label("ecoCode");
        title.setStyle("-fx-font-size: 36px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        
        header.getChildren().addAll(leafIcon, title);

        // Light theme content (hidden initially)
        lightContent = createLightContent();
        lightContent.setVisible(false);

        darkRoot.getChildren().addAll(header, lightContent);

        Scene scene = new Scene(darkRoot, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ecoCode - Sustainable Code Analysis");
        primaryStage.show();
    }

    private VBox createLightContent() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 30; -fx-border-radius: 10;");
        content.setMaxWidth(600);

        // Directory input
        HBox dirBox = new HBox(10);
        TextField dirField = new TextField();
        dirField.setPromptText("Project directory...");
        dirField.setStyle("-fx-pref-width: 400px;");
        
        Button browseBtn = new Button("Browse");
        browseBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        dirBox.getChildren().addAll(dirField, browseBtn);

        // Scan button
        Button scanBtn = new Button("SCAN FOR SUSTAINABILITY");
        scanBtn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold;");

        content.getChildren().addAll(
            new Label("Scan your project for eco-friendly improvements").setStyle("-fx-font-size: 16px;"),
            dirBox,
            scanBtn
        );

        return content;
    }

    private void toggleContent() {
        contentVisible = !contentVisible;
        lightContent.setVisible(contentVisible);
        
        if (contentVisible) {
            darkRoot.setPadding(new Insets(20, 40, 40, 40)); // Push up effect
        } else {
            darkRoot.setPadding(new Insets(40)); // Reset padding
        }
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
