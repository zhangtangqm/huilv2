package cn.edu.swufe.huilv2;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {//通过父类ListActivity创建列表，ListActivity用于显示列表界面
String data []={"waiting....."};
Handler handler;
    private final String TAG="RateListActivity";
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);因为父类中已经含有布局，所以不需要用布局去填充它

        List<String> list1=new ArrayList<String>();//列表没有长度限制
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        ListAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);//android.R.layout.simple_list_item_1表示布局，适配器将布局和数据联系起来，ArrayAdapter为数组适配器
        setListAdapter(adapter);//把当前界面用adapter进行管理
        Thread t=new Thread(this);
        t.start();

        handler=new Handler(){
         public void handleMessage(Message msg){
             if(msg.what==5){
                 List<String> list2= (List<String>) msg.obj;
                 ListAdapter adapter=new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);//android.R.layout.simple_list_item_1表示布局
                 setListAdapter(adapter);//把当前界面用adapter进行管理
             }



             super.handleMessage(msg);
         }
        };
    }

    @Override
    public void run() {
//获取网络数据，放入list中带回到主线程当中
List<String> retList=new ArrayList<String>();
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj").get();
            Log.i(TAG,"run:"+doc.title());
            Elements tables= doc.getElementsByTag("table");//使用标签获得元素

            /*使用for循环找到是哪个table
            int i=1;
            for(Element table:tables){
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/
            Element table=tables.get(1);
            Log.i(TAG,"run:table="+table);
            //获取td中的元素
            Elements tds= doc.getElementsByTag("td");//使用标签获得元素
            for(int i=0;i<tds.size()-8;i+=8){//根据网页源代码发现，每8个td一个循环
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                //过滤出需要的数据
               retList.add(td1.text()+"==>"+td2.text());

            }
          /* for(Element td:tds){
                Log.i(TAG,"run:td="+td);
                Log.i(TAG,"run:text="+td.text());
                Log.i(TAG,"run:html="+td.html());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //bundle中保存获取的数据
        //获取msg对象，用于返回主线程
        Message msg=handler.obtainMessage(5);
        //msg.what=5;//what用于标记当前Message的属性
        msg.obj=retList;
        handler.sendMessage(msg);

    }
}
