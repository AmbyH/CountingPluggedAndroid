package com.example.countingplugged;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
private TextInputLayout input;
private Button top5;
private Button findMostCommonWord;
private TextView textView;
private String mostCommonWord;
private int occurences;
static ArrayList<WordCount> wordList = new ArrayList<WordCount>();
private String commonWords;
private String in;

    public MainActivity() throws FileNotFoundException {
    }
    static class WordCount {
    String word;
    int count;
    WordCount(String a, int b) {
        word = a;
        count = b;
    }
    private String getWord() {return word;}
    private int getCount() {return count;}
    private void addCount() {count++;}
        public String toString () {return "\"" + word + "\"" + " with " + count + " occurences.";}

}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        top5 = (Button) findViewById(R.id.button);
        findMostCommonWord = (Button) findViewById(R.id.button2);
        input = (TextInputLayout) findViewById(R.id.textInputLayout);
        textView = (TextView) findViewById(R.id.textView);
        AssetManager assets = this.getAssets();
        try {
            InputStream inputStream = assets.open("commonWords.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            commonWords = stringBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        top5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in = String.valueOf(input.getEditText().getText());
                try {
                    String result = findTop5Occurences(in,1);
                    textView.setText(result);
                    System.out.println(result);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        findMostCommonWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                in = String.valueOf(input.getEditText().getText());
                try {
                    mostCommonWord = findTop5Occurences(in,0);
                    textView.setText("The most common word is " + mostCommonWord);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }
    private String findTop5Occurences(String filePath, int b) throws FileNotFoundException {
        ArrayList<String> words = cleanString(filePath);
        for (String a : words) {
            if (!(isInList(a))) {
                wordList.add(new WordCount(a, 1));
            }
            else {
                for (WordCount wordCount : wordList) {
                    if (wordCount.getWord().equals(a)) {
                        wordCount.addCount();
                    }
                }
            }
        }
        Collections.sort(wordList, new Comparator<WordCount>() {
            public int compare(WordCount wc1, WordCount wc2) {
                return Integer.compare(wc2.count, wc1.count);
            }
        });
        System.out.println(wordList.get(0).toString());
        System.out.println(wordList.get(1).toString());
        System.out.println(wordList.get(2).toString());
        System.out.println(wordList.get(3).toString());
        System.out.println(wordList.get(4).toString());
        if (b == 1) {
            return "The top five most common words in the text file " + in + " are" + "\n1. " + wordList.get(0).toString() + "\n2. " + wordList.get(1).toString() + "\n3. " + wordList.get(2).toString() + "\n4. " + wordList.get(3).toString() + "\n5. " + wordList.get(4).toString();
        }
        else {
            return wordList.get(0).toString();
        }
    }

    public static boolean isInList(String word) {
        for (WordCount wordCount : wordList) {
            if (wordCount.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }
    public ArrayList<String> cleanString(String input) {
        AssetManager assets = this.getAssets();
        String output = "";
        try {
            InputStream inputStream = assets.open(input);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            output = stringBuilder.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> out = new ArrayList<String>();
        String curWord = "";
        for (int i = 0; i < output.length(); i++) {
            if ((output.charAt(i) != ' ') && (output.charAt(i) != ',') && (output.charAt(i) != '!') && (output.charAt(i) != '-') && (output.charAt(i) != '.') && (output.charAt(i) != '?') && (output.charAt(i) != '“') && (output.charAt(i) != '”')) {
                curWord = curWord + output.charAt(i);
                System.out.println(curWord);
            }
            else {
                if (curWord.isEmpty() || commonWords.contains(curWord)) {
                }
                else {
                    out.add(curWord.toLowerCase());
                    curWord = "";
                }
            }
        }
        return out;
    }
    public static int countLinesInFile(String nameOfFile) throws FileNotFoundException {
        File file = new File(nameOfFile);
        Scanner scanner = new Scanner(file);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            lineCount++;
            scanner.nextLine();
        }
        return lineCount;
    }
    public static String[] readFileIntoArray(String nameOfFile) throws FileNotFoundException {
        int linesInFile = countLinesInFile(nameOfFile);
        String[] array = new String[linesInFile];
        int index = 0;
        File file = new File(nameOfFile);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            array[index++] = scanner.nextLine();
        }
        return array;
    }
}