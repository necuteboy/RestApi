package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RestApi {
    public String getFile(String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        try{
            FileReader reader = new FileReader(fileName);
            BufferedReader rd = new BufferedReader(reader);
            String line = rd.readLine();
            while(line != null){
                line = rd.readLine();
            }
            return line;
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
