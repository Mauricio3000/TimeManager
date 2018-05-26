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
    String saveDirFileName = "TimeManager.savedir.csv"; 
    String timerStateFileName = "TimeManager.timers.csv"; 
    String timerLogsFileName = "TimeManager.timer.logs.csv";
    String timerLogFile; 
    String timerStateFile;
    String saveDirFile;
    Text feedback;
    String sep = File.separator;
    
    FileManager(String dir, Text feedback)
    {
        
        timerLogFile = dir + sep + timerLogsFileName;
        timerStateFile = dir + sep + timerStateFileName;
        saveDirFile = dir + sep + saveDirFileName;
        this.feedback = feedback;
    }
    
    private void exceptionHandler(Exception e)
    {
        feedback.setText(e.getMessage());
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace())); 
    }
    
    public ArrayList<String> getTimerState()
    {   /*
        Read state file data and retrun as ArrayList<String> of file lines.
        */
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
    
    public ArrayList<String> getSaveDirState()
    {   /*
        Read state file data and retrun as ArrayList<String> of file lines.
        */
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
    
    public void writeTimerState(ArrayList<TimerDisplay> timers)
    {
        /*
        Write exisiting timers and save file location to file.
        */
        String s = "";
        for(int i=0; i<timers.size(); i++)
        {   
            s += timers.get(i).noteField.getText() + ",";
            
            if(timers.get(i).initial_hours != null)
            {
                s += timers.get(i).initial_hours + ",";
                s += timers.get(i).initial_minutes + ",";
                s += timers.get(i).initial_seconds + "\n";
            }
            else
            {
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
    
    public void writeSaveDirState(Label saveFileLbl)
    {
        // Write dave dir
        writeFile(saveFileLbl.getText(), saveDirFile);
    }
    
    public void updateLocation(String dir)
    {
        timerLogFile = dir + sep + timerLogsFileName;
    }
    
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
    
    public boolean checkForFile(String file)
    {
        File f = new File(file);
        if(f.isFile()) return true; 
        else
        {   
            try 
            {
                f.createNewFile();
            }
            catch(IOException e)
            { 
                exceptionHandler(e); 
            }
            return true;
        }
    }
}
