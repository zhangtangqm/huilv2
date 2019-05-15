package cn.edu.swufe.huilv2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//自己定义Adapter
public class MyAdapter extends ArrayAdapter {
private static final String TAG="MyAdapter";
public MyAdapter(Context context, int resource, ArrayList<HashMap<String,String>> list){
    super(context,resource,list);
}

    @Override
public View getView(int position, View convertView, ViewGroup parent){//position用于确定位置，根据不同的position返回不同的view对象，实现自定义多级列表，View代表当前view控件，ViewGroup代表父类控件
    View itemView=convertView;
    if(itemView==null){
        itemView=LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
    }//最初的时候判断是否为空
        //定义数据的取出和安放
    Map<String,String>map=(Map<String,String>)getItem(position);//获得当前行数据
    TextView title=(TextView)itemView.findViewById(R.id.itemTitle);//找到控件
    TextView detail=(TextView)itemView.findViewById(R.id.itemDetail);

    title.setText("Title:"+map.get("ItemTitle"));//将数据取出，在控件中输出，此处一行是一个map
    title.setText("detail:"+map.get("ItemDetail"));
    return itemView;//到下一行
}





}
