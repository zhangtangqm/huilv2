package cn.edu.swufe.huilv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class activity_config extends AppCompatActivity {
    public final String TAG="activity_config";
    EditText dollarText;
    EditText euroText;
    EditText wonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent=getIntent();//获取参数

        float dollar= intent.getFloatExtra("dollar_rate",0.0f);//取出数据，dollar_rate为之前设置的标签，一定要一致
        float euro= intent.getFloatExtra("euro_rate",0.1f);
        float won= intent.getFloatExtra("won_rate",0.0f);
        Log.i(TAG,"onCreate:dollar="+dollar);
        Log.i(TAG,"onCreate:euro="+euro);


        dollarText=(EditText) findViewById(R.id.editText);
        euroText=(EditText) findViewById(R.id.editText2);
        wonText=(EditText) findViewById(R.id.editText3);
        dollarText.setText(String.valueOf(dollar));//显示数据到控件
        euroText.setText(String.valueOf(euro));
        wonText.setText(String.valueOf(won));
    }
    public void save(View v){
        Log.i("cfg","save:");
        //从文本框中获取输入新的值
        float newDollar=Float.parseFloat(dollarText.getText().toString());
        float newEuro=Float.parseFloat(euroText.getText().toString());
        float newWon=Float.parseFloat(wonText.getText().toString());
        //处理异常
        Log.i(TAG,"save:获取新的值");
        Log.i(TAG,"save:newDollar="+newDollar);
        Log.i(TAG,"save:newEuro="+newEuro);
        Log.i(TAG,"save:newWon="+newWon);
        //保存到bundle中
        Intent intent=getIntent();
        Bundle bdl=new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);//将三个数据打包放在bundle中
        setResult(2,intent);//2为响应代码
        //返回调用界面
        finish();


    }

}
