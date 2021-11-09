package com;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final boolean isTest = false;  //Переключение тестирования приложения

    private static final String[] testInputStrings = {
            //Правильные цепочки
            "WHILE (MY != 0 || MY != -1) { MY = D - 0.01; D = D * 0.3; }",
            "WHILE (B[10] < 90 && C >= -9.3E-3) { A = A + 1; C = C * 3.2 - 0.01; }",
            "WHILE (X[I] == 10) { I = I + 1; }",

            //Цепочка с идентификатором, длина которого превышает 12 символов
            "while (i < 1000) {i = i + 1; sumOfSomeVariable += i;}",

            //Цепочки с идентификатором, который является зарезервированным словом
            "while( pszEOS >= szSource && pszEOS == 50 ) {pszEOS = case - 3;}",
            "while( pszEOS >= for && pszEOS == 50 ) {pszEOS = case - 3;}",
            "while( switch >= for && pszEOS == 50 ) {pszEOS = case - 3;}",

            //Цепочки с целочисленной константой, выходящая за диапазон -32768 – 32767
            "while (n > 0) { a = 0.03; value = n * 40000; n = n - 2; }",
            "while (n < 0) { a = 0.03; value = n * 40; n = -50000; }",
            "while (n == 0) { a = 0.0312332156823; value = n * 40; n = -50000; }",
            "while (n != 0) { a = 500000.03; value = n * 40; n = -50000; }",

            //Цепочки с числом simple-expr в expr большем 4
            "while ( i < 20 && i != 12 && i != 0 && m > 10 && m < 30){ i++; }",
            "while ( i < 20 || i != 120 && i != 0 || m > 2 && m < 37){ i++; }",
            "while ( i > 27 || i != 12 || i != 0 || m > 10 || m < 60){ i++; }"
    };

    public static void main(final String[] args) throws IOException, InterruptedException {
        if (isTest) {
            AnalyzeTask[] taskArray = new AnalyzeTask[testInputStrings.length];
            for (int i = 0; i < taskArray.length; i++) {
                taskArray[i] = new AnalyzeTask(testInputStrings[i]);
            }
            for (AnalyzeTask task : taskArray) {task.getThread().join();}
            for (AnalyzeTask task : taskArray) {
                task.getAnalyzer().outOnScreen();
            }
        } else {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
                Analyzer analyzer = new Analyzer(3, 12);
                analyzer.analyze(reader.readLine());
                analyzer.outOnScreen();
            }
        }
    }

    private static class AnalyzeTask implements Runnable {
        private static int threadCount = 0;
        private final String str;
        private final int maxLogicOperators = 3;
        private final int maxIdLength = 12;
        private final Analyzer analyzer = new Analyzer(maxLogicOperators, maxIdLength);
        private final Thread thread;

        public AnalyzeTask(String str) {
            this.str = str;

            thread = new Thread(this);
            thread.setName("Thread - " + threadCount);
            threadCount++;
            thread.start();
        }
        public Thread getThread() {
            return thread;
        }
        public Analyzer getAnalyzer() {
            return analyzer;
        }

        @Override
        public void run() {
            analyzer.analyze(str);
        }
    }
}