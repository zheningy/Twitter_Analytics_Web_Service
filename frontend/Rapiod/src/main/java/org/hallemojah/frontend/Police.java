package org.hallemojah.frontend;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Police {

    private Map<String, String> bannedWords = new HashMap<>();

    Police(){
        InputStream input = getClass().getResourceAsStream("/bannedwords");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                StringBuilder builder = new StringBuilder(line.length());
                builder.append(line.charAt(0));
                for (int i = 0; i < line.length() - 2; ++i) {
                    builder.append('*');
                }
                builder.append(line.charAt(line.length() - 1));
                String censored = builder.toString();
                bannedWords.put(line, censored);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String purify(String word){
        return bannedWords.getOrDefault(word, word);
    }

    public static void main(String[] args){
        Police police = new Police();
        System.out.println(police.purify("fuck"));
        System.out.println(police.purify("wow"));
    }
}
