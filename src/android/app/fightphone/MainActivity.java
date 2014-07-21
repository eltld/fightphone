/****************************************************************************
Copyright (c) 2010-2012 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package android.app.fightphone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mainlayout);
        this.init();
    }
    private void init(){
    	registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    	getWeithAndHeight();
    	TextView MaxCpuFreq = (TextView)findViewById(R.id.MaxCpuFreq);
    	MaxCpuFreq.setText(CpuManager.getMaxCpuFreq());
    	TextView MinCpuFreq = (TextView)findViewById(R.id.MinCpuFreq);
    	MinCpuFreq.setText(CpuManager.getMinCpuFreq());
    	TextView CurCpuFreq = (TextView)findViewById(R.id.CurCpuFreq);
    	CurCpuFreq.setText(CpuManager.getCurCpuFreq());
    	TextView CpuName = (TextView)findViewById(R.id.CpuName);
    	CpuName.setText(CpuManager.getCpuName());
    	TextView TotalMemory = (TextView)findViewById(R.id.Memory);
    	TotalMemory.setText(getTotalMemory());
    	TextView AvailMemory = (TextView)findViewById(R.id.AvailMemory);
    	AvailMemory.setText(getAvailMemory());
    	getRomMemroy();
    	getSDCardMemory();
    	getVersion();
    	getInfo();
    }
    private void getWeithAndHeight(){
//    	DisplayMetrics dm = new DisplayMetrics();
//    	getWindowManager().getDefaultDisplay().getMetrics(dm);
//    	int width = dm.widthPixels;
//    	int height = dm.heightPixels;
    	
    	WindowManager mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
    	int width = mWindowManager.getDefaultDisplay().getWidth();
    	int height = mWindowManager.getDefaultDisplay().getHeight();
    	TextView Resolution = (TextView)findViewById(R.id.Resolution);
    	Resolution.setText(width+"x"+height);
    }
    private String getTotalMemory() {  
        String str1 = "/proc/meminfo";// 系统内存信息文件   
        String str2;  
        String[] arrayOfString;  
        long initial_memory = 0;  
      
        try {  
        FileReader localFileReader = new FileReader(str1);  
        BufferedReader localBufferedReader = new BufferedReader(  
        localFileReader, 8192);  
        str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小   
      
        arrayOfString = str2.split("\\s+");  
//        for (String num : arrayOfString) {  
//        Log.i(str2, num + "\t");  
//        }  
      
        initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte   
        localBufferedReader.close();  
      
        } catch (IOException e) {  
        }  
        return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化   
    }
    private String getAvailMemory() {// 获取android当前可用内存大小     
        
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);    
        MemoryInfo mi = new MemoryInfo();    
        am.getMemoryInfo(mi);    
        //mi.availMem; 当前系统的可用内存     
    
        return Formatter.formatFileSize(getBaseContext(), mi.availMem);// 将获取的内存大小规格化     
    }
    
    public long[] getRomMemroy() {  
        long[] romInfo = new long[2];  
        //Total rom memory  
        romInfo[0] = getTotalInternalMemorySize();  
        System.out.println("My Device's TotalRommemory is "+Formatter.formatFileSize(getBaseContext(), romInfo[0]));
        TextView RomMemroy = (TextView)findViewById(R.id.RomMemory);
        RomMemroy.setText(Formatter.formatFileSize(getBaseContext(), romInfo[0]));
        //Available rom memory  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
        romInfo[1] = blockSize * availableBlocks;  
        System.out.println("My Device's Rommemory is "+Formatter.formatFileSize(getBaseContext(), romInfo[1]));
        TextView AvailRomMemroy = (TextView)findViewById(R.id.AvailRomMemory);
        AvailRomMemroy.setText(Formatter.formatFileSize(getBaseContext(), romInfo[1]));
       // getVersion();  
        return romInfo;  
    } 
    public long getTotalInternalMemorySize() {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return totalBlocks * blockSize;  
    }
    
    public long[] getSDCardMemory() {  
        long[] sdCardInfo=new long[2];  
        String state = Environment.getExternalStorageState();  
        if (Environment.MEDIA_MOUNTED.equals(state)) {  
            File sdcardDir = Environment.getExternalStorageDirectory();  
            StatFs sf = new StatFs(sdcardDir.getPath());  
            long bSize = sf.getBlockSize();  
            long bCount = sf.getBlockCount();  
            long availBlocks = sf.getAvailableBlocks();  
   
            sdCardInfo[0] = bSize * bCount;//总大小  
            System.out.println("My Device's SDCardMemory is "+Formatter.formatFileSize(getBaseContext(), sdCardInfo[0]));
            TextView SDCardMemroy = (TextView)findViewById(R.id.SDMemory);
            SDCardMemroy.setText(Formatter.formatFileSize(getBaseContext(), sdCardInfo[0]));
            sdCardInfo[1] = bSize * availBlocks;//可用大小  
            System.out.println("My Device's availSDCardMemory is "+Formatter.formatFileSize(getBaseContext(), sdCardInfo[1]));
            TextView AvailSDCardMemroy = (TextView)findViewById(R.id.AvailSDMemory);
            AvailSDCardMemroy.setText(Formatter.formatFileSize(getBaseContext(), sdCardInfo[1]));
        }  
        return sdCardInfo;  
    } 
    
    private BroadcastReceiver batteryReceiver=new BroadcastReceiver(){  
    	 
        @Override 
        public void onReceive(Context context, Intent intent) {  
            int level = intent.getIntExtra("level", 0);  
            System.out.println("My CurBattery is "+level+"%");
            //  level加%就是当前电量了  
    }  
    };
    
    public String[] getVersion(){  
        String[] version={"null","null","null","null"};  
        String str1 = "/proc/version";  
        String str2;  
        String[] arrayOfString;  
        try {  
            FileReader localFileReader = new FileReader(str1);  
            BufferedReader localBufferedReader = new BufferedReader(  
                    localFileReader, 8192);  
            str2 = localBufferedReader.readLine();  
            arrayOfString = str2.split("\\s+");  
            version[0]=arrayOfString[2];//KernelVersion  
            localBufferedReader.close();  
        } catch (IOException e) {  
        }  
        version[1] = Build.VERSION.RELEASE;// firmware version  
        System.out.println("My Device's firmware version is "+version[1]);
        TextView AndroidVersion = (TextView)findViewById(R.id.AndroidVersion);
        AndroidVersion.setText(version[1]);
        version[2]=Build.MODEL;//model  
        System.out.println("My Device's model version is "+version[2]);
        version[3]=Build.DISPLAY;//system version  
        System.out.println("My Device's system version is "+version[3]);
        return version;  
    } 
    
    private void getInfo() {  
        TelephonyManager mTm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);  
         String imei = mTm.getDeviceId();  
         String imsi = mTm.getSubscriberId();  
         String mtype = android.os.Build.MODEL; // 手机型号  
         String mtyb= android.os.Build.BRAND;//手机品牌  
         String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得  
         Log.i("text", "手机IMEI号："+imei+"手机IESI号："+imsi+"手机型号："+mtype+"手机品牌："+mtyb+"手机号码"+numer);  
         TextView AndroidImei = (TextView)findViewById(R.id.AndroidImei);
         AndroidImei.setText(imei);
         TextView AndroidIesi = (TextView)findViewById(R.id.AndroidIesi);
         AndroidIesi.setText(imsi);
         TextView PhoneType = (TextView)findViewById(R.id.PhoneType);
         PhoneType.setText(mtype);
     } 
    @Override
    protected void onDestroy() {
    	unregisterReceiver(batteryReceiver);
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }
    
}
