/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenprojecttest;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

/**
 *
 * @author DevinPetersohn
 */
public class SparkDeployment extends Thread {
    
    private static SparkConf sparkConf;
    private static JavaSparkContext jsc;
    private static boolean stop = false;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public static double result;
    
    SparkDeployment(SparkConf sparkConf) {
        this.sparkConf = sparkConf;
        jsc = new JavaSparkContext(this.sparkConf);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void doNotify(String message) {
        Platform.runLater(() -> {
            // I'm choosing not to send the old value (second param).  Sending "" instead.
            pcs.firePropertyChange("message", "", message);
        });
    }
    
    @Override
    public void run() {
        deploy();
        doNotify("Pie is approximately " + Double.toString(result));
    }
    
    public void end() {
        stop = true;
    }

    private static void deploy(){
        
        
        
        int slices = 2;
        int n = 100000 * slices;
        List<Integer> l = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
          if(stop) break;
          l.add(i);
        }

        JavaRDD<Integer> dataSet = jsc.parallelize(l, slices);

        int count = dataSet.map(new Function<Integer, Integer>() {
          @Override
          public Integer call(Integer integer) {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            return (x * x + y * y < 1) ? 1 : 0;
          }
        }).reduce(new Function2<Integer, Integer, Integer>() {
          @Override
          public Integer call(Integer integer, Integer integer2) {
            return integer + integer2;
          }
        });

        System.out.println("Pi is roughly " + 4.0 * count / n);
        result = 4.0 * count / n;

        jsc.stop();
        
        
    }
    
}
