package com.robotizac.education.multithreading.hackerpolice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final int MAX_PASS = 9999;

    public static void main(String[] args) {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASS));
        AscendingHacker ascendingHacker = new AscendingHacker(vault);
        DescendingHacker descendingHacker = new DescendingHacker(vault);
        Police police = new Police();
        List<Thread> threads = List.of(ascendingHacker, descendingHacker, police);

        for (Thread thread : threads) {
            thread.start();
        }

    }

    private static class Vault {
        private int pass;

        private Vault(int pass) {
            this.pass = pass;
        }

        public boolean isPassCorrect(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
            return pass == guess;
        }
    }

    private static class Hacker extends Thread {
        protected Vault vault;

        private Hacker(Vault vault) {
            this.vault = vault;
            this.setName(getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public synchronized void start() {
            System.out.println("Starting hacker thread " + getName());
            super.start();
        }
    }

    private static class AscendingHacker extends Hacker {

        private AscendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = 0; i < MAX_PASS; i++) {
                if (vault.isPassCorrect(i)) {
                    System.out.println(getName() + " guessed the password " + i);
                    System.exit(0);
                }
            }
        }
    }

    private static class DescendingHacker extends Hacker {

        private DescendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = MAX_PASS; i > 0; i--) {
                if (vault.isPassCorrect(i)) {
                    System.out.println(getName() + " guessed the password " + i);
                    System.exit(0);
                }
            }
        }
    }

    private static class Police extends Thread {
        @Override
        public void run() {
            for (int i = 10; i > 0; i--) {
                try {
                    System.out.println(i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Game over for you dirty hackers!");
            System.exit(0);
        }
    }
}
