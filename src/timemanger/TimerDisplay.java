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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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

/**
 *
 * @author mauricio
 */
public class TimerDisplay {
    
    MediaPlayer mediaPlayer;
    FileManager fm;
    TM_Timer timer;
    Text feedback;
    TextField noteField;
    ComboBox hoursCB;
    ComboBox minCB;
    ComboBox secCB;
    CheckBox cb1;
    
    String initial_hours;
    String initial_minutes;
    String initial_seconds;
    String current_hours;
    String current_minutes;
    String current_seconds;
    
    Button startBtn;
    Button resetBtn;
    Button removeBtn;
    
    VBox container;
    VBox parent;
            
    TimerDisplay(FileManager fm, Text feedback, MediaPlayer mediaPlayer)
    {
        this.fm = fm;
        this.feedback = feedback;
        this.mediaPlayer = mediaPlayer;
        
        //--- Create Data structures
        ObservableList<String> hoursList = 
                FXCollections.observableArrayList();
        ObservableList<String> minuteList = 
                FXCollections.observableArrayList();
        ObservableList<String> secondsList = 
                FXCollections.observableArrayList();
        
        //--- Initialize data structures
        for(int i=0; i<24; i++){ hoursList.add(String.format("%02d", i)); }
        for(int i=0; i<60; i++){ 
            minuteList.add(String.format("%02d", i)); 
            secondsList.add(String.format("%02d", i));
        }
        
        //--- Create UI nodes
        container = new VBox(5);       // Root container
        HBox hbox = new HBox(5);  // TimerDisplay comboboxes container 
        HBox hbBtn = new HBox(5); // Buttons container
        
        
        hoursCB = new ComboBox();
        Label sep1 = new Label(":");
        minCB = new ComboBox();
        Label sep2 = new Label(":");
        secCB = new ComboBox();
        
        Label noteLbl = new Label("Memo:");
        noteField = new TextField();
        
        startBtn = new Button("Start");
        resetBtn = new Button("Reset");
        resetBtn.setDisable(true);
        removeBtn = new Button("Remove");
        
        cb1 = new CheckBox("Save");
        cb1.setTooltip(new Tooltip("Save log to file on 'Reset' or 'Remove'"));
        cb1.setSelected(true);
        
        //--- Format UI nodes
        Font uiFont = Font.font("Tahoma", FontWeight.MEDIUM, 14);
        Font btnFont = Font.font("Tahoma", FontWeight.MEDIUM, 13);
        
        noteLbl.setFont(uiFont);
        startBtn.setFont(btnFont);
        resetBtn.setFont(btnFont);
        removeBtn.setFont(btnFont);
        cb1.setFont(btnFont);
        
        hbox.setPadding(new Insets(5));
        hbox.setAlignment(Pos.CENTER);
        hbBtn.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(1));
        container.setAlignment(Pos.CENTER);
        
        hoursCB.setItems(hoursList);
        minCB.setItems(minuteList);
        secCB.setItems(secondsList);
        
        hoursCB.setValue("00");
        minCB.setValue("00");
        secCB.setValue("00");
        
        hoursCB.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
        minCB.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
        secCB.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
        
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        
        //--- Compose UI nodes
        hbBtn.getChildren().addAll(startBtn, resetBtn, removeBtn);
        hbox.getChildren().addAll(noteLbl,noteField,hoursCB,
                                    sep1,minCB,sep2,secCB,hbBtn, cb1);
        container.getChildren().addAll(hbox);
        
        //--- Event handlers
        startBtn.setOnAction(event -> {
            this.startClicked();
        });
        
        resetBtn.setOnAction(event -> {
            this.resetClicked();
        });
        
        removeBtn.setOnAction(event -> {
            this.removeClicked();
        });
    }
    
    public VBox getVBox(){ return container; } 
    
    public void startClicked()
    { // Start button handler
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
                
                // Keep comboboxs non opaque
                hoursCB.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
                minCB.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
                secCB.setStyle("-fx-opacity: 1; -fx-text-fill: black;-fx-background-color: white");
                
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
    {
        // If save checked
        if(cb1.isSelected()) saveEntry();
        
        // Restore combobox to saved values
        hoursCB.setValue(initial_hours);
        minCB.setValue(initial_minutes);
        secCB.setValue(initial_seconds);
        
        // Set startBtn text to Start
        startBtn.setText("Start");
        
        // Enable comboboxes
        noteField.setDisable(false);
        hoursCB.setDisable(false);
        minCB.setDisable(false);
        secCB.setDisable(false);
    }
    
    public void removeClicked()
    {
        // If save checked
        if(cb1.isSelected()) saveEntry();
        
        // Remove container from parent
        parent.getChildren().remove(container);
    }
    
    private void saveEntry()
    {
        int[] start = {Integer.parseInt(initial_hours),
                    Integer.parseInt(initial_minutes),
                    Integer.parseInt(initial_seconds) };
            
        int[] end = {Integer.parseInt((String)hoursCB.getValue()),
                    Integer.parseInt((String)minCB.getValue()),
                    Integer.parseInt((String)secCB.getValue()) };
            
        TimerEntry te = new TimerEntry(noteField.getText(), start, end);
        fm.saveToFile(te.formatLine());
        feedback.setText("Last recorded entry:  " +  te.formatLine());
    }
}
