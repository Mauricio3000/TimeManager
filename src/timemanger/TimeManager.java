/* 
 * Copyright (C) 2018 Mauricio Santos-Hoyos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package timemanger;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author mauricio
 */
public class TimeManager extends Application {
    String sep = File.separator;
    Text feedback;
    MediaPlayer mediaPlayer;
    FileManager fm = new FileManager(System.getProperty("user.home"),
                                    feedback);
    Label saveFileLbl;
    
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        
        //--- Menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);
        
        
        
        //--- Create UI nodes
        Font uiFont = Font.font("Tahoma", FontWeight.MEDIUM, 14);
        Font btnFont = Font.font("Tahoma", FontWeight.MEDIUM, 13);
        Font feedbackFont = Font.font("Tahoma", FontWeight.MEDIUM, 12);
        
        feedback = new Text();
        Label saveFileTitleLbl = new Label("Save Timer Entry File at: ");
        saveFileLbl = new Label(System.getProperty("user.dir"));
        Button saveFileBtn = new Button("Change Directory");
        Button newTimerBtn = new Button("Create Timer");
        
        VBox rootVbox = new VBox(5);
        HBox feedBackHbox = new HBox(5);
        HBox saveFileLayout = new HBox(10);
        rootVbox.setPadding(new Insets(1));
        HBox addTimerLayout = new HBox(10);
        VBox timersVbox = new VBox(5);
        timersVbox.setPadding(new Insets(1));
        
        //--- Format UI nodes
        saveFileBtn.setFont(btnFont);
        newTimerBtn.setFont(btnFont);
        
        saveFileTitleLbl.setFont(uiFont);
        saveFileLbl.setFont(uiFont);
        
        saveFileLayout.setStyle("-fx-padding: 10;");
        saveFileLayout.setAlignment(Pos.CENTER);
        saveFileLayout.getChildren().addAll(saveFileTitleLbl, 
                                            saveFileLbl, 
                                            saveFileBtn);
        addTimerLayout.setStyle("-fx-padding: 10;");
        addTimerLayout.setAlignment(Pos.CENTER);
        addTimerLayout.getChildren().addAll(newTimerBtn);
        
        feedback.setFont(feedbackFont);
        feedback.setFill(Color.FIREBRICK);
        
        feedBackHbox.getChildren().addAll(feedback);
        feedBackHbox.setAlignment(Pos.CENTER);
        feedBackHbox.setStyle("-fx-padding: 10;");
        
        //--- Setup the MediaPlayer
        String soundFile = "alarm_01.mp3";
        Media sound = new Media(new File(soundFile).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        MediaView mediaView = new MediaView(mediaPlayer);
        
        //--- Load UI into rootVbox
        rootVbox.getChildren().addAll(saveFileLayout, 
                                    addTimerLayout, 
                                    timersVbox,
                                    feedBackHbox,
                                    mediaView);

        root.setCenter(rootVbox);
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setTitle("Time Manager v0.1");
        primaryStage.setScene(scene);
        
        //--- Button event handlers
        newTimerBtn.setOnAction(event -> {
                TimerDisplay timerDisplay = 
                        new TimerDisplay(fm, feedback, mediaPlayer);
                timerDisplay.parent = timersVbox;
                timersVbox.getChildren().add(timerDisplay.getVBox());
        });
        
        saveFileBtn.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose directory");
            File defaultDirectory = new File(System.getProperty("user.home"));
            chooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = chooser.showDialog(primaryStage);
            saveFileLbl.setText(selectedDirectory.getAbsolutePath());
            fm.updateLocation(selectedDirectory.getAbsolutePath());
        });
        
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
