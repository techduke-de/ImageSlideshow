package com.imageslideshow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.awt.event.*;
import java.nio.Buffer;

public class MainWindow extends javax.swing.JFrame{

    private static String slash = "\\";  // '\\' for Windows '/' for Linux

    private static int delay = 3000; //Timer delay in ms

    public static void main(String[] args) throws IOException{
        MainWindow mainwindow = new MainWindow();
    }

    private MainWindow() throws IOException{


        //Choose Directory with Images
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("user.home"));
        chooser.setDialogTitle("Foto Ordner suchen");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){

            File folder = new File(chooser.getSelectedFile().toURI());
            File[] listOfFiles = folder.listFiles();
            String[] links = new String[listOfFiles.length]; //Contains complete Path and filename

            //Generate complete Links
            for(int i = 0; i < listOfFiles.length; i++){
                links[i] = chooser.getSelectedFile() + slash + listOfFiles[i].getName();

                //System.out.println("File: " + links[i]);
            }


            ImageClickedListener img_ClickedListener = new ImageClickedListener();


            //Create Image Label and set Mouse Click Listener
            JLabel img_lbl = new JLabel();
            img_lbl.addMouseListener(img_ClickedListener);


            //Create Main Frame and set to Fullscreen
            JFrame mainFrame = new JFrame();
            mainFrame.setLayout(new BorderLayout(0,0));
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setUndecorated(true);
            mainFrame.getContentPane().setBackground(Color.BLACK);
            mainFrame.setVisible(true);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.add(img_lbl);

            //Show first Image after Startup - with Thread
            try {
                LoadImageInBackground bgworker = new LoadImageInBackground(mainFrame.getWidth(),mainFrame.getHeight(), links[0]);
                img_lbl.setIcon(bgworker.doInBackground());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Show other images after delay of time
            Timer timer = new Timer(delay, new ActionListener() {
                int element = 1;
                @Override
                public void actionPerformed(ActionEvent e) {

                    //Load new Image
                    try {
                        LoadImageInBackground bgworker = new LoadImageInBackground(mainFrame.getWidth(), mainFrame.getHeight(), links[element]);
                        img_lbl.setIcon(bgworker.doInBackground());

                        if(element == links.length -1)
                            element = 0;
                        else
                            element++;

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        System.exit(1);
                    }
                }
            });

            timer.start();
        }
        else{
            System.out.print("No selection");
            System.exit(0);
        }
    }
}

class ImageClickedListener extends MouseAdapter{
    @Override
    public void mouseClicked(MouseEvent e){
        if(e.getClickCount() == 2){
            System.exit(0);
        }
    }
}

class LoadImageInBackground extends SwingWorker<ImageIcon, Object>{

    private int width;
    private int height;
    private String path;

    public LoadImageInBackground(int width, int height, String path){
        this.width = width;
        this.height = height;
        this.path = path;
    }

    @Override
    protected ImageIcon doInBackground() throws Exception {
        BufferedImage img = ImageIO.read(new File(this.path));
        ImageIcon icon = new ImageIcon(img.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH));
        return icon;
    }
}