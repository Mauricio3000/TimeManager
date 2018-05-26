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
package timemanager;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
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
    FileManager fm = new FileManager(System.getProperty("user.dir"), feedback);
    Label saveFileLbl;
    
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("borderpane");
        
        //--- Menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root.setTop(menuBar);
        
        // File menu - new, save, exit
        Menu aboutMenu = new Menu("About");
        MenuItem aboutMenuItem = new MenuItem("About");
        MenuItem exitMenuItem = new MenuItem("Exit");
        
        aboutMenu.getItems().addAll(aboutMenuItem, 
                                    new SeparatorMenuItem(), 
                                    exitMenuItem);
        menuBar.getMenus().addAll(aboutMenu);
        
        aboutMenuItem.setOnAction(actionEvent -> aboutPopup());
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        
        //--- Create UI nodes
        Font feedbackFont = Font.font("Tahoma", FontWeight.BOLD, 13);
        
        feedback = new Text();
        Label saveFileTitleLbl = new Label("Save timer data file at: ");
        saveFileLbl = new Label(System.getProperty("user.dir"));
        saveFileLbl.setPadding(new Insets(4));
        saveFileLbl.setId("filepath");
        Button saveFileBtn = new Button("Browse");
        Button newTimerBtn = new Button("Add Timer");
        
        VBox rootVbox = new VBox(5);
        HBox feedBackHbox = new HBox(5);
        HBox saveFileLayout = new HBox(10);
        rootVbox.setPadding(new Insets(1));
        HBox addTimerLayout = new HBox(10);
        VBox timersVbox = new VBox(5);
        timersVbox.setPadding(new Insets(1));
        
        rootVbox.getStyleClass().add("vbox");
        feedBackHbox.getStyleClass().add("hbox");
        saveFileLayout.getStyleClass().add("hbox");
        addTimerLayout.getStyleClass().add("hbox");
        timersVbox.getStyleClass().add("vbox");
        
        //--- Format UI nodes
        saveFileLayout.getChildren().addAll(saveFileTitleLbl, 
                                            saveFileLbl, 
                                            saveFileBtn);
        addTimerLayout.getChildren().addAll(newTimerBtn);
        
        feedback.setFont(feedbackFont);
        feedback.setFill(Color.BEIGE);
        
        feedBackHbox.getChildren().addAll(feedback);
        
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
        Scene scene = new Scene(root, 900, 400);
        
        scene.getStylesheets().add("timemanager/styleSheet.css");
        
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
            try{
                saveFileLbl.setText(selectedDirectory.getAbsolutePath());
                fm.updateLocation(selectedDirectory.getAbsolutePath());
            }
            catch(NullPointerException e){
                feedback.setText("Change directory canceled.");
            }
        });
        
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void aboutPopup() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Time Manager");
        alert.setHeaderText(null);
        
        String s = "\t\tTime Manager v0.1 \n";
        s += "Copyright (C) 2018 Mauricio Santos-Hoyos\n";
        s += "\t\tLicensed under GPLv3\n\n";
        s += "Alarm sound license:\ncreativecommons.org/licenses/by/3.0/legalcode";
        alert.setContentText(s);

        alert.showAndWait();
    }
    
}
