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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class FileManager {
    String saveDirFileName      = "TimeManager.savedir.csv";        
    String timerStateFileName   = "TimeManager.timers.csv";      
    String timerLogsFileName    = "TimeManager.timer.logs.csv";
    String timerLogFile;        // Full path to read / write timer entries
    String timerStateFile;      // Full path to read / write timer state file
    String saveDirFile;         // Full path to read / write save dir state file
    Text feedback;              // Text node to output data to user in UI
    String sep = File.separator;
    
    /**
    Constructor
    @param dir Base directory to be used for saving files
    @param feedback Text node t display any feedback to user in UI
    */
    FileManager(String dir, Text feedback)
    {   
        timerLogFile = dir + sep + timerLogsFileName;
        timerStateFile = dir + sep + timerStateFileName;
        saveDirFile = dir + sep + saveDirFileName;
        this.feedback = feedback;
    }
    
    /**
    Used in catch clause to give user feedback
    @param e Exception passed in by caller
    */
    private void exceptionHandler(Exception e)
    {
        feedback.setText(e.getMessage());
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace())); 
    }
    
    /**
    Read state file data and retrun as ArrayList of file lines.
    @return ArrayList Timer state file entries as String lines.
    */
    public ArrayList<String> getTimerState()
    {   
        ArrayList<String> timerData = new ArrayList();
        
        File f = new File(timerStateFile);
        if(f.isFile())
        {
            try (Stream<String> lines = Files.lines(Paths.get(timerStateFile), Charset.defaultCharset())) {
                lines.forEachOrdered(line -> timerData.add(line));
            }
            catch(IOException e){ exceptionHandler(e); }
        }
        return timerData;
    }
    
    /**
    Read state file data and retrun as ArrayList of file lines.
    @return ArrayList Timer state file entries as String lines.
    */
    public ArrayList<String> getSaveDirState()
    {   
        ArrayList<String> appData = new ArrayList();
        
        File f = new File(saveDirFile);
        if(f.isFile())
        {
            try (Stream<String> lines = Files.lines(Paths.get(saveDirFile), Charset.defaultCharset())) {
                lines.forEachOrdered(line -> appData.add(line));
            }
            catch(IOException e){ exceptionHandler(e); }
        }
        return appData;
    }
    
    /**
    Write existing timers to timer state file.
    @param timers ArrayList used by TimeManager to store TimerDisplay objects
    */
    public void writeTimerState(ArrayList<TimerDisplay> timers)
    {
        String s = "";
        for(int i=0; i<timers.size(); i++)
        {   
            s += timers.get(i).noteField.getText() + ",";
            
            // Get initial data from objects if it has been stored
            if(timers.get(i).initial_hours != null)
            {
                s += timers.get(i).initial_hours + ",";
                s += timers.get(i).initial_minutes + ",";
                s += timers.get(i).initial_seconds + "\n";
            }
            else
            {   // Get data from comboboxes as intial data has not been stored
                String temp;
                temp = (String)timers.get(i).hoursCB.getValue();
                s += temp + ",";
                temp = (String)timers.get(i).minCB.getValue();
                s += temp + ",";
                temp = (String)timers.get(i).secCB.getValue();
                s += temp + "\n";
            }
        }
        // Write timer data
        writeFile(s, timerStateFile);
    }
    
    /**
    Write log directory displayed in UI to file to persist 
    it once app is closed.
    @param saveFileLbl Label node from UI displaying where log file will be written.
    */
    public void writeSaveDirState(Label saveFileLbl)
    {   
        writeFile(saveFileLbl.getText(), saveDirFile);
    }
    /**
    Called by TimeManager when user changes the log directory
    @param dir New directory selected by user
    */
    public void updateLocation(String dir)
    {   
        timerLogFile = dir + sep + timerLogsFileName;
    }
    
    /**
    Append lines to a file
    @param lines String of lines to write to file
    @param file String of full path and filename to write to
    @return boolean True if successfully appended, false otherwise
    */
    public boolean appendToFile(String lines, String file)
    {   
        try 
        {
            if(checkForFile(file))
            {
                Files.write(Paths.get(file), 
                        lines.getBytes(), 
                        StandardOpenOption.APPEND);
            }
        }
        catch (IOException e) 
        {
            exceptionHandler(e);
            return false;
        }
        return true;
    }
    
    /**
    Writes a new file, overwriting the file if it exists.
    @param lines String of lines to write to file
    @param file String of full path and filename to write to
    @return boolean True if successfully written, false otherwise
    */
    public boolean writeFile(String lines, String file)
    {   
        File f = new File(file);
        
        if(f.isFile())
        {
            f.delete();
            f = new File(file);
        }
        try 
        {
            f.createNewFile();
            Files.write(Paths.get(file), 
                    lines.getBytes(), 
                    StandardOpenOption.WRITE);
        }
        catch (IOException e) {
            exceptionHandler(e);
            return false;
        }
        return true;
    }
    
    /**
    Check if given file exists. If it dowsn't, create it.
    @param file String with full path and nameof file to check
    @return boolean True if it exists or it was successfully created, false if it doesn't
    */
    public boolean checkForFile(String file)
    {   
        File f = new File(file);
        if(f.isFile()) return true; 
        else
        {   
            try 
            {
                f.createNewFile();
                return true;
            }
            catch(IOException e)
            { 
                exceptionHandler(e); 
                return false;
            }
        }
    }
}
