package com.example.dell.wordsapp5;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends Activity implements View.OnClickListener {
    private String BaiduTrans = "http://openapi.baidu.com/public/2.0/bmt/translate";
    private String Client_id = "S7874h7McC7avmbWtPFkCOgc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
        initView();
    }

    public void initView() {
        findViewById(R.id.btn_trans).setOnClickListener(this);
    }

    private Handler insHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub  
            switch (msg.what) {
                case 0:
                    String word = msg.getData().getString("word");
                    ((EditText) findViewById(R.id.et_second)).setText(word);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     *   
     *      * 翻译  
     *      
     */
    private void transEnTo() {
        // path: http://fanyi.baidu.com/#en/zh/  
        String putword = ((EditText) findViewById(R.id.et_first)).getText().toString();
        try {
            // 对中文字符进行编码,否则传递乱码  
            putword = URLEncoder.encode(putword, "utf-8");
            URL url = new URL(BaiduTrans + "?client_id=" + Client_id + "&q=" + putword + "&from=auto&to=zh");
            URLConnection con = url.openConnection();
            con.connect();
            InputStreamReader reader = new InputStreamReader(
                    con.getInputStream());
            BufferedReader bufread = new BufferedReader(reader);
            StringBuffer buff = new StringBuffer();
            String line;
            while ((line = bufread.readLine()) != null) {
                buff.append(line);
            }
            // 对字符进行解码  
            String back = new String(buff.toString().getBytes("ISO-8859-1"), "UTF-8");
            String str = JsonToString(back);
            Message msg = new Message();
            msg.what = 0;
            Bundle bun = new Bundle();
            bun.putString("word", str);
            msg.setData(bun);
            insHandler.sendMessage(msg);
            reader.close();
            bufread.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
    }

    /**
     *   
     *      * 获取jsoon中翻译的内容  
     *      *   
     *      * @param jstring  
     *      * @return  
     *      
     */
    private String JsonToString(String jstring) {
        try {
            JSONObject obj = new JSONObject(jstring);
            JSONArray array = obj.getJSONArray("trans_result");
            obj = array.getJSONObject(0);
            String word = obj.getString("dst");
            return word;
        } catch (JSONException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        return "";
    }

    /**
     *   
     *      * 访问网络线程  
     *      
     */
    private void tranThread() {
        new Thread() {
            public void run() {
                transEnTo();
            };
        }.start();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub  
        switch (v.getId()) {
            case R.id.btn_trans:
                tranThread();
                break;
            default:
                break;
        }
    }
}