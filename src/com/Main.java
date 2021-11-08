package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(final String[] args) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            final Analyzer analyzer = new Analyzer(3,12);
            analyzer.analyze(reader.readLine());
            analyzer.outOnScreen();
        }
    }
}
