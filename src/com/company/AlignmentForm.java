package com.company;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by marcusmotill on 2/10/16.
 */
public class AlignmentForm extends JFrame {
    private JTextField tfGapPen;
    private JTextField tfMatchPen;
    private JTextField tfMisPen;
    private JPanel rootPanel;
    private JButton bSubmit;
    private JRadioButton rbDNA;
    private JRadioButton rbAmino;
    private JTextArea taSequence1;
    private JTextArea taSequence2;
    private JTextPane tpResults;
    private JLabel lScore;
    private JRadioButton rbLocal;
    private JRadioButton rbGlobal;
    ButtonGroup geneGroup = new ButtonGroup();
    ButtonGroup alignGroup = new ButtonGroup();

    int matchPen = 5;
    int missPen = -3;
    int gapPen = -4;
    char[] topSeq;
    char[] botSeq;
    MatrixItem[][] matrix;
    List<Character> acidNameList;
    List<List<Integer>> acidScoreList;

    public AlignmentForm() {

        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        geneGroup.add(rbAmino);
        geneGroup.add(rbDNA);
        rbDNA.setSelected(true);

        alignGroup.add(rbLocal);
        alignGroup.add(rbGlobal);
        rbLocal.setSelected(true);

        tfGapPen.setText(String.valueOf(gapPen));
        tfMisPen.setText(String.valueOf(missPen));
        tfMatchPen.setText(String.valueOf(matchPen));
        bSubmit.addActionListener(e -> runAlignment());
        tpResults.setVisible(false);
        setVisible(true);
        rbAmino.addActionListener(e -> getScoringMatrixFile());
    }

    private void getScoringMatrixFile() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(AlignmentForm.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            saveScoringMatrix(file);
            System.out.print("Opening: " + file.getName() + "." + "\n");
        } else {
            System.out.print("Open command cancelled by user." + "\n");
        }
    }

    private void saveScoringMatrix(File file) {
        acidNameList = new ArrayList<>();
        acidScoreList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                String[] lineArray = line.split(" +");
                acidNameList.add(lineArray[0].charAt(0));
                List<Integer> scoreList = new ArrayList<>();
                for (int i = 1; i < lineArray.length; i++) {
                    scoreList.add(Integer.valueOf(lineArray[i]));
                }
                acidScoreList.add(scoreList);
                line = br.readLine();
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void runAlignment() {
        getInputVars();
        matrix = new MatrixItem[botSeq.length + 1][topSeq.length + 1];

        initMatrix();

        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[0].length; j++) {
                doWork(i, j);
            }
        }

        findMaxValue();
    }

    private void findMaxValue() {
        List<Character> topList = new ArrayList<>();
        List<Character> bottomList = new ArrayList<>();
        int iCord = 0;
        int jCord = 0;
        int max = 0;
        for (int i = 0; i < matrix.length - 1; i++) {
            for (int j = 0; j < matrix[0].length - 1; j++) {
                if (matrix[i][j].getScore() > max) {
                    max = matrix[i][j].getScore();
                    iCord = i;
                    jCord = j;
                }
            }
        }
        if (rbDNA.isSelected())
            lScore.setText(String.valueOf(max));

        while (max != 0) {
            System.out.print(topSeq[jCord]);
            System.out.print("\t");
            System.out.print(botSeq[iCord]);
            System.out.println();

            topList.add(topSeq[jCord]);
            bottomList.add(botSeq[iCord]);

            Direction nextDirection = matrix[iCord][jCord].getDirection();
            if (nextDirection == Direction.Top) {
                iCord--;
            } else if (nextDirection == Direction.TopLeft) {
                jCord--;
                iCord--;
            } else if (nextDirection == Direction.Left) {
                jCord--;
            }
            max = matrix[iCord][jCord].getScore();
        }

        int count = 0;

        while (count < topList.size()) {
            for (int i = 1; i <= 10; i++) {
                char nextItem = 0;
                try {
                    nextItem = topList.get(count + i - 1);
                } catch (IndexOutOfBoundsException ignored) {

                }
                if (nextItem != 0) {
                    tpResults.setText(tpResults.getText() + nextItem);
                }

            }
            tpResults.setText(tpResults.getText() + "\n");

            for (int i = 1; i <= 10; i++) {
                char nextItem = 0;
                try {
                    nextItem = bottomList.get(count + i - 1);
                } catch (IndexOutOfBoundsException ignored) {

                }
                if (nextItem != 0) {
                    tpResults.setText(tpResults.getText() + nextItem);
                }

            }

            tpResults.setText(tpResults.getText() + "\n\n");
            count += 10;
        }

        if (rbAmino.isSelected()) {
            getAminoScore(topList, bottomList);
        }

    }

    private void getAminoScore(List<Character> topList, List<Character> bottomList) {

        int aminoScore = 0;

        for (int i = 0; i < topList.size(); i++) {
            int acidIndex = acidNameList.indexOf(topList.get(i));
            int matchAcidIndex = acidNameList.indexOf(bottomList.get(i));

            if (acidIndex == -1) {
                acidIndex = acidNameList.get(acidNameList.size() - 1);
            }
            if (matchAcidIndex == -1) {
                matchAcidIndex = acidNameList.get(acidNameList.size() - 1);
            }
            aminoScore += acidScoreList.get(acidIndex).get(matchAcidIndex);
        }

        lScore.setText(String.valueOf(aminoScore));
    }

    private void getInputVars() {
        String sequenceInput1 = taSequence1.getText();
        String sequenceInput2 = taSequence2.getText();

        String regexPattern = ">.*";

        sequenceInput1 = sequenceInput1.replaceAll(regexPattern, "");
        sequenceInput2 = sequenceInput2.replaceAll(regexPattern, "");

        System.out.println(sequenceInput1);
        System.out.println(sequenceInput2);

        topSeq = sequenceInput1.toCharArray();
        botSeq = sequenceInput2.toCharArray();

        gapPen = Integer.valueOf(tfGapPen.getText());
        missPen = Integer.valueOf(tfMisPen.getText());
        matchPen = Integer.valueOf(tfMatchPen.getText());
    }

    private void doWork(int i, int j) {
        int result;
        int first = matrix[i - 1][j - 1].getScore() + getIsMatch(i - 1, j - 1);
        int second = matrix[i][j - 1].getScore() + gapPen;
        int third = matrix[i - 1][j].getScore() + gapPen;
        if (rbGlobal.isSelected()) {
            result = Math.max(first, Math.max(second, third));
        } else {
            result = Math.max(first, Math.max(second, Math.max(third, 0)));
        }

        matrix[i][j].setScore(result);
        if (result == first) {
            matrix[i - 1][j - 1].setDirection(Direction.TopLeft);
        }
        if (result == second) {
            matrix[i - 1][j - 1].setDirection(Direction.Left);
        }
        if (result == third) {
            matrix[i - 1][j - 1].setDirection(Direction.Top);
        }
    }

    private int getIsMatch(int i, int j) {
        if (botSeq[i] == topSeq[j]) {
            return matchPen;
        } else {
            return missPen;
        }
    }

    private void printMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%5d ", matrix[i][j].getScore());
            }
            System.out.println();
        }
    }

    private void printDirection() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.printf("%5s ", matrix[i][j].getDirection());
            }
            System.out.println();
        }
    }

    private void initMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = new MatrixItem();
            }
        }

        if (rbGlobal.isSelected()) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][0].setScore(i * -4);
            }

            for (int i = 0; i < matrix[0].length; i++) {
                matrix[0][i].setScore(i * -4);
            }
        }

    }
}