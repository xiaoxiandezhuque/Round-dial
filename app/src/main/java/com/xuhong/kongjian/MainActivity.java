package com.xuhong.kongjian;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YuanPanView  view = (YuanPanView) findViewById(R.id.view);
        view.setOnClickListener(new YuanPanView.OnClickListener() {
            @Override
            public void onClick(int num) {
                Toast.makeText(MainActivity.this,num+"",Toast.LENGTH_SHORT).show();
                Log.e("aaa",num+"");
            }
        });
    }
}
