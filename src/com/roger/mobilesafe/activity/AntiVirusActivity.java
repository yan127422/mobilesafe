package com.roger.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.roger.mobilesafe.R;
import com.roger.mobilesafe.db.dao.AntiVirusDao;
import com.roger.mobilesafe.domain.ScanInfo;
import com.roger.mobilesafe.ui.HoloCircularProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by Roger on 2014/11/12.
 * 手机杀毒
 */
public class AntiVirusActivity extends Activity{
    private static final int SCANNING = 0;//正在扫描
    private static final int FINISH = 1;//扫描完成
    private HoloCircularProgressBar hcp_progerss;
    private TextView tv_progerss,//进度百分比
                     tv_scanning;//扫描信息
    private LinearLayout ll_container;//扫描总信息
    private float progress;//进度：0-1
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        pm = getPackageManager();
        hcp_progerss = (HoloCircularProgressBar) findViewById(R.id.hcp_progress);
        tv_progerss = (TextView) findViewById(R.id.tv_progress);
        tv_scanning = (TextView) findViewById(R.id.tv_scanning);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        hcp_progerss.setProgress(progress);
        scanVirus();
        tv_progerss.setClickable(false);
        tv_progerss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_container.removeAllViews();
                tv_progerss.setClickable(false);
                scanVirus();
            }
        });

    }
    private Handler scanHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANNING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scanning.setText("正在扫描："+scanInfo.getName());
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText(scanInfo.isVirus() ?
                            "发现病毒：" + scanInfo.getName()
                            : "扫描安全：" + scanInfo.getName());
                    tv.setTextColor(scanInfo.isVirus() ? Color.RED : Color.BLACK);
                    ll_container.addView(tv,0);

                    hcp_progerss.setProgress(progress);
                    tv_progerss.setText(((int)(progress*100))+"%");
                    break;
                case FINISH:
                    tv_scanning.setText("扫描完毕");
                    tv_progerss.setText("开始");
                    tv_progerss.setClickable(true);
                    break;
            }
        }
    };
    /**
     * 扫描病毒
     */
    private void scanVirus() {
        new Thread(){
            @Override
            public void run() {
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
                float size = packageInfos.size();
                int n = 0;
                for(PackageInfo info:packageInfos){
                    Message message = Message.obtain();
                    message.what = SCANNING;
                    n++;
                    progress = n/size;
                    String md5 = getFileMd5(info.applicationInfo.sourceDir);
                    ScanInfo scanInfo = new ScanInfo();
                    scanInfo.setVirus(AntiVirusDao.isVirus(md5));
                    scanInfo.setName(info.applicationInfo.loadLabel(pm).toString());
                    message.obj = scanInfo;
                    scanHandler.sendMessage(message);
                }
                Message message = Message.obtain();
                message.what = FINISH;
                scanHandler.sendMessage(message);
            }
        }.start();

    }
    /**
     * 获取文件的md5值
     * @param path 文件的全路径名称
     * @return
     */
    private String getFileMd5(String path){
        try {
            // 获取一个文件的特征信息，签名信息。
            File file = new File(path);
            // md5
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb  = new StringBuffer();
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                // System.out.println(str);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
