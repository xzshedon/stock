package com.my.stock;

import android.content.Context;

import java.util.ArrayList;

public class DataHandler{

    //存储股票数据的数组
    private ArrayList<StockInfo> stockInfo = new ArrayList<StockInfo>();


    public DataHandler(Context mContext) {
    }

    public int stocksSize() {
        return 0;
    }

    public StockInfo getQuoteForIndex(int index) {
        return stockInfo.get(index);
    }

    public void refreshStocks() {
    }

    public void addSymbolsToFile(ArrayList<String> symbols) {
    }

    public void removeQuoteByIndex(int index) {

    }
}
