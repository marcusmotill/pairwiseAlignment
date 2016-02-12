package com.company;

import javax.swing.*;

public class Main {

    static char[] topSeq = {'A', 'C', 'G', 'C', 'G', 'T'};
    static char[] botSeq = {'C', 'G', 'T', 'A', 'A'};
    static int match = 8;
    static int mismatch = -3;
    static int gap = -4;

    public static void main(String[] args) {
        AlignmentForm alignmentForm = new AlignmentForm();

        int[][] matrix = new int[botSeq.length + 1][topSeq.length + 1];
        String[][] direction = new String[botSeq.length][topSeq.length];

        //initMatrix(gap, matrix);

        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                //doWork(matrix, direction, i, j);

            }
        }

        //printMatrix(matrix);
        // printDirection(direction);
    }

    private static void doWork(int[][] matrix, String[][] direction, int i, int j) {
        int result = 0;
        int first = matrix[i - 1][j - 1] + getIsMatch(i - 1, j - 1);
        int second = matrix[i][j - 1] + gap;
        int third = matrix[i - 1][j] + gap;
        result = Math.max(first, Math.max(second, Math.max(third, 0)));
        matrix[i][j] = result;
        direction[i - 1][j - 1] = "";
        if (result == first) {
            direction[i - 1][j - 1] += "/TL";
        }
        if (result == second) {
            direction[i - 1][j - 1] += "/L";
        }
        if (result == third) {
            direction[i - 1][j - 1] += "/T";
        }
    }

    private static int getIsMatch(int i, int j) {
        if (botSeq[i] == topSeq[j]) {
            return match;
        } else {
            return mismatch;
        }
    }

    private static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%5d ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    private static void printDirection(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%5s ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    private static void initMatrix(int gap, int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][0] = 0;
        }

        for (int i = 0; i < matrix[0].length; i++) {
            matrix[0][i] = 0;
        }
    }
}
