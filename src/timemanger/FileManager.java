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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javafx.scene.text.Text;

/**
 *
 * @author mauricio
 */
public class FileManager {
    String dataFile;
    Text feedback;
    String sep = File.separator;
    
    FileManager(String dir, Text feedback)
    {
        dataFile = dir + sep + "TimeManager.data.csv";
        this.feedback = feedback;
    }
    
    public void updateLocation(String dir)
    {
        dataFile = dir + sep + "TimeManager.data.csv";
    }
    
    public void saveToFile(String lines)
    {
        try {
            if(checkForFile())
            {
            Files.write(Paths.get(dataFile), 
                        lines.getBytes(), 
                        StandardOpenOption.APPEND);
            }
        }catch (IOException e) {
            feedback.setText(e.getMessage());
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }
    
    public boolean checkForFile()
    {
        File f = new File(dataFile);
        if(f.isFile())return true;
        else
        {   try { 
                f.createNewFile();
                return true;
            }
            catch(IOException e){ 
                feedback.setText(e.getMessage());
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace()); }
        }
        return false;
    }
}
