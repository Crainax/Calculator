package com.ruffneck.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruffneck.calculator.view.NoScrollGridView;

public class MainActivity extends AppCompatActivity {

    private NoScrollGridView gridView;

    KeyAdapter adapter;

    private int height = -2;

    private String[] keys = new String[]{"C", "Del", "(", ")",
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            ".", "0", "=", "+"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (NoScrollGridView) findViewById(R.id.gv);

//        System.out.println("layoutParams = " + layoutParams);
//        Toast.makeText(MainActivity.this, layoutParams.width+","+layoutParams.height, Toast.LENGTH_SHORT).show();

//得到设备的大小


        initGridView();
        //添加适配器.

//        setListViewHeightBasedOnChildren(gridView);


//        adapter.notifyDataSetChanged();
    }


    /**
     * 设置好GridView的适配.异步去循环获取.
     */
    private void initGridView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    height = gridView.getHeight();
                    if (height > 0) {
                        gridView.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new KeyAdapter();
                                gridView.setAdapter(adapter);
                            }
                        });
                        break;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /*public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);

    }*/


    class KeyAdapter extends BaseAdapter {

        /*
                int deviceWidth;
                int deviceHeight;

                public KeyAdapter() {
                    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    Display display = windowManager.getDefaultDisplay();

                    deviceWidth = display.getWidth();
                    deviceHeight = display.getHeight();
                }
        */
        int raw;

        public KeyAdapter() {
            int count = gridView.getNumColumns();
            if (keys.length % count == 0)
                raw = keys.length / count;
            else
                raw = keys.length / count + 1;
        }

        @Override
        public int getCount() {
            return keys.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.item_key, null);


//            int height = deviceHeight * 5 / 7 / 5;//此处的高度需要动态计算
//            int width = deviceWidth / 4 - 4; //此处的宽度需要动态计算
            ViewGroup.LayoutParams paras = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , height / raw - 1);
            view.setLayoutParams(paras); //使设置好的布局参数应用到控件

//            Log.e("Main", paras.height + "");

            TextView tv_key = (TextView) view.findViewById(R.id.tv_key);
            tv_key.setText(keys[position]);
            return view;
        }
    }

}
