package com.whn.whn.slidemenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.whn.whn.slidemenu.R.id.iv_head;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.menu_listview)
    ListView menuListview;
    @InjectView(iv_head)
    ImageView ivHead;
    @InjectView(R.id.main_listview)
    ListView mainListview;
    @InjectView(R.id.slideMenu)
    SlideMenu slideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //填充数据
        mainListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES));
        menuListview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            //偷梁换柱
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);
                return tv;
            }
        });

        /**
         * 接口回调
         */
        slideMenu.setonSlideChangeListener(new SlideMenu.onSlideChangeListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this, "open", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDragint(float fraction) {
                //执行旋转动画
                ivHead.setRotation(720 * fraction);
            }
        });
    }
}
