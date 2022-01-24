package com.robotizac.education.multithreading.multiexecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    int read = System.in.read();
                    if ('q' == read) {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public static class MultiExecutor {

        // Add any necessary member variables here
        private final List<Thread> threads = new ArrayList<>();

        /*
         * @param tasks to executed concurrently
         */
        public MultiExecutor(List<Runnable> tasks) {
            // Complete your code here
            for (Runnable task : tasks) {
                threads.add(new Thread(task));
            }
        }

        /**
         * Starts and executes all the tasks concurrently
         */
        public void executeAll() {
            // complete your code here
            for (Thread thread : threads) {
                thread.start();
            }
        }
    }
}
