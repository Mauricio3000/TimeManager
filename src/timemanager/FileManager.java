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
import javafx.scene.text.Text;

public class FileManager {
    ArrayList<String> timerData;
    String dataFile;
    String timerFile;
    Text feedback;
    String sep = File.separator;
    
    FileManager(String dir, Text feedback)
    {
        timerData = new ArrayList();
        dataFile = dir + sep + "TimeManager.data.csv";
        timerFile = dir + sep + "TimeManager.timers.csv";
        this.feedback = feedback;
    }
    
    private void exceptionHandler(Exception e)
    {
        feedback.setText(e.getMessage());
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace())); 
    }
    
    public ArrayList<String> getTimers()
    {
        timerData.clear();
        
        File f = new File(timerFile);
        if(f.isFile())
        {
            try (Stream<String> lines = Files.lines(Paths.get(timerFile), Charset.defaultCharset())) {
                lines.forEachOrdered(line -> timerData.add(line));
            }
            catch(IOException e){ exceptionHandler(e); }
        }
        return timerData;
    }
    
    public void writeTimers(ArrayList<TimerDisplay> timers)
    {
        String s = "";
        for(int i=0; i<timers.size(); i++)
        {   
            String temp;
            s += timers.get(i).noteField.getText() + ",";
            
            if(timers.get(i).initial_hours != null)
            {
                s += timers.get(i).initial_hours + ",";
                s += timers.get(i).initial_minutes + ",";
                s += timers.get(i).initial_seconds + "\n";
            }
            else
            {
                temp = (String)timers.get(i).hoursCB.getValue();
                s += temp + ",";
                temp = (String)timers.get(i).minCB.getValue();
                s += temp + ",";
                temp = (String)timers.get(i).secCB.getValue();
                s += temp + "\n";
            }
        }
        writeFile(s, timerFile);
    }
    
    public void updateLocation(String dir)
    {
        dataFile = dir + sep + "TimeManager.data.csv";
        timerFile = dir + sep + "TimeManager.timers.csv";
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
