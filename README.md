import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EcoCodeApp extends Application {

    private TabPane tabPane;
    private Label blinkingCaption;
    private VBox headerBox;

    @Override
    public void start(Stage primaryStage) {
        // Main container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Header with title and leaf icon (centered)
        headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        HBox titleBox = new HBox(5);
        titleBox.setAlignment(Pos.CENTER);

        // Eco (red) Code (black) with leaf icon
        Label ecoLabel = new Label("Eco");
        ecoLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: #ff0000; -fx-font-weight: bold;");

        ImageView leafIcon = new ImageView(new Image(getClass().getResourceAsStream("/leaf.png")));
        leafIcon.setFitHeight(30);
        leafIcon.setFitWidth(30);

        Label codeLabel = new Label("Code");
        codeLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: #000000; -fx-font-weight: bold;");

        titleBox.getChildren().addAll(ecoLabel, leafIcon, codeLabel);

        // Blinking clickable caption
        blinkingCaption = new Label("Scan, Clean. Code Green.");
        blinkingCaption.setStyle("-fx-font-size: 16px; -fx-text-fill: #000000;");
        setupBlinkingAnimation();

        // Make caption clickable
        blinkingCaption.setOnMouseClicked(e -> showTabs());
        blinkingCaption.setStyle("-fx-cursor: hand;");

        headerBox.getChildren().addAll(titleBox, blinkingCaption);
        root.setTop(headerBox);

        // Tab pane (hidden initially)
        tabPane = new TabPane();
        tabPane.setVisible(false);
        
        // Scan Tab
        Tab scanTab = new Tab("Scans");
        scanTab.setContent(createScanTabContent());
        scanTab.setClosable(false);

        // Reports Tab
        Tab reportsTab = new Tab("Reports");
        reportsTab.setContent(new Label("Scanning done. Here are below reports..."));
        reportsTab.setClosable(false);

        // Config Tab
        Tab configTab = new Tab("Configurations");
        configTab.setContent(new Label("Upload the configurations"));
        configTab.setClosable(false);

        tabPane.getTabs().addAll(scanTab, reportsTab, configTab);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("EcoCode - Sustainable Development");
        primaryStage.show();
    }

    private void setupBlinkingAnimation() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), blinkingCaption);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    private void showTabs() {
        // Stop blinking when clicked
        blinkingCaption.getProperties().put("stopBlinking", true);
        
        // Move header up
        headerBox.setPadding(new Insets(10, 20, 5, 20));
        
        // Show tabs
        tabPane.setVisible(true);
        tabPane.getSelectionModel().select(0); // Select first tab
    }

    private VBox createScanTabContent() {
        VBox scanContent = new VBox(20);
        scanContent.setPadding(new Insets(20));
        scanContent.setAlignment(Pos.TOP_CENTER);

        // Directory selection
        HBox dirBox = new HBox(10);
        dirBox.setAlignment(Pos.CENTER);
        
        TextField dirField = new TextField();
        dirField.setPromptText("Select project directory...");
        dirField.setPrefWidth(400);
        
        Button browseBtn = new Button("Browse");
        browseBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        dirBox.getChildren().addAll(dirField, browseBtn);

        // Scan button
        Button scanBtn = new Button("Start Scanning");
        scanBtn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold;");
        scanBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(1); // Switch to Reports tab
            // Here you would add your actual scanning logic
            // Then update the reports tab with real data
        });

        scanContent.getChildren().addAll(dirBox, scanBtn);
        return scanContent;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


-- css--

/* Main Application Styles */
.root {
    -fx-background-color: #f5f5f5;
    -fx-font-family: 'Arial';
}

/* Header Styles */
.header-box {
    -fx-alignment: center;
    -fx-padding: 20px;
}

.title-box {
    -fx-spacing: 5px;
    -fx-alignment: center;
}

.eco-text {
    -fx-font-size: 36px;
    -fx-font-weight: bold;
    -fx-fill: #ff0000; /* Red */
}

.code-text {
    -fx-font-size: 36px;
    -fx-font-weight: bold;
    -fx-fill: #000000; /* Black */
}

/* Blinking Caption */
.caption {
    -fx-font-size: 16px;
    -fx-text-fill: #000000;
    -fx-cursor: hand;
}

/* Tab Pane */
.tab-pane {
    -fx-tab-min-width: 120px;
    -fx-tab-max-width: 120px;
    -fx-background-color: transparent;
}

.tab {
    -fx-background-color: #e0e0e0;
    -fx-background-radius: 5px 5px 0px 0px;
    -fx-padding: 8px 15px;
}

.tab:selected {
    -fx-background-color: #ffffff;
    -fx-font-weight: bold;
}

.tab-label {
    -fx-text-fill: #2E7D32; /* Dark green */
}

/* Scan Tab Content */
.scan-content {
    -fx-padding: 20px;
    -fx-spacing: 20px;
    -fx-alignment: top-center;
}

.directory-box {
    -fx-spacing: 10px;
    -fx-alignment: center;
}

.text-field {
    -fx-pref-width: 400px;
    -fx-padding: 8px;
    -fx-background-radius: 4px;
    -fx-border-radius: 4px;
    -fx-border-color: #bdbdbd;
}

/* Buttons */
.button {
    -fx-background-radius: 4px;
    -fx-padding: 8px 16px;
    -fx-cursor: hand;
    -fx-font-weight: bold;
}

.browse-button {
    -fx-background-color: #4CAF50; /* Light green */
    -fx-text-fill: white;
}

.scan-button {
    -fx-background-color: #2E7D32; /* Dark green */
    -fx-text-fill: white;
}

.button:hover {
    -fx-opacity: 0.9;
}

/* Reports Tab */
.reports-content {
    -fx-padding: 20px;
    -fx-spacing: 15px;
}

.reports-title {
    -fx-font-size: 18px;
    -fx-font-weight: bold;
    -fx-text-fill: #2E7D32;
}

/* Config Tab */
.config-content {
    -fx-padding: 20px;
    -fx-alignment: center;
}
