package com.my.stock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//股票数据处理类
public class DataHandler{

    private static final String TAG="DataHandler";
    //股票查询网址
    private static final String QUERY_URL = "http://hq.sinajs.cn/list=";
    //股票K线图
    private static final String QUERY_IMG = "http:image.sinajs.cn/newchart/daily/n/";
    //存储股票代码的文件
    private static final String SYMBOL_FILE_NAME = "symbols.txt";
    private int BUF_SIZE = 16384;
    //存储股票代码额的数组
    private ArrayList<String> stocks;
    //存储股票数据的数组
    private ArrayList<StockInfo> stockInfo = new ArrayList<StockInfo>();
    //各股票信息在数组中的索引
    private final int NAME=0;
    private final int OPENING_PRICE=1;
    private final int CLOSING_PRICE=1;
    private final int CURRENT_PRICE=1;
    private final int MAX_PRICE=1;
    private final int MIN_PRICE=1;
    Context context;

    public DataHandler(Context mContext){
        super();
        //读取存储在文件中的股票代码信息
        readStockFromFile();
        context=mContext;
        if(stocks !=null){
            //更新股票数据
            refreshStocks();
        }
    }

    //读取存储在文件中的股票代码信息
    private void readStockFromFile() {

    }


    public int stocksSize() {
        return 0;
    }

    public StockInfo getQuoteForIndex(int index) {
        return stockInfo.get(index);
    }

    //更新股票数据
    public void refreshStocks() {
    }

    public void addSymbolsToFile(ArrayList<String> symbols) {
    }

    public void removeQuoteByIndex(int index) {

    }

    //取得K线图
    public Bitmap getChartForSymbol(String symbol) {
        try{
            try{
                //构架股票K线图网址
                StringBuilder sb = new StringBuilder(QUERY_IMG);
                sb = sb.append(symbol+".gif");
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient req = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址
                HttpGet httpGet = new HttpGet(sb.toString());
                //第三步：执行请求，获取服务器发还的相应对象。访问执行网址
                HttpResponse response = req.execute(httpGet);
                InputStream iStream;
                BitmapDrawable bitMap;
                //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                if(response.getStatusLine().getStatusCode() == 200){
                    //第五步：从相应对象当中取出数据，放到entity当中
                    //取得网址返回的内容
                    iStream = response.getEntity().getContent();
                    //将返回的内容解析成图片
                    bitMap = new BitmapDrawable(iStream);
                    iStream.close();
                    iStream = null;
                    return bitMap.getBitmap();
                }
            } catch (IOException iox) {
                Log.d(TAG, iox.getMessage());
            }
        }catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        return null;
    }

}
