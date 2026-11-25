package io.github.s23hype.advancedpowermenu;
import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
public final class ConfirmActivity extends Activity {
 private static final AtomicBoolean BUSY=new AtomicBoolean();
 private volatile boolean resumed;
 private AlertDialog dialog;
 private boolean unlocked(){ KeyguardManager k=getSystemService(KeyguardManager.class); return k!=null&&!k.isDeviceLocked()&&!k.isKeyguardLocked(); }
 @Override public void onCreate(Bundle b){super.onCreate(b);
  if(!unlocked()){finish();return;}
  dialog=new AlertDialog.Builder(this).setTitle("Soft restart?").setMessage("Android will restart its userspace and close running apps. Save your work first. Do not continue if KernelSU or your device is unstable.").setNegativeButton("Cancel",(d,w)->finish()).setPositiveButton("Soft restart",null).create();
  dialog.setOnCancelListener(d->finish());dialog.show();dialog.getButton(-1).setOnClickListener(v->request());
  dialog.getButton(-1).setFilterTouchesWhenObscured(true);
 }
 @Override protected void onResume(){super.onResume();resumed=true;}
 @Override protected void onPause(){resumed=false;super.onPause();}
 @Override protected void onDestroy(){if(dialog!=null)dialog.dismiss();super.onDestroy();}
 private int shell(String cmd,long seconds)throws Exception{
  Process p=new ProcessBuilder("su","-c",cmd).redirectErrorStream(true).redirectOutput(new java.io.File("/dev/null")).start();
  try {if(!p.waitFor(seconds,TimeUnit.SECONDS)){p.destroyForcibly();return -1;} return p.exitValue();}finally{p.destroy();}
 }
 private void request(){
  if(!resumed||!unlocked()||!BUSY.compareAndSet(false,true))return;
  long now=SystemClock.elapsedRealtime(); long last=getPreferences(0).getLong("last_attempt",-1);
  if(last>=0&&now>=last&&now-last<30000){BUSY.set(false);Toast.makeText(this,"Please wait before retrying",Toast.LENGTH_SHORT).show();return;}
  dialog.getButton(-1).setEnabled(false);
  new Thread(()->{
   String result="Restart was not started. Root or KernelSU is unavailable.";
   try{
    int pre=shell("test \"$(id -u)\" = 0 && test -x /data/adb/ksud && /data/adb/ksud --help | grep -q soft-reboot",20);
    if(pre==0&&resumed&&unlocked()&&!isFinishing()&&!isDestroyed()){
     if(!getPreferences(0).edit().putLong("last_attempt",SystemClock.elapsedRealtime()).commit()) throw new IllegalStateException("Cannot save rate limit");
     int code=shell("test \"$(id -u)\" = 0 && exec /data/adb/ksud soft-reboot",15);
     result=code==0?"Restart requested.": "Restart command did not report success. Do not retry automatically.";
    }else if(pre==0){result="Confirmation interrupted. Unlock and reopen to try again.";}
   }catch(Exception ignored){} finally{BUSY.set(false);}
   final String msg=result;
   runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()){Toast.makeText(this,msg,Toast.LENGTH_LONG).show();finish();}});
  },"apm-restart").start();
 }
}
