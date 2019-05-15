package cn.edu.swufe.huilv2;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Runnable{
private final String TAG="MainActivity";
   private float dollarRate=0.1f;
    private float euroRate=0.2f;
    private float wonRate=0.3f;
    private String updateDate="";//定义空串，后面设置时间更新做准备
    private Object btn;
    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rmb=(EditText)findViewById(R.id.editText1);
        show=(TextView)findViewById(R.id.textView);
        //获取sharedPreferences中的数据，SharedPreferences只用于保存少量数据
        SharedPreferences sharedpreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);//getSharedPreferences(文件名，访问权限)
        SharedPreferences sp=  PreferenceManager.getDefaultSharedPreferences(this);//默认只有一个存取数据的文件
        dollarRate= sharedpreferences.getFloat("dollar_rate",0.1f);
        euroRate= sharedpreferences.getFloat("euro_rate",0.2f);
        wonRate= sharedpreferences.getFloat("won_rate",0.3f);
        updateDate=sharedpreferences.getString("update_date","");

        //获取当前系统时间
        Date today=Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr=sdf.format(today);

        Log.i(TAG,"onCreate:sp dollarRate="+dollarRate);
        Log.i(TAG,"onCreate:sp euroRate="+euroRate);
        Log.i(TAG,"onCreate:sp wonRate="+wonRate);
        Log.i(TAG,"onCreate:sp updateDate="+updateDate);

        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG,"onCreate:需要更新");
        //开启子线程
            Thread t=new Thread(this);//t表示子线程
            t.start();
        }
        else{
            Log.i(TAG,"onCreate:不需要更新");
        }



        handler=new Handler() {

            public void handleMessage(Message msg) {
                if(msg.what==5){
                   Bundle bdl=(Bundle)msg.obj;
                   dollarRate= bdl.getFloat("dollar_rate");
                    euroRate= bdl.getFloat("euro_rate");
                    wonRate= bdl.getFloat("won_rate");

                    Log.i(TAG,"handleMessage:dollarRate:"+dollarRate);
                    Log.i(TAG,"handleMessage:euroRate:"+euroRate);
                    Log.i(TAG,"handleMessage:wonRate:"+wonRate);


                    //保存更新的日期
                    SharedPreferences sharedpreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);//getSharedPreferences(文件名，访问权限)，写入数据会自动创建myrate文件，注意保存数据的文件名和读取数据的文件名相同
                    SharedPreferences.Editor editor=sharedpreferences.edit();
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.putString("update_date",todayStr);
                    editor.apply();//保存

                    Toast.makeText(MainActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }
    public void onClick(View btn){
        String str=rmb.getText().toString();
        float r=Float.valueOf(str);
        if(str.length()==0){
            Toast.makeText(this,"请输入想要转换的数值",Toast.LENGTH_SHORT).show();
        }
else {
            //计算汇率
            if (btn.getId() == R.id.button1) {
                show.setText(String.format("%.2f", r * dollarRate));
            } else if (btn.getId() == R.id.button2) {
                show.setText(String.format("%.2f", r * euroRate));
            }
            if (btn.getId() == R.id.button3) {
                show.setText(String.format("%.2f", r * wonRate));
            }
        }

    }
    public void openOne(View btn){
      Intent config=new Intent(this,activity_config.class);
        config.putExtra("dollar_rate",dollarRate);//通过附加值的方式传递参数到下一个页面
        config.putExtra("ero_rate",euroRate);
        config.putExtra("won_rate",wonRate);
        Log.i(TAG,"openOne:dollar_rate"+dollarRate);//获悉是否传递参数成功
        Log.i(TAG,"openOne:euro_rate"+euroRate);
        Log.i(TAG,"openOne:won_rate"+wonRate);


        startActivityForResult(config,1);//打开新的页面，并在页面关闭后会向前面的activity传递数据，1为请求代码

     // Intent config=new Intent(this,activity_config.class);
      //startActivity(config);
    }

//菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }
    //点击下拉菜单中的选项


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /*if(item.getItemId()==R.id.menu_set){
          public void open(View btn){
           private void openOne() {
                   Intent config = new Intent(this, activity_config.class);
                   config.putExtra("dollar_rate", dollarRate);//通过附加值的方式传递参数到下一个页面
                   config.putExtra("ero_rate", euroRate);
                   config.putExtra("won_rate", wonRate);
                   Log.i(TAG, "openOne:dollar_rate" + dollarRate);//获悉是否传递参数成功
                   Log.i(TAG, "openOne:euro_rate" + euroRate);//获悉是否传递参数成功
                   Log.i(TAG, "openOne:won_rate" + wonRate);//获悉是否传递参数成功


                   startActivityForResult(config, 1);//打开新的页面，并在页面关闭后会向前面的activity传递数据，1为请求代码
               }
           }
           }
       */
         if(item.getItemId()==R.id.open_list){
             //打开列表窗口
             Intent list = new Intent(this, MyList2Activity.class);

           startActivity(list);


       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1&&resultCode==2){
            Bundle bundle=data.getExtras();
            dollarRate=bundle.getFloat("key_dollar",0.1f);
            euroRate=bundle.getFloat("key_euro",0.1f);
            wonRate=bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult:dollar_rate"+dollarRate);
            Log.i(TAG,"onActivityResult:euro_rate"+euroRate);
            Log.i(TAG,"onActivityResult:won_rate"+wonRate);
            //将新设置的数据写入sharedPreferences中的数据
            SharedPreferences sharedpreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);//getSharedPreferences(文件名，访问权限)，写入数据会自动创建myrate文件，注意保存数据的文件名和读取数据的文件名相同
            SharedPreferences.Editor editor=sharedpreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();//保存
            Log.i(TAG,"onActivityResult:数据已保存到sharedpreferences");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run( ){
        for (int i=1;i<6;i++) {
            //接入Runnable接口，只有run方法可以用
            Log.i(TAG, "run:run().....");

            try {
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
            //用于保存获取的汇率
            Bundle bundle=new Bundle();


        //获取网络数据
        /*URL url=null;
        try {//通过网络地址将源代码转换为字符串，有了doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();就可以不要
             url=new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http= (HttpURLConnection) url.openConnection();
            InputStream in=http.getInputStream();//获得一个输入流，获得网络数据，获得的是网页源代码
            String html=inputStream2String(in);
            Log.i(TAG,"run:html="+html);
            Document doc=Jsoup.parse(html);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
//等同于doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get()


        //jsoup
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run:"+doc.title());
           Elements tables= doc.getElementsByTag("table");//使用标签获得元素

            /*使用for循环找到是哪个table
            int i=1;
            for(Element table:tables){
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/
            Element table=tables.get(0);
            Log.i(TAG,"run:table="+table);
            //获取td中的元素
            Elements tds= doc.getElementsByTag("td");//使用标签获得元素
            for(int i=0;i<tds.size()-5;){//根据网页源代码发现，每8个td一个循环
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
               //过滤出需要的数据
                String str1=td1.text();
                String val=td2.text();
                if("美元".equals(str1)){
                    bundle.putFloat("dollar_rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro_rate",100f/Float.parseFloat(val));
                }else if("韩元".equals(str1)){
                    bundle.putFloat("won_rate",100f/Float.parseFloat(val));
                }
                i+=6;
            }
          /* for(Element td:tds){
                Log.i(TAG,"run:td="+td);
                Log.i(TAG,"run:text="+td.text());
                Log.i(TAG,"run:html="+td.html());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        //bundle中保存获取的数据
        //获取msg对象，用于返回主线程
        Message msg=handler.obtainMessage(5);
        //msg.what=5;//what用于标记当前Message的属性
        msg.obj=bundle;
        handler.sendMessage(msg);


    }
    //转换网页源代码
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize=1024;
        final char[] buffer=new char[bufferSize];
        final StringBuilder out=new StringBuilder();
        Reader in=new InputStreamReader(inputStream , "gb2312");//此处显示中文的方式应和访问的网站的编码配置方式相同
        for(;;){
            int rsz=in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }

}

