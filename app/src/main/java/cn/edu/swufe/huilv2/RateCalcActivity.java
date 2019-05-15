package cn.edu.swufe.huilv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class RateCalcActivity extends AppCompatActivity {
float rate=0f;

EditText inp2;
    EditText rmb;
String TAG="rateCalc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calc);
        //接收数据
        rate=getIntent().getFloatExtra("rate",0f);

        String title=getIntent().getStringExtra("title");
        Log.i(TAG,"onCreate:title:"+title);
        Log.i(TAG,"onCreate:rate:"+rate);

        ((TextView)findViewById(R.id.title2)).setText(title);
        inp2=(EditText)findViewById(R.id.inp2);
        inp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView show=(TextView)RateCalcActivity.this.findViewById(R.id.show2);

                if(s.length()>0){
                          float val=Float.parseFloat(s.toString());

                          show.setText(val+"RMB==>"+100/rate*val);
                }
                else{
                   show.setText("");
                }
            }
        });
    }
}
