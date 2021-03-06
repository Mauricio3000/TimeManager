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
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
TM_Timer uses a Timeline as a counter, creating a keyframe per second
that tick down the UI combobox values.
*/
public class TM_Timer {
    
    MediaPlayer mediaPlayer;    // Created in TimeManager. Used to play the alarm sound
    
    Timeline timeline;
    ArrayList<KeyFrame> keys;
    
    // UI to update
    Button startBtn;
    ComboBox hoursCB;
    ComboBox minCB;
    ComboBox secCB;
    
    // Timer values
    int hour;
    int minute;
    int seconds;
    
    TM_Timer(MediaPlayer mediaPlayer, Button startBtn, ComboBox hoursCB, 
            ComboBox minCB, ComboBox secCB, String hour, String minute, 
            String seconds)
    {
        this.keys = new ArrayList();
        this.mediaPlayer = mediaPlayer; // Used to play the alarm sound
        this.startBtn = startBtn;       // Need so it can be set to "Done"
        this.secCB = secCB;             // Seconds combobox that will be ticked down
        this.hoursCB = hoursCB;         // Hours combobox that will be ticked down
        this.minCB = minCB;             // Minutes combobox that will be ticked down
        this.hour = Integer.parseInt(hour);         // Starting hour value
        this.minute = Integer.parseInt(minute);     // Starting minute value
        this.seconds = Integer.parseInt(seconds);   // Starting seconds value
    }
    
    /**
    Convert Hours, minutes and seconds passed in by user to just seconds.
    @return seconds Integer value of total seconds
    */
    private int timeToSeconds()
    {   
        int seconds = this.hour * 60 * 60; // Hours to seconds
        seconds += this.minute * 60; // Minutes to seconds
        seconds += this.seconds;
        
        return seconds;
    }
    
    /**
    Create a key per second to be used in the Timeline. It's callback
    will tickdown the comboboxs.
    Store the keys in the array list keys.
    @param seconds Integer value of total seconds
    */
    private void setKeys(int seconds)
    {   
        // Plus 10 padding eliminates flaw where not enough keys are made
        // For durations greater than 2 hours.
        for(int i=0; i<=(seconds+10); i++)
        {
            keys.add(new KeyFrame( 
                    Duration.millis(1000*i), 
                    e -> countDown() ));
        }
    }
    
    /**
    Create the Timeline, add the keys and then play the timeline.
    */
    public void start()
    {   
        setKeys(timeToSeconds());
        timeline = new Timeline();
        for(int i=0; i<keys.size();i++)
        {
            timeline.getKeyFrames().add(keys.get(i));
        }
        timeline.play();
    }
    
    /**
    Count down the comboboxes, then play alarm when all are zero.
    Called by each key frame
    */
    private void countDown()
    {   
        // Get current combobox values
        int cb_hours = Integer.parseInt((String)hoursCB.getValue());
        int cb_mins = Integer.parseInt((String)minCB.getValue());
        int cb_secs = Integer.parseInt((String)secCB.getValue());
        
        if(cb_secs == 0) // Seconds are zero
        {
            if(cb_mins == 0) // Minutes are zero
            {
                if(cb_hours == 0) // Hours are zero
                {   
                    // All comboboxes zero
                    startBtn.setText("Done");
                    // Ensure any extra keys don't continue after reseting
                    timeline.stop(); 
                    // Sound the alarm
                    mediaPlayer.play();
                }
                else // Hours not zero
                {
                    // Reduce hours by one
                    hoursCB.setValue(String.format("%02d", (cb_hours - 1)));
                    
                    // Set minutes to 60, since minutes were zero
                    // Use 60 instead of 59, as next check will instantly 
                    // reduce minutes by 1
                    minCB.setValue(String.format("%02d", (60)));
                }
            }
            else // Minutes are not zero
            {
                // Reduce minutes by one
                minCB.setValue(String.format("%02d", (cb_mins - 1)));
                
                // Set seconds to 59, since they were zero
                secCB.setValue(String.format("%02d", (59)));
            }
        }
        else // Seconds are not zero
        {
            // Reduce seconds by one
            secCB.setValue(String.format("%02d", (cb_secs - 1)));
        }
    }
    
    /**
    Stop the timeline, set reference to null to 
    garbage collect the timeline object, as continue uses 
    a whole new TM_Timer.
    */
    public void stop()
    {   
        timeline.stop();
        timeline = null;
        keys = null;
        System.gc();
    }
}
