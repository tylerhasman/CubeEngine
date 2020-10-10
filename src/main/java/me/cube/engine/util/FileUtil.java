package me.cube.engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

    public static String readFileAsString(File file) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            StringBuilder out = new StringBuilder();

            String line;

            while((line = reader.readLine()) != null){
                out.append(line);
                out.append('\n');
            }

            return out.toString();
        }


    }

}
