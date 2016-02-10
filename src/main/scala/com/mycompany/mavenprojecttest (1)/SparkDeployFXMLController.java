/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenprojecttest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.spark.SparkConf;

/**
 * FXML Controller class
 *
 * @author DevinPetersohn
 */
public class SparkDeployFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
    }    
    
    StockAnalysis stockList = new StockAnalysis();
    
    Stage stage;
    
    @FXML
    TextArea statusTextArea;
    
    @FXML
    GridPane bigGrid;
    
    @FXML
    LineChart<Date,Number> chartArea;
    
    @FXML
    ListView historicalQuotes;
    
    @FXML
    Button searchYahoo;
    
    @FXML
    TextField searchField;
    
    @FXML
    AnchorPane anchorPane;
    
    @FXML
    Button deployButton;
    
    @FXML
    TextField masterNode;
    
    @FXML
    TextField memory;
    
    @FXML
    TextField cores;
    
    LineChart<Date,Number> multiChartArea = null;
    
    public void ready() {
        anchorPane.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                try {
                    FileOutputStream fileOut = new FileOutputStream("history.stock", false);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(stockList);
                    out.close();
                    fileOut.close();
                } catch (IOException ioex) {
                    System.out.println("IOException: " + ioex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        
        try {
            FileInputStream fileIn = new FileInputStream("history.stock");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            StockAnalysis stockList2 = (StockAnalysis) in.readObject();
            in.close();
            fileIn.close();


            stockList.stockInfo.addAll(stockList2.stockInfo);
            for(String i : stockList.stockInfo){
                System.out.println(i);
            }

            historicalQuotes.setItems(FXCollections.observableList(stockList.stockInfo));
            stockList.stocks = new ArrayList<>();
            stockList.isDisplayed = new ArrayList<>();

           for(String i : stockList.stockInfo) {
                stockList.onlyAdd(i);
            } 
            if(stockList.stockInfo.size() - stockList2.stockInfo.size() == 0){
                historicalQuotes.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {

                    if(multiChartArea == null){
                        if(chartArea != null)
                            bigGrid.getChildren().remove(chartArea);

                        NumberAxis numAxis = new NumberAxis();
                        DateAxis datAxis = new DateAxis();
                        numAxis.setLabel("Percent Change (%)");
                        datAxis.setLabel("Date");

                        multiChartArea = new LineChart(datAxis,numAxis);
                        bigGrid.add(multiChartArea,1,1);
                        multiChartArea.setCreateSymbols(false);
                        multiChartArea.getData().add(stockList.getPercentHistory(stockList.stocks.get((Integer) new_val)));
                    } else multiChartArea.getData().add(stockList.getPercentHistory(stockList.stocks.get((Integer) new_val)));
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    private void handleAbout(Event event) {
        
    }
    
    @FXML
    private void handleSearchButton(Event event) {
        
        
        if(searchField.getText() != null && searchField.getText() != "") {
            System.out.println(searchField.getText());
            stockList.addStock(searchField.getText());
            historicalQuotes.setItems(FXCollections.observableList(stockList.stockInfo));
            
            double min = StockAnalysis.getMinimum(searchField.getText());
            double max = StockAnalysis.getMaximum(searchField.getText());
            NumberAxis numberAxis = new NumberAxis("Price ($)", min * 0.95, max * 1.05, (max - min) / 15);
            DateAxis dateAxis = new DateAxis();
            numberAxis.setLabel("Price ($)");
            dateAxis.setLabel("Date");
            
            LineChart temp = chartArea;
            chartArea = new LineChart<>(dateAxis,numberAxis);
            bigGrid.getChildren().remove(temp);
            bigGrid.getChildren().remove(multiChartArea);
            multiChartArea = null;
            
            bigGrid.add(chartArea, 1, 1);
            chartArea.getData().add(stockList.getPriceHistory(searchField.getText()));
            chartArea.setCreateSymbols(false);
            
            if(stockList.stocks.size() == 1) {
                historicalQuotes.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    if(multiChartArea == null){
                        bigGrid.getChildren().remove(chartArea);
                        NumberAxis numAxis = new NumberAxis();
                        DateAxis datAxis = new DateAxis();
                        numAxis.setLabel("Percent Change (%)");
                        datAxis.setLabel("Date");
                    
                        multiChartArea = new LineChart(datAxis,numAxis);
                        bigGrid.add(multiChartArea,1,1);
                        multiChartArea.setCreateSymbols(false);
                    
                        multiChartArea.getData().add(stockList.getPercentHistory(stockList.stocks.get((Integer) new_val)));
                    } else multiChartArea.getData().add(stockList.getPercentHistory(stockList.stocks.get((Integer) new_val)));
                });
            }
        }
    }
    
    @FXML
    private void handleSaveItemAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dialog");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Stock Files (*.stock)", "*.stock");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
            if (file != null) {
                System.out.println(file.getAbsolutePath());
                try {
                    FileOutputStream fileOut = new FileOutputStream(file.getPath());
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(stockList);
                    out.close();
                    fileOut.close();
                } catch (IOException ioex) {
                    System.out.println("IOException: " + ioex.getMessage());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
    }
    
    
    
    @FXML
    private void handleOpenItemAction(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Dialog");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Stock files (*.stock)", "*.stock"), 
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            try {
                FileInputStream fileIn = new FileInputStream(file.getPath());
               ObjectInputStream in = new ObjectInputStream(fileIn);
               StockAnalysis stockList2 = (StockAnalysis) in.readObject();
               in.close();
               fileIn.close();
               
               
               stockList.stockInfo.addAll(stockList2.stockInfo);
               historicalQuotes.setItems(FXCollections.observableList(stockList.stockInfo));
               stockList.stocks = new ArrayList<>();
               stockList.isDisplayed = new ArrayList<>();
               
               for(String i : stockList.stockInfo) {
                    stockList.onlyAdd(i);
                } 
                if(stockList.stockInfo.size() - stockList2.stockInfo.size() == 0){
                    historicalQuotes.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {

                        if(multiChartArea == null){
                            if(chartArea != null)
                                bigGrid.getChildren().remove(chartArea);

                            NumberAxis numAxis = new NumberAxis();
                            DateAxis datAxis = new DateAxis();
                            numAxis.setLabel("Percent Change (%)");
                            datAxis.setLabel("Date");

                            multiChartArea = new LineChart(datAxis,numAxis);
                            bigGrid.add(multiChartArea,1,1);
                            multiChartArea.setCreateSymbols(false);

                            multiChartArea.getData().add(stockList.getPercentHistory(stockList.stocks.get((Integer) new_val)));

                        } else multiChartArea.getData().add(stockList.getPercentHistory(stockList.stocks.get((Integer) new_val)));
                    });
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }             
        }
        
        
    }
    
    @FXML
    public void handleClose(ActionEvent event) {
        try {
            FileOutputStream fileOut = new FileOutputStream("history.stock", false);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(stockList);
            out.close();
            fileOut.close();
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleDeployButton(Event event){
        String masterID = null;
        String memoryAmount;
        String numCores;
        
        if(masterNode.getText() != null)
            masterID = masterNode.getText();
        else return;
        if(memory.getText() != null)
            memoryAmount = memory.getText();
        else return;
        if(cores.getText() != null)
            numCores = cores.getText();
        else return;
        
        if(masterID == "local")
            masterID = masterID + "[" + numCores + "]";
        
        SparkConf sparkConf = new SparkConf().setMaster(masterID)
                .set("spark.driver.memory", memoryAmount)
                .set("spark.executor.memory", memoryAmount)
                .set("spark.driver.cores", numCores)
                .setAppName("StockVisualizer");
    

        SparkDeployment sparkDeployment = new SparkDeployment(sparkConf);
        sparkDeployment.start();
        
        sparkDeployment.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                statusTextArea.appendText((String)evt.getNewValue() + "\n");
            }
        });
        /*
        Node node=(Node) event.getSource();
        Stage stage=(Stage) node.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/StockGraph.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        
        } catch (IOException ex) {
            System.out.println(ex);
        }
        */
    }
    
    
    
}
