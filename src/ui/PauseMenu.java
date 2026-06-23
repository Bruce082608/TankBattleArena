package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Overlay menu shown while gameplay is paused.
 */
public class PauseMenu extends StackPane {
    /**
     * Creates the pause menu overlay.
     *
     * @param resumeAction resumes gameplay
     * @param restartRoundAction restarts the current round
     * @param mainMenuAction returns to the main menu
     */
    public PauseMenu(Runnable resumeAction, Runnable restartRoundAction, Runnable mainMenuAction) {
        setStyle("-fx-background-color: rgba(8, 12, 15, 0.78);");
        Label title = new Label("Paused");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: 900; -fx-text-fill: #f2d16b;");

        Button resume = createButton("Resume");
        resume.setOnAction(event -> resumeAction.run());

        Button restart = createButton("Restart Round");
        restart.setOnAction(event -> restartRoundAction.run());

        Button mainMenu = createButton("Main Menu");
        mainMenu.setOnAction(event -> mainMenuAction.run());

        VBox menu = new VBox(16, title, resume, restart, mainMenu);
        menu.setAlignment(Pos.CENTER);
        getChildren().add(menu);
    }

    /**
     * Creates a consistently styled pause button.
     *
     * @param text button label
     * @return configured button
     */
    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setPrefHeight(46);
        button.setStyle("-fx-font-size: 17px; -fx-font-weight: 800; -fx-background-radius: 6;"
                + "-fx-background-color: #e7edf0; -fx-text-fill: #172026;");
        return button;
    }
}
