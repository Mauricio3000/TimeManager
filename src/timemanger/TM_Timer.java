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

import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class TM_Timer {
    MediaPlayer mediaPlayer;
    
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
        this.mediaPlayer = mediaPlayer;
        this.startBtn = startBtn;
        this.secCB = secCB;
        this.hoursCB = hoursCB;
        this.minCB = minCB;
        this.hour = Integer.parseInt(hour);
        this.minute = Integer.parseInt(minute);
        this.seconds = Integer.parseInt(seconds);
        

    }
    
    private int timeToSeconds()
    {
        int seconds = this.hour * 60 * 60; // Hours to seconds
        seconds += this.minute * 60; // Minutes to seconds
        seconds += this.seconds;
        
        return seconds;
    }
    
    private void setKeys(int seconds)
    {
        for(int i=0; i<=seconds; i++)
        {
            keys.add(new KeyFrame( 
                    Duration.millis(1000*i), 
                    e -> countDown() ));
        }
    }
    
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
    
    private void countDown()
    {
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
    {
        timeline.stop();
    }
}
