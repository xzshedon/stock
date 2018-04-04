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

    private void addSymbol() {
    }


}
