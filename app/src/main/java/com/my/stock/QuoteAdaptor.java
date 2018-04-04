package com.my.stock;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//股票数据适配器
public class QuoteAdaptor extends BaseAdapter implements ListAdapter,Runnable{
    //当前显示的最大数量为10
    private static final int DISPLAY_COUNT = 10;
    public DataHandler dataHandler;
    //强制更新标志
    private boolean forceUpdate = false;
    //保存上下文
    Context context;
    //保存Activity实例
    MainActivity mainActivity;
    LayoutInflater inflater;

    QuoteRefreshTask quoteRefreshTask = null;
    int progressInterval;
    //消息处理器
    Handler messageHandler = new Handler();

    public QuoteAdaptor(MainActivity aController, Context mContext,DataHandler mdataHandler){
        //保存当前的上下文和Activity实例
        context = mContext;
        mainActivity = aController;
        dataHandler = mdataHandler;
    }

    //取得股票数组的大小
    public int getCount(){
        return dataHandler.stocksSize();
    }

    //获取当前位置股票的对象
    public StockInfo getItem(int position){
        return dataHandler.getQuoteForIndex(position);
    }

    //获取当前的位置
    public long getItemId(int position){
        return position;
    }

    //生成视图
    public View getView(int position, View convertView, ViewGroup parent){
        StockInfo quote;
        inflater = LayoutInflater.from(context);
        RelativeLayout cellLayout = (RelativeLayout)
                inflater.inflate(R.layout.simple_stock,null);
        cellLayout.setMinimumWidth(parent.getWidth());
        int color;
        mainActivity.setProgress(progressInterval*(position+1));
        if (position %2 > 0)
            color = Color.rgb(48,92,131);
        else
            color = Color.rgb(119,138,170);
        cellLayout.setBackgroundColor(color);
        quote = dataHandler.getQuoteForIndex(position);
        TextView field = (TextView)cellLayout.findViewById(R.id.symbol);

        //设置股票的代码
        field.setText(quote.getNo());
        field.setClickable(true);
        field.setOnClickListener(mainActivity);

        //股票名字
        field = (TextView)cellLayout.findViewById(R.id.name);
        field.setClickable(true);
        field.setOnClickListener(mainActivity);
        field.setText(quote.getName());

        //设置股票当前价格
        field = (TextView)cellLayout.findViewById(R.id.current);
        double current = Double.parseDouble(quote.getCurrent_price());
        double closing_price = Double.parseDouble(quote.getClosing_price());
        //保留2位小数
        DecimalFormat df = new DecimalFormat( "#0.00");
        String percent = df.format(((current-closing_price)*100/closing_price))+"%";
        field.setText(df.format(current));
        field.setClickable(true);
        field.setOnClickListener(mainActivity);
        field = (TextView)cellLayout.findViewById(R.id.percent);
        //若股票价格超过昨日收盘价
        if (current > closing_price){
            //设置字体为红色
            field.setTextColor(0xffEE3B3B);
        }
        else {
            //设置字体为绿色
            field.setTextColor(0xff2e8b57);
        }
        field.setText(percent);
        cellLayout.setId(position + 33);
        cellLayout.setClickable(true);
        cellLayout.setOnClickListener(mainActivity);
        return cellLayout;
    }

    //所有元素均可选择
    public boolean areAllTtemSelectable(){
        return true;
    }
    public boolean isSelectable(int arg0){
        return true;
    }

    //开始更新股票
    public void startRefresh() {
        if(quoteRefreshTask == null)
            quoteRefreshTask = new QuoteRefreshTask(this);
    }

    //停止更新股票
    public void stopRefresh() {
        quoteRefreshTask.cancelTimer();
        quoteRefreshTask = null;
    }

    //更新适配器
    public void refreshQuotes(){
        messageHandler.post(this);
    }

    //更新适配器内容 ****
    public void run(){
        if(dataHandler.stocksSize()>0){
            if(forceUpdate){
                forceUpdate = false;
                progressInterval = 10000/DISPLAY_COUNT;
                mainActivity.setProgressBarVisibility(true);
                mainActivity.setProgress(progressInterval);
                dataHandler.refreshStocks();
            }
            //通知数据更改
            this.notifyDataSetChanged();
        }
    }

    //股票代码保存到文件中
    public void addSymbolsToFile(ArrayList<String> symbols){
        //强行更新页面数据
        forceUpdate = true;
        //添加股票到文件中
        dataHandler.addSymbolsToFile(symbols);
        //添加消息到消息列队
        messageHandler.post(this);
    }

    //移除列表中的数据
    public void removeQuoteAtIndex(int index){
        forceUpdate = true;
        dataHandler.removeQuoteByIndex(index);
        messageHandler.post(this);
    }

    //股票更新定时器
    public class QuoteRefreshTask extends TimerTask{
        QuoteAdaptor quoteAdaptor;
        Timer refreshTimer;
        final static  int TENSECONDS = 10000;
        public QuoteRefreshTask(QuoteAdaptor anAdaptor){
            refreshTimer.schedule(this, TENSECONDS, TENSECONDS);
            quoteAdaptor = anAdaptor;
        }
        public void run(){
            messageHandler.post(quoteAdaptor);
        }
        public void startTimer(){
            if (refreshTimer == null){
                refreshTimer = new Timer("Quote Refresh Timer");
                refreshTimer.schedule(this, TENSECONDS, TENSECONDS);
            }
        }
        //取消定时器
        public void cancelTimer(){
            this.cancel();
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }
}
