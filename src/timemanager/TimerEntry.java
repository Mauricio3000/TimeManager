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

import java.util.Date;

/**
Create a String to write to timer log file.
*/
public class TimerEntry {
    int[] start;
    int[] end;
    String note;
    Date now;
    String line;
    
    /**
    Constructor.
    @param note User note from TimerDisplay
    @param start Timer initial [hours, minutes, seconds]
    @param end Timer comboboxes [hours, minutes, seconds]
    */
    TimerEntry(String note, int[] start, int[] end)
    {   
        this.note = note;
        this.start = start;
        this.end = end;
        this.now = new Date();
        
        formatLine();
    }
    
    /**
    Return a formated string to be used as timer log entry
    @return String Format: timestamp, hh:mm:ss, note
    */
    public String formatLine() 
    {
        return now.toString() + ", " + duration() + ", " + note + "\n"; 
    }
    
    /**
    Take give int array [hh,mm,ss] representing a time and
    return it as total seconds
    @param t int array [hh,mm,ss] representing a time
    @return int Total seconds
    */
    private int timeToSeconds(int[] t) 
    {   
        int hours = t[0];
        int mins = t[1];
        int secs = t[2];
        
        return (hours*60*60)+(mins*60)+secs;
    }
    
    /**
    @return String that represents the duration [hh,mm,ss] - [hh,mm,ss]
    */
    private String duration()
    {   
        int a = timeToSeconds(start);
        int b = timeToSeconds(end);
        
        int totalSecs = (a-b);
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
}
