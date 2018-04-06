package com.my.stock;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends ListActivity implements View.OnClickListener, KeyEvent.Callback{

    //股票数据适配器
    private QuoteAdaptor quoteAdaptor;
    //股票代码输入框
    private EditText symbolText;
    //股票代码输入按钮
    private Button addButton;
    //返回按钮
    private Button cancelButton;
    //删除按钮
    private Button deleteButton;
    //对话框
    private Dialog dialog = null;
    //股票详细信息
    private TextView currentTextView, noTextView, openTextView,
            closeTextView,dayLowTextView,dayHighTextView;
    //日K线图
    private ImageView chartView;
    //股票数据处理类
    DataHandler mDataHandler;
    //当前Activity实例
    MainActivity mContext;
    //当前选中的股票序号
    int currentSelectedIndex;

    //初始化界面
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        mContext = this;

        //验证当前存放股票代码的文件是否存在
        File mFile = new File("/data/data/com.my.stock/files/symbols.txt");
        if (mFile.exists()){
            Log.e("guojs","file exits");
        }else {
            try{
                //新建股票代码的文件
                FileOutputStream outputStream = openFileOutput("symbols.txt",MODE_PRIVATE);
                outputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            Log.e("guojs","file no exist");
        }

        //初始化股票代码处理类
        mDataHandler = new DataHandler(mContext);
        //如果adapter数据为空显示的内容
        getListView().setEmptyView(findViewById(R.id.empty));
        quoteAdaptor = new QuoteAdaptor(this,this,mDataHandler);
        //为列表设置适配器
        this.setListAdapter(quoteAdaptor);
        //添加股票按钮
        addButton = (Button)findViewById(R.id.add_symbols_button);
        //设置按钮监听器
        addButton.setOnClickListener(this);
        //股票输入框
        symbolText = (EditText)findViewById(R.id.stock_symbols);
    }


    //生命周期： onCreate->onStart->onResume
    protected void onResume(){
        super.onResume();;
        if (quoteAdaptor != null){
            //开始更新界面
            quoteAdaptor.startRefresh();
        }
    }

    //界面不可见时停止更新
    protected void onStop(){
        super.onStop();
        //停止更新界面
        quoteAdaptor.stopRefresh();
    }

    //列表元素被单机后触发
    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l,v,position,id);
        //取得单机位置的股票
        StockInfo quote = quoteAdaptor.getItem(position);
        //取得当前位置的序号
        currentSelectedIndex = position;
        if(dialog == null){
            dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.details_stock);
            //删除按钮
            deleteButton = (Button)dialog.findViewById(R.id.delete);
            //设置删除按钮监听器
            deleteButton.setOnClickListener(this);
            //“返回”主界面按钮
            cancelButton = (Button)dialog.findViewById(R.id.close);
            //设置返回按钮监听器
            cancelButton.setOnClickListener(this);
            //当前股票价格
            currentTextView = (TextView)dialog.findViewById(R.id.current);
            //当前股票编码
            noTextView = (TextView)dialog.findViewById(R.id.no);
            //昨日收盘价格
            openTextView = (TextView)dialog.findViewById(R.id.opening_price);
            //今日收盘价格
            closeTextView = (TextView)dialog.findViewById(R.id.closing_price);
            //今日最低价
            dayLowTextView = (TextView)dialog.findViewById(R.id.day_low);
            //今日最高价
            dayHighTextView = (TextView)dialog.findViewById(R.id.day_high);
            //股票K线图
            chartView = (ImageView)dialog.findViewById(R.id.chart_view);
        }

        //设置对话框标题
        dialog.setTitle(quote.getName());
        //设置股票当前价格
        double current = Double.parseDouble(quote.getCurrent_price());
        double closing_price = Double.parseDouble(quote.getClosing_price());
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");
        String percent = df.format(((current-closing_price)*100/closing_price))+"%";
        //若股票价格超过昨日收盘价
        if (current > closing_price){
            //设置字体为红色
            currentTextView.setTextColor(0xffEE3B3B);
        }
        else {
            //设置字体为绿色
            currentTextView.setTextColor(0xff2e8b57);
        }
        //设置TextView内容
        currentTextView.setText(df.format(current)+" ("+percent+")");
        openTextView.setText(quote.opening_price);
        closeTextView.setText(quote.closing_price);
        dayHighTextView.setText(quote.max_price);
        dayLowTextView.setText(quote.min_price);
        noTextView.setText(quote.no);
        //设置K线图
        chartView.setImageBitmap(mDataHandler.getChartForSymbol(quote.no));
        dialog.show();
    }

    //判断回车键按下时添加股票
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            //添加股票
            addSymbol();
            return true;
        }
        return false;
    }

    //添加股票代码，以空格或回车分割多个股票
    private void addSymbol() {
        //获得文本框的输入内容
        String symbolsStr = symbolText.getText().toString();
        //将回车符替换为空格
        symbolsStr = symbolsStr.replace("\n", " ");
        //以空格分割字符
        String symbolArray[] = symbolsStr.split(" ");
        int index,count = symbolArray.length;
        ArrayList<String> symbolList = new ArrayList<>();
        for(index = 0; index < count;index++){
            symbolList.add(symbolArray[index]);
        }
        //将股票代码添加到文件中
        quoteAdaptor.addSymbolsToFile(symbolList);
        //设置文本为空
        symbolText.setText(null);
    }

    //设置按钮回调函数
    public void onClick(View view){
        if(view == addButton){
            //添加股票到文件中
            addSymbol();
        }else if(view == cancelButton){
            dialog.dismiss();
        }else if (view == deleteButton){
            //删除当前股票
            quoteAdaptor.removeQuoteAtIndex(currentSelectedIndex);
            dialog.dismiss();
        }else if (view.getParent() instanceof RelativeLayout){
            RelativeLayout r1 = (RelativeLayout)view.getParent();
            this.onListItemClick(getListView(), r1, r1.getId(), r1.getId());
        }else if(view instanceof RelativeLayout){
            this.onListItemClick(getListView(),view,view.getId(), view.getId());
        }
    }




}
