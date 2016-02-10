 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenprojecttest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javafx.scene.chart.XYChart;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 *
 * @author DevinPetersohn
 */
public class StockAnalysis implements Serializable {

    transient ArrayList<Stock> stocks = new ArrayList<>();
    //ObservableList<String> stockInfo = FXCollections.observableArrayList ();
    ArrayList<String>stockInfo = new ArrayList<>();
    ArrayList<Boolean> isDisplayed = new ArrayList<>();
    
    public void onlyAdd(String ticker) {
        stocks.add(YahooFinance.get(ticker.split(" ")[0]));
        isDisplayed.add(false);        
    }
    
    public void addStock(String ticker) {
        Stock stock = YahooFinance.get(ticker);
        if(!stock.getName().matches("N/A")){
            stocks.add(stock);
            stockInfo.add(ticker + " - " + stock.getName() + " => $" + stock.getQuote().getPrice() + " (" + stock.getQuote().getChangeInPercent() + "%)");
            isDisplayed.add(false);
        } else System.out.println("TICKER ERROR");
    }
    
    public ArrayList<Stock> getStocks(){
        return stocks;
    }
    
    public XYChart.Series<Date, Number> getPriceHistory (String ticker) {
        
        for(int i = 0;i < isDisplayed.size(); i++)
            isDisplayed.set(i, false);
        
        Stock stock = YahooFinance.get(ticker);
        if(stock.getName().matches("N/A")) return null;
        XYChart.Series<Date, Number> series = new XYChart.Series();
        
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -1);
        
        series.setName(stock.getName() + " Historical Data");
        List<HistoricalQuote> hists = stock.getHistory(from, Interval.DAILY);
        for(HistoricalQuote hist : hists) {
            series.getData().add(new XYChart.Data(hist.getDate().getTime(),hist.getClose().doubleValue()));
        }
        return series;
    }
    
    public XYChart.Series<Date, Number> getPercentHistory (Stock stock) {
        if(isDisplayed.get(stocks.indexOf(stock))) return null;
        
        isDisplayed.set(stocks.indexOf(stock), true);
        
        if(stock.getName().matches("N/A")) return null;
        XYChart.Series<Date, Number> series = new XYChart.Series();
        
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -1);
        
        series.setName(stock.getName() + " Historical Data");
        List<HistoricalQuote> hists = stock.getHistory(from, Interval.DAILY);
        Collections.reverse(hists);
        double prevHist = hists.get(0).getClose().doubleValue();
        for(HistoricalQuote hist : hists) {
            series.getData().add(new XYChart.Data(hist.getDate().getTime(),((hist.getClose().doubleValue())/prevHist) - 1));
            
        }
        return series;
    }
    
    
    public static Double getMinimum (String ticker) {
        ArrayList<Double> list = new ArrayList<>();
        Stock stock = YahooFinance.get(ticker);
        if(stock.getName().matches("N/A")) return null;
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -1);
        
        
        List<HistoricalQuote> hists = stock.getHistory(from, Interval.DAILY);
        for(HistoricalQuote hist : hists) {
            list.add(hist.getClose().doubleValue());
        }
        return Collections.min(list);
    }
    

        public static Double getMaximum (String ticker) {
        ArrayList<Double> list = new ArrayList<>();
        Stock stock = YahooFinance.get(ticker);
        if(stock.getName().matches("N/A")) return null;
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -1);
        
        
        List<HistoricalQuote> hists = stock.getHistory(from, Interval.DAILY);
        for(HistoricalQuote hist : hists) {
            list.add(hist.getClose().doubleValue());
        }
        return Collections.max(list);
    }
    
}
