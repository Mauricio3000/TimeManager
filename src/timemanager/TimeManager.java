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

/*
Todo:
- View log file
- Persist Save Checkbox setting
- Reorder timers in UI
*/
package timemanager;

import java.io.File;
import java.util.ArrayList;
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
import javafx.scene.control.ScrollPane;
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
    Use multiple timers with custom notes.
    Upon reset or removal of a timer, a record is stored of the note and the duration.
    The user can choose in which directory to store the timer log file.
    The application state file will always be in the application run directory.
 
 * @author Mauricio Santos-Hoyos
 * @version 0.06
*/
public class TimeManager extends Application {
    String changeLog;
    ArrayList<TimerDisplay> timers;     // List of created timerDisplay objects
    String sep = File.separator;        // Path seperator
    Text feedback;                      // Text node used in UI to give user feedback
    MediaPlayer mediaPlayer;            // Used to play alarm sound
    
                                        // Manage state and log files
    FileManager fm = new FileManager(System.getProperty("user.dir"), feedback);
    Label saveFileLbl;                  // Label UI node that displays log file directory
    double version = 0.07;              // Current app version
    String verStr = "" + version;       // App version as string
    
    @Override
    public void start(Stage primaryStage) {
        // Second stage of a JavaFX application
        changeLog = "Change Log\n";
        changeLog += "v0.07:\n";
        changeLog += "\tUpdate: Improved timer based on system clock snapshots\n";
        changeLog += "\tUpdate: Persist Save checkbox state\n"; 
        changeLog += "v0.06:\n";
        changeLog += "\tBugfix: Alarm not sounding on timers set to more than an hour\n"; 
        changeLog += "v0.05:\n";
        changeLog += "\tAdd changelog to about popup window\n";       
        changeLog += "v0.04:\n";
        changeLog += "\tFix off by one logic when counting down from hours to minutes\n";
        changeLog += "v0.03:\n";
        changeLog += "\tMemory optimization in timer code\n";
        changeLog += "\tRecomposed UI\n";
        changeLog += "v0.02:\n";
        changeLog += "\tPersist app state after exiting\n";
        changeLog += "\tFixed with for most UI elements\n";
        changeLog += "v0.01:\n";
        changeLog += "\tInitial release\n";
        
        
        timers = new ArrayList<TimerDisplay>(); // Instantiate the timers list
        BorderPane root = new BorderPane();     // Root UI node
        root.getStyleClass().add("borderpane"); // Associate to styling label
        
        //--- Menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        
        //--- File menus - about, exit
        Menu aboutMenu = new Menu("About");
        MenuItem aboutMenuItem = new MenuItem("About");
        MenuItem exitMenuItem = new MenuItem("Exit");
        
        aboutMenu.getItems().addAll(aboutMenuItem, 
                                    new SeparatorMenuItem(), 
                                    exitMenuItem);
        menuBar.getMenus().addAll(aboutMenu);
        
        //--- File menu event handlers
        aboutMenuItem.setOnAction(actionEvent -> aboutPopup());
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        
        //--- Create UI nodes
        feedback = new Text();
        Label saveFileTitleLbl = new Label("Log file directory: ");
        saveFileLbl = new Label(System.getProperty("user.dir"));
        
        Button saveFileBtn = new Button("Browse");
        Button newTimerBtn = new Button("Add Timer");
        
        VBox topVbox = new VBox(5);
        HBox saveFileLayout = new HBox(10);
        HBox addTimerLayout = new HBox(10);
        VBox centerVbox = new VBox(5);
        VBox timersVbox = new VBox(5);
        HBox feedBackHbox = new HBox(5);
        
        topVbox.setPadding(new Insets(1));
        centerVbox.setPadding(new Insets(1));
        timersVbox.setPadding(new Insets(1));
        
        //--- Associate UI nodes to style tags
        saveFileLbl.setId("filepath");
        centerVbox.getStyleClass().add("vbox");
        feedBackHbox.getStyleClass().add("hbox");
        saveFileLayout.getStyleClass().add("hbox");
        addTimerLayout.getStyleClass().add("hbox");
        timersVbox.getStyleClass().add("vbox");
        
        //--- Format UI nodes
        Font feedbackFont = Font.font("Tahoma", FontWeight.BOLD, 13);
        saveFileLbl.setPadding(new Insets(4));
        feedback.setFont(feedbackFont);
        feedback.setFill(Color.BEIGE);
        
        //--- Setup the MediaPlayer
        String soundFile = "alarm_01.mp3";
        Media sound = new Media(new File(soundFile).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        MediaView mediaView = new MediaView(mediaPlayer);
        
        //--- Load saved state to saveFileLbl node
        ArrayList<String> appData = fm.getSaveDirState();
        for(int i=0; i<appData.size(); i++)
        {
            saveFileLbl.setText( appData.get(0) );
            fm.updateLocation(appData.get(0));
            break;
        }
        
        //--- Load saved timers into timersVbox
        appData = fm.getTimerState();
        for(int i=0; i<appData.size(); i++)
        {
            String[] data = appData.get(i).split(",");
            createTimer(timersVbox, data[1], data[2], data[3], data[0], data[4]);
        }
        
        //--- Compose UI
        saveFileLayout.getChildren().addAll(saveFileTitleLbl, 
                                            saveFileLbl, 
                                            saveFileBtn);
        
        addTimerLayout.getChildren().addAll(newTimerBtn);
        
        topVbox.getChildren().addAll(menuBar,
                            saveFileLayout,
                            addTimerLayout);
        
        centerVbox.getChildren().addAll(timersVbox,
                                        mediaView);
        feedBackHbox.getChildren().addAll(feedback);

        root.setTop(topVbox);
        root.setCenter(centerVbox);
        root.setBottom(feedBackHbox);
        
        //--- Create the scene
        Scene scene = new Scene(root, 1000, 400);
        scene.getStylesheets().add("timemanager/styleSheet.css");
        
        primaryStage.setTitle("Time Manager v" + verStr);
        primaryStage.setScene(scene);
        
        //--- Button event handlers
        newTimerBtn.setOnAction(event -> {
            createTimer(timersVbox,"00","00","00"," ", "true");
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
    Second to last stage of a JavaFX application's shutdown cycle.
    Application state preservation logic in here.
    */
    @Override
    public void stop()
    {   
        // Save all timers currently in UI to file
        fm.writeTimerState(timers);
        // Save current log directory, which  user may have changed, to file
        fm.writeSaveDirState(saveFileLbl);
    }
    
    /**
    Create a TimerDisplay object, add it to the parent node
    and add it to the timers list.
    @param parent VBox container to place the timer
    @param h Hours setting
    @param m Minutes setting
    @param s Seconds setting
    @param n Note text
    @param cb Check box selected
    */
    public void createTimer(VBox parent, String h, String m, String s, String n, String cb)
    {   
        TimerDisplay timerDisplay = 
            new TimerDisplay(fm, feedback, mediaPlayer, timers);
        timerDisplay.parent = parent;
        timerDisplay.hoursCB.setValue(h);
        timerDisplay.minCB.setValue(m);
        timerDisplay.secCB.setValue(s);
        timerDisplay.noteField.setText(n);
        
        if(cb.equals("true")) timerDisplay.cb1.setSelected(true);
        else timerDisplay.cb1.setSelected(false);
        
        parent.getChildren().add(timerDisplay.getVBox());
        
        timers.add(timerDisplay);
    }
    
    /**
    Called by MenuBar About
    Display copyright and licensing information
    */
    private void aboutPopup() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Time Manager");
        
        String header = "\t\tTime Manager v" + verStr;
        header += "\nCopyright (C) 2018 Mauricio Santos-Hoyos\n";
        header += "\t\tLicensed under GPLv3\n\n";
        header += "Alarm sound license:\ncreativecommons.org/licenses/by/3.0/legalcode";
        alert.setHeaderText(header);
        
        
        
        ScrollPane sp = new ScrollPane();
        Text text = new Text();
        text.setText(changeLog);
        sp.setContent(text);
        alert.getDialogPane().setExpandableContent(sp);

        alert.showAndWait();
    }
    
    /**
     *
     * @param args Command line arguments passed by caller
     */
    public static void main(String[] args) {
        launch(args);
    }
}
