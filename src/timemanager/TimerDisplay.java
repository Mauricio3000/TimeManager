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

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class TimerDisplay {
    /*
    UI for the actual timer, TM_Timer.
    Write entries into log file upon reset or removal.
    TimerEntry in log: time stamp, hours, minutes, seconds, custom note
    */
    ArrayList<TimerDisplay> timers;     // List of TimerDisplay objects created by TimeManager
    MediaPlayer mediaPlayer;            // MediaPlayer created in TimeManger
    FileManager fm;                     // Used to manage timer log file
    TM_Timer timer;                     // The actual timer
    Text feedback;                      // Used to display feedback in UI
    TextField noteField;                // Custom text
    ComboBox hoursCB;                   // Hours selector
    ComboBox minCB;                     // Minutes selector
    ComboBox secCB;                     // Seconds selector

    
    String initial_hours;   // If timer is started, store intial values here
    String initial_minutes; // If timer is started, store intial values here
    String initial_seconds; // If timer is started, store intial values here
    String current_hours;   // If timer is paused, store current values here
    String current_minutes; // If timer is paused, store current values here
    String current_seconds; // If timer is paused, store current values here
    
    Button startBtn;        // Start | Pause | Continue to timer
    Button resetBtn;        // Reset the timer
    Button removeBtn;       // Remove the timer from the UI and timers list
    CheckBox cb1;           // Save log entry checkbox
    
    VBox container;         // Conatiner for all nodes
    VBox parent;            // Parent container created by TimeManger
            
    TimerDisplay(FileManager fm, 
                Text feedback, 
                MediaPlayer mediaPlayer,
                ArrayList<TimerDisplay> timers)
    {   /*
        Constructor
        @param fm FileManager to manage timer log entries
        @param feedback Node used to display feedback in UI
        @param mediaPlayer MediaPlayer for alarm sound created in TimeManger
        @param timers List of TimerDisplay objects managed by timeManager
        */
        this.fm = fm;
        this.feedback = feedback;
        this.mediaPlayer = mediaPlayer;
        this.timers = timers;
        
        //--- Create combobox data structures
        ObservableList<String> hoursList = 
                FXCollections.observableArrayList();
        ObservableList<String> minuteList = 
                FXCollections.observableArrayList();
        ObservableList<String> secondsList = 
                FXCollections.observableArrayList();
        
        //--- Initialize combobox data structures
        for(int i=0; i<24; i++){ hoursList.add(String.format("%02d", i)); }
        for(int i=0; i<60; i++){ 
            minuteList.add(String.format("%02d", i)); 
            secondsList.add(String.format("%02d", i));
        }
        
        //--- Create UI nodes
        container = new VBox(5);  // Root container
        HBox hbox = new HBox(5);  // TimerDisplay comboboxes container 
        HBox hbBtn = new HBox(5); // Buttons container
        
        hoursCB = new ComboBox();
        Label sep1 = new Label(":");
        minCB = new ComboBox();
        Label sep2 = new Label(":");
        secCB = new ComboBox();
        
        noteField = new TextField();
        
        startBtn = new Button("Start");
        resetBtn = new Button("Reset");
        removeBtn = new Button("Remove");
        cb1 = new CheckBox("Save");
        
        //--- Tool tips
        startBtn.setTooltip(new Tooltip("Start/Pause/Continue this timer"));
        resetBtn.setTooltip(new Tooltip("Reset this timer to it's intial values"));
        removeBtn.setTooltip(new Tooltip("Remove this timer"));
        cb1.setTooltip(new Tooltip("Save log to file on 'Reset' or 'Remove'"));
        
        //--- Set UI intial state
        resetBtn.setDisable(true);
        cb1.setSelected(true);
        
        //--- Associate UI nodes to style tags
        hbox.getStyleClass().add("hbox");
        hbBtn.getStyleClass().add("hbox");
        container.getStyleClass().add("vbox");
        
        //--- Initialize UI data
        hoursCB.setItems(hoursList);
        minCB.setItems(minuteList);
        secCB.setItems(secondsList);
        
        hoursCB.setValue("00");
        minCB.setValue("00");
        secCB.setValue("00");
        
        //--- Compose UI nodes
        hbBtn.getChildren().addAll(startBtn, resetBtn, removeBtn);
        hbox.getChildren().addAll(noteField, hoursCB, sep1, minCB, 
                                    sep2, secCB, hbBtn, cb1);
        container.getChildren().addAll(hbox);
        
        //--- Event handlers
        startBtn.setOnAction(event -> {
            this.startBtnClicked();
        });
        
        resetBtn.setOnAction(event -> {
            this.resetClicked();
        });
        
        removeBtn.setOnAction(event -> {
            this.removeClicked();
        });
    }
    
    public VBox getVBox(){ return container; } 
    
    public void startBtnClicked()
    {   /*
        startButton handler.
        If button text on click is:
        - Start: Save initial timer data, disable UI, start the timer
        - Pause: Stop the timer, enable UI, save current timer data
        - Continue: Start timer from current state, disable UI
        - Done: Stop alarm sound, reset timer
        @return None
        */
        String text = startBtn.getText();
        
        switch(text)
        {
            case "Start":
                // Change text to pause
                startBtn.setText("Pause");
                
                // Store initial_hours / initial_minutes values
                initial_hours = (String)hoursCB.getValue();
                initial_minutes = (String)minCB.getValue();
                initial_seconds = (String)secCB.getValue();
                
                // Disable all other buttons and comboboxes
                hoursCB.setDisable(true);
                minCB.setDisable(true);
                secCB.setDisable(true);

                resetBtn.setDisable(true);
                removeBtn.setDisable(true);
                noteField.setDisable(true);

                // Start timer using initial_hours / minutes
                timer = new TM_Timer(
                                    mediaPlayer,
                                    startBtn,
                                    hoursCB, 
                                    minCB,
                                    secCB,
                                    initial_hours,
                                    initial_minutes,
                                    initial_seconds);
                timer.start();
                break;
                
            case "Pause":
                // Set text to restart
                startBtn.setText("Continue");
                
                // Stop timer
                timer.stop();
                
                // Enable other buttons
                resetBtn.setDisable(false);
                removeBtn.setDisable(false);
                noteField.setDisable(false);
                
                // Set current_hours / minutes
                current_hours = (String)hoursCB.getValue();
                current_minutes = (String)minCB.getValue();
                current_seconds = (String)secCB.getValue();
                
                break;
                
            case "Continue":
                // Set text to Pause
                startBtn.setText("Pause");
                
                // Disable all other buttons and comboboxes
                resetBtn.setDisable(true);
                removeBtn.setDisable(true);
                noteField.setDisable(true);
                
                // Start timer using current_hours / minutes
                timer = new TM_Timer(
                                    mediaPlayer,
                                    startBtn,
                                    hoursCB, 
                                    minCB,
                                    secCB,
                                    current_hours,
                                    current_minutes,
                                    current_seconds);
                timer.start();
                break;
                
            case "Done":
                resetClicked();
                mediaPlayer.stop();
        }
    }
    
    public void resetClicked()
    {   /*
        resetBtn handler.
        If "save" is checked, create entry in log file.
        Restore comboboxes to intial values and enable UI.
        @return None
        */
        // If save checked
        if(cb1.isSelected()) saveEntry();
        
        // Restore combobox to saved values
        hoursCB.setValue(initial_hours);
        minCB.setValue(initial_minutes);
        secCB.setValue(initial_seconds);
        
        // Set startBtn text to Start
        startBtn.setText("Start");
        resetBtn.setDisable(true);
        
        // Enable comboboxes
        noteField.setDisable(false);
        removeBtn.setDisable(false);
        hoursCB.setDisable(false);
        minCB.setDisable(false);
        secCB.setDisable(false);
    }
    
    public void removeClicked()
    {   /*
        removeBtn handler.
        If "save" is checked, create entry in log file.
        Remove the timer from the UI and the timers list.
        @return None
        */
        // If save checked
        if(cb1.isSelected()) saveEntry();
        
        // Remove container from parent
        parent.getChildren().remove(container);
        
        // Remove from timers ArrayList
        timers.remove(this);
    }
    
    private void saveEntry()
    {   /*
        saveBtn handler.
        If intial data is not null, meaning that the timer has been used,
        then create an entry in the log file and send feedback to UI.
        @return None
        */
        if(initial_hours != null)
        {
            int[] start = {Integer.parseInt(initial_hours),
                        Integer.parseInt(initial_minutes),
                        Integer.parseInt(initial_seconds) };

            int[] end = {Integer.parseInt((String)hoursCB.getValue()),
                        Integer.parseInt((String)minCB.getValue()),
                        Integer.parseInt((String)secCB.getValue()) };

            TimerEntry te = new TimerEntry(noteField.getText(), start, end);
            if( fm.appendToFile(te.formatLine(), fm.timerLogFile) )
            {
                feedback.setText("Last recorded entry:  " +  te.formatLine());
            }
        }
    }
}
