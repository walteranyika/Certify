/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.walter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Walter
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    TableView<Item> tableItems;

    @FXML
    TableColumn<Item, String> columnNames;

    @FXML
    TableColumn<Item, Integer> columnPos;

    @FXML
    private TextField tfImgPath;
    
    @FXML
    private ProgressBar pbBar;

    @FXML
    private TextField tfFontPath;

    @FXML
    private Label labelProgress;

    @FXML
    private Label labelOutput;
    
    @FXML
    private Label labelCsv;

    @FXML
    private TextArea textAreaNames;

    ObservableList<Item> listItems = FXCollections.observableArrayList();

    @FXML
    private void handleButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        //FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterPNG); //extFilterJPG,             
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        try {
            BufferedImage bufferedImg = ImageIO.read(file);
            int h = bufferedImg.getHeight() / 2;
            int w = bufferedImg.getWidth() / 2;
            System.out.println(h + " : " + w);
            System.out.println(file.getAbsoluteFile());
            tfImgPath.setText(file.getAbsolutePath());
            saveSettings(Constants.TEMPLATE_KEY, file.getAbsolutePath());
        } catch (IOException | IllegalArgumentException ex) {
            tfImgPath.setText("No image choosen");
            ex.printStackTrace();
        }

    }

    @FXML
    private void handleFontAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        //FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterTTF = new FileChooser.ExtensionFilter("Font files (*.ttf)", "*.TTF");
        fileChooser.getExtensionFilters().addAll(extFilterTTF); //extFilterTTF,             
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        try {

            tfFontPath.setText(file.getAbsolutePath());
            saveSettings(Constants.FONT_KEY, file.getAbsolutePath());

        } catch (NullPointerException ex) 
        {
            tfFontPath.setText("No font choosen");
            ex.printStackTrace();
        }

    }

    @FXML
    private void handleProcessAction(ActionEvent event) {
        String all = textAreaNames.getText().trim();
        final String imgPath = tfImgPath.getText();
        listItems.clear();
        final String data[] = all.split(",");//{"John Mark","Mary Mwangi","Janet Koech","Hellen Sambili"};
        final String fontPath = tfFontPath.getText().trim();
        final String outputPath=labelOutput.getText().trim();
        pbBar.setVisible(true);
        pbBar.setProgress(0);
        // This works
        if (data.length > 1) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    printer(imgPath, fontPath, data,outputPath);
                }
            });
            thread.start();
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Names");
            alert.setHeaderText("Empty Field");
            alert.setContentText("Enter A few names separated by a comma,");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClearAction(ActionEvent event) {
        textAreaNames.clear();
        listItems.clear();
        labelProgress.setText("Processed 0 out of 0 certificates");
        pbBar.setProgress(0);
        pbBar.setVisible(false);
    }

    @FXML
    private void handleOutputAction(ActionEvent event) {

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Output Folder");
        Button btn = (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        File selectedDirectory = chooser.showDialog(stage);
        String outPath = selectedDirectory.getAbsolutePath();
        saveSettings(Constants.OUTPUT_KEY, outPath);
        labelOutput.setText(outPath);
        System.out.println(outPath);
    }

    private void printer(String imgPath, String fontPath, String[] data, String outputPath) {
        if (!imgPath.isEmpty() && !fontPath.isEmpty() && (imgPath.toLowerCase().contains(".png") || imgPath.toLowerCase().contains(".jpg")) && fontPath.toLowerCase().contains(".ttf")) {
            final int size = data.length;
            for (int i = 0; i < size; i++) {
                String string = data[i];
                final int curr_pos = i + 1;
                if (!string.isEmpty()) {
                    //String desktop=System.getProperty("user.home")+"/Desktop/fonts";
                    try {
                        BufferedImage bufferedImg = ImageIO.read(new File(imgPath));
                        int h = bufferedImg.getHeight() / 2;
                        int w = bufferedImg.getWidth() / 2;

                        Graphics gg = bufferedImg.getGraphics();
                        Graphics2D graphics = (Graphics2D) gg;
                        graphics.setColor(Color.BLACK);
                        Font ff = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
                        ff = ff.deriveFont(Font.BOLD, 55);

                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(ff);

                        graphics.setFont(ff);
                        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                        FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());

                        Rectangle rect = new Rectangle(h, 0, w * 2, 60);
                        int x = (rect.width - metrics.stringWidth(string)) / 2;

                        graphics.drawString(string.trim().toUpperCase(), x, 520);
                        String file_name = string.trim().replace(' ', '_');
                        file_name = file_name.toLowerCase();
                        file_name = i + "_" + file_name + ".png";
                        // String desktopy = System.getProperty("user.home") + "/Desktop/output/";
                        String desktopy=outputPath+File.separator;
                        ImageIO.write(bufferedImg, "png", new File(desktopy + file_name));
                        // graphics.drawString(names[0], x, 520);
                        //int pos =i;
                        //pos=pos+1;
                        //labelStatus.setText(pos+" out of  "+ len);
                        listItems.add(new Item(curr_pos, string));
                        double prog= (double)curr_pos/(double)size;
                        Platform.runLater(() -> pbBar.setProgress(prog));
                        Platform.runLater(() -> labelProgress.setText("Processed " + curr_pos + " out of " + size));
                        System.out.println(file_name);
                        graphics.dispose();

                    } catch (IOException | FontFormatException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {

            Platform.runLater(() -> showDialog());
            /* */
        }

    }

    private void showDialog() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Check Your Font/Image Template");
        alert.setHeaderText("Something is missing");
        alert.setContentText("You have something missing. Confirm that you have selected a font to use and an image template");
        alert.showAndWait();
    }

    private void makeDefaultOutput() {
        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "CreatedCerts";
        File customDir = new File(path);
        if (customDir.exists()) {
            System.out.println(customDir + " already exists "+customDir.getAbsolutePath());
        } else if (customDir.mkdirs()) {
            System.out.println(customDir + " was created " +customDir.getAbsolutePath());
        } else {
            System.out.println(customDir + " was not created");
        }
        labelOutput.setText(customDir.getAbsolutePath());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        columnNames.setCellValueFactory(cellData -> cellData.getValue().namesProperty());
        columnPos.setCellValueFactory(cellData -> cellData.getValue().posProperty().asObject());
        tableItems.setItems(listItems);
        makeDefaultOutput();
        pbBar.setVisible(false);
        //System.out.println(restoreSettings(Constants.FONT_KEY));
        tfImgPath.setText(restoreSettings(Constants.TEMPLATE_KEY));
        tfFontPath.setText(restoreSettings(Constants.FONT_KEY));
        labelCsv.setText(restoreSettings(Constants.CSV_KEY));
        labelOutput.setText(restoreSettings(Constants.OUTPUT_KEY));
        
    }
    
    private void readCSV(String path) {
        //String csvFile = "/Users/mkyong/csv/country.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        StringBuilder builder=new StringBuilder();

        try {

            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                //String[] country = line.split(cvsSplitBy);
                 System.out.println(line);
                 builder.append(line);
                 builder.append(",");                 
                //System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");
            }            
           String all=builder.toString();
           int x= all.lastIndexOf(",");
           all=all.substring(0, x);
           textAreaNames.setText(all);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    @FXML
    private void handleCSVAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        //FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterTTF = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.CSV");
        fileChooser.getExtensionFilters().addAll(extFilterTTF); //extFilterTTF,             
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        try 
        {

            labelCsv.setText(file.getAbsolutePath());
            readCSV(file.getAbsolutePath());
            saveSettings(Constants.CSV_KEY, file.getAbsolutePath());

        } catch (Exception ex) {
            labelCsv.setText("No CSV choosen");
            ex.printStackTrace();
        }

    }

    private void saveSettings(String key, String value)
    {
        Preferences prefs=Preferences.userNodeForPackage(com.walter.FXMLDocumentController.class);
        prefs.put(key,value);
    }
    
    private String restoreSettings(String key)
    {
        Preferences prefs=Preferences.userNodeForPackage(com.walter.FXMLDocumentController.class);
        return prefs.get(key,"Empty");
    }   
}
