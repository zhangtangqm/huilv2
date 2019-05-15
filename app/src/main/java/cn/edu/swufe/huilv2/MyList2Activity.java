package cn.edu.swufe.huilv2;

import android.app.Activity;
import android.app.ListActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable ,AdapterView.OnItemClickListener ,AdapterView.OnItemLongClickListener {
    private List<HashMap<String, String>> listItems;//存放文字，图片信息
    private SimpleAdapter listItemAdapter;//适配器
    private final String TAG = "MyList2Activity";
    Handler handler;

    public MyList2Activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();
        this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {

            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    listItems = (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this, listItems,//listItems数据源
                            R.layout.list_item,//listItem的XML布局实现
                            new String[]{"ItemTitle", "ItemDetail"}, new int[]{R.id.itemTitle, R.id.itemDetail}//用于确立数据和布局的对应关系
                    );
                    setListAdapter(listItemAdapter);//把当前界面用adapter进行管理
                }

            }

        };
        /*监听方法1
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {//构造监听，当按下item时有处理
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/
        //建立监听2
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);//长按处理
    }
        private void initListView () {
            listItems = new ArrayList<HashMap<String, String>>();//分配存储空间
            for (int i = 0; i < 10; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", "Rate:" + i);//放数据，此处为标题文字
                map.put("ItemDetail", "Detail:" + i);//详情描述
                listItems.add(map);//将map对象放入列表中
            }
            //生成适配器的Item和动态数组对应的元素
            listItemAdapter = new SimpleAdapter(this, listItems,//listItems数据源
                    R.layout.list_item,//listItem的XML布局实现
                    new String[]{"ItemTitle", "ItemDetail"}, new int[]{R.id.itemTitle, R.id.itemDetail}//用于确立数据和布局的对应关系
            );
        }

        @Override
        public void run () {
            List<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();
            Document doc = null;
            try {
                Thread.sleep(3000);
                doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj").get();
                Log.i(TAG, "run:" + doc.title());
                Elements tables = doc.getElementsByTag("table");//使用标签获得元素


                Element table = tables.get(1);
                Log.i(TAG, "run:table=" + table);
                //获取td中的元素
                Elements tds = doc.getElementsByTag("td");//使用标签获得元素
                for (int i = 0; i < tds.size() - 8; i += 8) {//根据网页源代码发现，每8个td一个循环
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "run:" + td1.text() + "==>" + td2.text());
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("ItemTitle", str1);
                    map.put("ItemDetail", val);
                    retList.add(map);


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
            Message msg = handler.obtainMessage(5);
            //msg.what=5;//what用于标记当前Message的属性
            msg.obj = retList;
            handler.sendMessage(msg);


        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//当item被点击的时候调用这个方法

        Log.i(TAG,"onItemClick:parent:"+parent);
        Log.i(TAG,"onItemClick:view:"+view);
        Log.i(TAG,"onItemClick:position:"+position);
        Log.i(TAG,"onItemClick:id:"+id);

        HashMap<String, String> map= (HashMap<String, String>) getListView().getItemAtPosition(position);//点击行获得相应的数据项，从数据项中去取数据
        String titleStr=map.get("ItemTitle");
        String detailStr=map.get("ItemDetail");
        Log.i(TAG,"onItemClick:titleStr:"+titleStr);
        Log.i(TAG,"onItemClick:detailStr:"+detailStr);

        TextView title=view.findViewById(R.id.itemTitle);//从控件中获得元素
        TextView detail=view.findViewById(R.id.itemDetail);
        String title2=String.valueOf(title.getText());
        String detail2=String.valueOf(detail.getText());

        Log.i(TAG,"onItemClick:title2:"+title2);
        Log.i(TAG,"onItemClick:detail2:"+detail2);

        //打开新的界面，传入参数
        Intent rateCalc=new Intent(this,RateCalcActivity.class);//（当前界面，即将打开的界面）
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {//在int position 前面加上final扩大position作用域，使其可以在onclick中使用
        Log.i(TAG,"onItemClick:长按列表项position:"+position);
        //删除操作
       // listItems.remove(position);
        //listItemAdapter.notifyDataSetChanged();//刷新
        //构造对话框进行确认操作
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("请确认是否删除").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//which表示是哪个按钮激发了这个处理
                Log.i(TAG,"onItemClick:对话框事件处理:");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();//刷新
            }
        }).setNegativeButton("否",null);
        builder.create().show();//创建和显示



        Log.i(TAG,"onItemClick:size:"+listItems.size());
        return true;//取决于是否屏蔽短按时间。如果为true则屏蔽

    }
}


