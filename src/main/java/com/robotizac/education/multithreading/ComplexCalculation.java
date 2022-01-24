package com.robotizac.education.multithreading;

import java.math.BigInteger;

public class ComplexCalculation {

    public static void main(String[] args) throws InterruptedException {
        BigInteger result = calculateResult(BigInteger.TWO, BigInteger.TEN, BigInteger.TWO, BigInteger.TEN);
        System.out.println(result);
    }

    public static BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) throws InterruptedException {
        BigInteger result = BigInteger.ZERO;
        /*
            Calculate result = ( base1 ^ power1 ) + (base2 ^ power2).
            Where each calculation in (..) is calculated on a different thread
        */
        PowerCalculatingThread p1 = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread p2 = new PowerCalculatingThread(base2, power2);
        p1.start();
        p1.join();
        p2.start();
        p2.join();
        result = p1.getResult().add(p2.getResult());
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;

        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
           /*
           Implement the calculation of result = base ^ power
           */
            try {
                for (int i = 0; i < power.intValue(); i++) {
                    result = result.multiply(base);
                }
            } catch (Exception e) {
                result = BigInteger.ZERO;
            }
        }

        public BigInteger getResult() { return result; }
    }
}