/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenprojecttest;

/**
 *
 * @author DevinPetersohn
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** 
 * Computes an approximation to pi
 * Usage: JavaSparkPi [slices]
 */



public final class MainClass extends Application{

    
    
    @Override
    public void start(Stage primaryStage) {
        
        Parent root = null;
        FXMLLoader loader = null;
        
        
        try {
            
            loader = new FXMLLoader(getClass().getResource("/fxml/StockVisualizationFXML.fxml"));
            //loader.setController(new SparkDeployFXMLController());
            root = (Parent) loader.load();
            //root = FXMLLoader.load(getClass().getResource("/fxml/StockVisualizationFXML.fxml"));
            //loader = FXMLLoader.load(getClass().getResource("/fxml/StockVisualizationFXML.fxml"));
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
        SparkDeployFXMLController controller = (SparkDeployFXMLController)loader.getController();
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);
        primaryStage.show();
        controller.ready();
        
        
        
    }

    
  public static void main(String[] args) {
    launch(args);
    
    //stock.print();
    //facebook.print();
    
    
    
    
  }
}