/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xephinh;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.ClipboardContent;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author MTC
 */
public class Controller {

    Interface puzzle;
    Setting setting;
    HashMap idenBtn;

    JButton btnArray[][];
    JButton btnSufArray[][];
    JButton buttonEmpty;
    int rows;
    int cols;
    int chunks;
    String imagePath;
    ArrayList<Integer> listRandomKey;

    public Controller() {
        puzzle = new Interface();
        setting = new Setting();
        idenBtn = new HashMap();

        puzzle.setVisible(true);
        rows = defaultCellHorizon;
        cols = defaultCellVertical;
        imagePath = defaultImg;
        chunks = calChucks();
        setBoard();
    }

    private void setting() {
        setting.setVisible(true);
        setting.getBtnDefault().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setting.getCbbPicture().setSelectedIndex(0);
                setting.getTxtCellHorizon().setText(defaultCellHorizon + "");
                setting.getTxtCellVertical().setText(defaultCellVertical + "");
            }
        });
        setting.getBtnOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePath = listImage[setting.getCbbPicture().getSelectedIndex()];
                rows = Integer.parseInt(setting.getTxtCellHorizon().getText());
                cols = Integer.parseInt(setting.getTxtCellVertical().getText());
                chunks = calChucks();
                setBoard();
                setting.setVisible(false);
            }
        });
    }

    private int calChucks() {
        return rows * cols;
    }

    public void control() {
        puzzle.getBtnSetting().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setting();
                puzzle.getBtnRestart().setEnabled(false);
                puzzle.getBtnStart().setEnabled(true);
            }
        });

        puzzle.getBtnRestart().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomBoard();
            }
        });

        puzzle.getBtnStart().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomBoard();
                puzzle.getBtnRestart().setEnabled(true);
                puzzle.getBtnStart().setEnabled(false);
                buttonEmpty.setBackground(Color.red);
            }
        });
    }

    private void setBoard() {
        btnArray = new JButton[rows][cols];
        BufferedImage[] imgs = getImages();
        idenBtn.clear();
        int count = 0;
        int sizeWidth = calChunkWidth();
        int sizeHeight = calChunkHeigh();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                btnArray[i][j] = new JButton("");
                btnArray[i][j].setPreferredSize(new Dimension(sizeWidth, sizeHeight));
                btnArray[i][j].setIcon(new ImageIcon(imgs[count]));
                idenBtn.put(count++, btnArray[i][j]);
                btnArray[i][j].setMargin(new Insets(2, 2, 2, 2));
            }
        }
        buttonEmpty = (JButton) idenBtn.get(0);
        addBoard(new ArrayList<>(idenBtn.keySet()));
    }

    private void addBoard(ArrayList<Integer> listkey) {
        btnSufArray = new JButton[rows][cols];
        puzzle.getPnlPlayArea().removeAll();
        puzzle.getPnlPlayArea().setLayout(new GridLayout(rows, cols, 0, 0));
        puzzle.getPnlPlayArea().setSize(rows * calChunkWidth(), cols * calChunkHeigh());
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton btn = (JButton) idenBtn.get(listkey.get(count++));
                puzzle.getPnlPlayArea().add(btn);
                btnSufArray[i][j] = btn;
            }
        }
        addActionForSufArray();
        puzzle.pack();
    }

    private void addActionForSufArray() {
        removeAction();
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final int ii = i;
                final int jj = j;
                final int posi = count++;
                btnSufArray[i][j].addActionListener((ActionEvent e) -> {
                    if (checkMove(btnSufArray[ii][jj], jj, ii)) {
//                        System.out.println("huhu");
                        swapButton(posi);
                    }
                });
            }
        }
    }

    private void removeAction() {
        for (JButton[] btnSingDe : btnSufArray) {
            for (JButton btn : btnSingDe) {
                for (ActionListener e : btn.getActionListeners()) {
                    btn.removeActionListener(e);
                }
            }
        }
    }

    private void randomBoard() {
        listRandomKey = new ArrayList<>();
        listRandomKey.addAll(idenBtn.keySet());
        Collections.shuffle(listRandomKey);
        addBoard(listRandomKey);
    }

    private boolean checkMove(JButton button, int xCurrent, int yCurrent) {
        if (!button.equals(buttonEmpty)) {
            int xEmpty = getXButtonEmpty();
            System.out.println("X:" + xCurrent);
            System.out.println("X:" + xEmpty);
            int yEmpty = getYButtonEmpty();
            System.out.println("Y:" + yCurrent);
            System.out.println("Y:" + yEmpty);
            return checkLeft(xEmpty, yEmpty, xCurrent, yCurrent)
                    || checkRight(xEmpty, yEmpty, xCurrent, yCurrent)
                    || checkTop(xEmpty, yEmpty, xCurrent, yCurrent)
                    || checkBottom(xEmpty, yEmpty, xCurrent, yCurrent);
        }
        return false;
    }

    private int getXButtonEmpty() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (btnSufArray[i][j].equals(buttonEmpty)) {
                    return j;
                }
            }
        }
        return -1;
    }

    private int getYButtonEmpty() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (btnSufArray[i][j].equals(buttonEmpty)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getPosiButtonEmpty() {
        for (int i = 0; i < listRandomKey.size(); i++) {
            if (listRandomKey.get(i) == 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean checkBottom(int xEmpty, int yEmpty, int xCurrent, int yCurrent) {
        if (yCurrent > 0) {
            System.out.println("Bottom: " + (yEmpty == yCurrent - 1 && xEmpty == xCurrent));
            System.out.println("");
            return yEmpty == yCurrent - 1 && xEmpty == xCurrent;
        }
        return false;
    }

    private boolean checkTop(int xEmpty, int yEmpty, int xCurrent, int yCurrent) {
        if (yCurrent < cols - 1) {
            System.out.println("Top: " + (yEmpty == yCurrent + 1 && xEmpty == xCurrent));
            return yEmpty == yCurrent + 1 && xEmpty == xCurrent;
        }
        return false;
    }

    private boolean checkLeft(int xEmpty, int yEmpty, int xCurrent, int yCurrent) {
        if (xCurrent > 0) {
            System.out.println("Left: " + (xEmpty == xCurrent - 1 && yEmpty == yCurrent));
            return xEmpty == xCurrent - 1 && yEmpty == yCurrent;
        }
        return false;
    }

    private boolean checkRight(int xEmpty, int yEmpty, int xCurrent, int yCurrent) {
        if (xCurrent < rows - 1) {
            System.out.println("Right: " + (xEmpty == xCurrent + 1 && yEmpty == yCurrent));
            return xEmpty == xCurrent + 1 && yEmpty == yCurrent;
        }
        return false;
    }

    private void swapButton(int posiCurrent) {
        int posiEmpty = getPosiButtonEmpty();
        listRandomKey.set(posiEmpty, listRandomKey.get(posiCurrent));
        listRandomKey.set(posiCurrent, 0);
        addBoard(listRandomKey);
    }

    public BufferedImage[] getImages() {
//        File file = new File(imagePath);
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(file);
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(GridImage.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//        BufferedImage image = null;
//        try {
//            image = ImageIO.read(fis);
//
//        } catch (IOException ex) {
//            Logger.getLogger(GridImage.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
        BufferedImage image = inputImage();
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        return imgs;
    }

    private BufferedImage inputImage() {
        File file = new File(imagePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GridImage.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(fis);

        } catch (IOException ex) {
            Logger.getLogger(GridImage.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    private int calChunkWidth() {
        return inputImage().getWidth() / cols;
    }

    private int calChunkHeigh() {
        return inputImage().getHeight() / rows;
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.control();
    }

    private final String listImage[]
            = {"./image/background1.png", "./image/background2.png", "./image/background3.png",
                "./image/background4.png", "./image/background5.png"};

    private final String defaultImg = listImage[0];
    private final int defaultCellHorizon = 3;
    private final int defaultCellVertical = 3;

}
