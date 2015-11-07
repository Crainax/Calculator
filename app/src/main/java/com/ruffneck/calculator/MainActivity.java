package com.ruffneck.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.ruffneck.calculator.exception.IllegalExpressionException;
import com.ruffneck.calculator.rpn.RPN;
import com.ruffneck.calculator.view.NoScrollGridView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private NoScrollGridView gridView;
    private int height = -2;

    private String[] keys = new String[]{"AC", "Del", "(", ")",
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            ".", "0", "=", "+"};
/*    private String[] keys = new String[]{"AC", "Del", "(", ")",
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            ".", "0", "π", "+",
            "Ans", "e", "^", "="};*/
    private EditText tv_result;
    private EditText tv_expression;

    StringBuilder sb;
    /**
     * 用于维护输入框的栈.
     */
    private Stack<String> expression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (NoScrollGridView) findViewById(R.id.gv);
        tv_result = (EditText) findViewById(R.id.tv_result);
        tv_expression = (EditText) findViewById(R.id.tv_expression);

        asyncInit();
        dataInit();

    }

    /**
     * 数据初始化
     */
    private void dataInit() {
        expression = new Stack<>();
        mPref = getSharedPreferences("data", MODE_PRIVATE);
    }

    /**
     * 异步去循环获取gridView的宽高.
     */
    private void asyncInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    height = gridView.getHeight();
                    if (height > 0) {
                        gridView.post(new Runnable() {
                            @Override
                            public void run() {
                                initGridView();
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

    /**
     * 初始化GridView的适配.
     */
    private void initGridView() {
        KeyAdapter adapter = new KeyAdapter();
        gridView.setAdapter(adapter);

        //适配按键事件.
       /* gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                sb = new StringBuilder(tv_expression.getText().toString().trim());

                switch (keys[position]) {
                    case "C":
                        clear();
                        break;
                    case "=":
                        calculate();
                        return;
                    case "Del":
                        if (!expression.isEmpty()) {
                            String del = expression.pop();
                            sb.delete(sb.length() - del.length(), sb.length());
                        }
                        break;
                    case ".":
                        //自动补全点号前的0
                        if (sb.length() == 0) {
                            add("0");
                        } else if (mPref.getBoolean("isNew", true)) {
                            defaultKey("0");
                        } else {
                            String before = sb.substring(sb.length() - 1, sb.length());
                            if (before != null && !RPN.isNum(before.charAt(0)))
                                add("0");
                        }

                        defaultKey(keys[position]);
                        break;
                    case "(":
                        //自动补全括号前的乘号
                        if (sb.length() > 0) {
                            String before = sb.substring(sb.length() - 1, sb.length());
                            *//*if (before != null && before.charAt(0) == '.') {
                                add("0");
                                add("×");
                            } else *//*
                            if (before != null && RPN.isNum(before.charAt(0)))
                                add("×");
                        }

                        defaultKey(keys[position]);
                        break;
                    default:
                        defaultKey(keys[position]);
                        break;
                }
                if (sb.length() > 0) {
                    tv_expression.setText(sb.toString());
                    tv_expression.setSelection(sb.length());
                } else
                    tv_expression.setText(" ");
            }
        });*/
    }


    /**
     * 默认键的处理方法.
     */
    private void defaultKey(String content) {
        if (mPref.getBoolean("isNew", true)) {
            clear();
            mPref.edit().putBoolean("isNew", false).apply();
        }
        add(content);
    }

    /**
     * 往表达式中添加字符串
     *
     * @param content 需要添加的字符串
     */
    private void add(String content) {
        sb.append(content);
        expression.push(content);
    }

    /**
     * =号里面的逻辑.
     */
    private void calculate() {
        String result = "0";
        try {

            String expre = sb.toString().replaceAll("÷", "/").replaceAll("×", "*");
            if (!RPN.isExpression(expre)) {
//            System.out.println("ERROR!!");
                throw new IllegalExpressionException();
            }

            double d_result = RPN.calculate(expre);
            //判断是否为无穷.
            if (d_result == Double.POSITIVE_INFINITY) {
                result = "+∞";
            } else if (d_result == Double.NEGATIVE_INFINITY) {
                result = "-∞";
            } else {
                result = String.valueOf(d_result);
            }
            //新开一个计算.
        } catch (RuntimeException e) {
            result = "错误";
        } finally {
            mPref.edit().putBoolean("isNew", true).apply();
            tv_result.setText(result);
        }
    }


    private String beforeCalculate(String expression) {

        return null;
    }

    /**
     * AC键逻辑
     */
    private void clear() {
        expression.clear();
        sb.setLength(0);
    }


    /**
     * 按键点击.
     * @param v
     */
    public void itemClick(View v) {

        String key = ((Button) v).getText().toString();

        sb = new StringBuilder(tv_expression.getText().toString().trim());

        switch (key) {
            case "AC":
                clear();
                break;
            case "=":
                calculate();
                return;
            case "Del":
                if (!expression.isEmpty()) {
                    String del = expression.pop();
                    sb.delete(sb.length() - del.length(), sb.length());
                }
                break;
            case ".":
                //自动补全点号前的0
                if (sb.length() == 0) {
                    add("0");
                } else if (mPref.getBoolean("isNew", true)) {
                    defaultKey("0");
                } else {
                    String before = sb.substring(sb.length() - 1, sb.length());
                    if (before != null && !RPN.isNum(before.charAt(0)))
                        add("0");
                }

                defaultKey(key);
                break;
            case "(":
                //自动补全括号前的乘号
                if (sb.length() > 0) {
                    String before = sb.substring(sb.length() - 1, sb.length());
                            /*if (before != null && before.charAt(0) == '.') {
                                add("0");
                                add("×");
                            } else */
                    if (before != null && RPN.isNum(before.charAt(0)))
                        add("×");
                }

                defaultKey(key);
                break;
            default:
                defaultKey(key);
                break;
        }
        if (sb.length() > 0) {
            tv_expression.setText(sb.toString());
            tv_expression.setSelection(sb.length());
        } else
            tv_expression.setText(" ");
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
        ViewGroup.LayoutPara
        ms params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);

    }*/


    /**
     * GridView的适配器
     */
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


            ViewGroup.LayoutParams paras = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , height / raw - 1);
            view.setLayoutParams(paras); //使设置好的布局参数应用到控件


            Button bt_key = (Button) view.findViewById(R.id.bt_key);
            bt_key.setText(keys[position]);
            //为不同的按键设置不同的点击效果
            switch (keys[position]) {
                case "=":
                case "+":
                case "-":
                case "×":
                case "÷":
                    System.out.println(keys[position] + ":main");
                    bt_key.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item_key_main));
                    break;
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                case "0":
                    System.out.println(keys[position] + ":number");
                    bt_key.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item_key_number));
                    break;
                default:
                    System.out.println(keys[position] + ":default");
                    bt_key.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item_key_others));
                    break;
            }
            return view;


        }
    }

}
