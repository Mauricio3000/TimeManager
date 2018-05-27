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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class TM_Timer {
    /*
    TM_Timer uses a Timeline as a counter, creating a keyframe per second
    that tick down the UI combobox values.
    */
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
    
    private int timeToSeconds()
    {   /*
        Convert Hours, minutes and seconds passed in by user to just seconds.
        @return seconds Integer value of total seconds
        */
        
        int seconds = this.hour * 60 * 60; // Hours to seconds
        seconds += this.minute * 60; // Minutes to seconds
        seconds += this.seconds;
        
        return seconds;
    }
    
    private void setKeys(int seconds)
    {   /*
        Create a key per second to be used in the Timeline. It's call back
        will tickdown the comboboxs.
        Store the keys in the array list keys.
        @param seconds Integer value of total seconds
        @return None
        */
        for(int i=0; i<=seconds; i++)
        {
            keys.add(new KeyFrame( 
                    Duration.millis(1000*i), 
                    e -> countDown() ));
        }
    }
    
    public void start()
    {   /*
        Create the Timeline, add the keys and then play the timeline.
        @return None
        */
        
        setKeys(timeToSeconds());
        timeline = new Timeline();
        for(int i=0; i<keys.size();i++)
        {
            timeline.getKeyFrames().add(keys.get(i));
        }
        timeline.play();
    }
    
    private void countDown()
    {   /*
        Count down the comboboxes, then play alarm when all are zero.
        @return None
        */
        int cb_hours = Integer.parseInt((String)hoursCB.getValue());
        int cb_mins = Integer.parseInt((String)minCB.getValue());
        int cb_secs = Integer.parseInt((String)secCB.getValue());
        
        String s;
        if(cb_secs == 0)
        {
            if(cb_mins == 0)
            {
                if(cb_hours == 0)
                {
                    startBtn.setText("Done");
                    mediaPlayer.play();
                }
                else
                {
                    s = String.format("%02d", (cb_hours - 1));
                    hoursCB.setValue(s);
                    
                    s = String.format("%02d", (59));
                    minCB.setValue(s);
                }
            }
            else
            {
                s = String.format("%02d", (cb_mins - 1));
                minCB.setValue(s);
                
                s = String.format("%02d", (59));
                secCB.setValue(s);
            }
        }
        else
        {
            s = String.format("%02d", (cb_secs - 1));
            secCB.setValue(s);
        }
    }

    public void stop()
    {   /*
        Stop the timeline, set reference to null to 
        garbage collect the timeline object, as continue uses 
        a whole new TM_Timer.
        @return None
        */
        timeline.stop();
        timeline = null;
    }
}
