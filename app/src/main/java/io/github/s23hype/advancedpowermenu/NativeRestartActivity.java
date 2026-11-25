package io.github.s23hype.advancedpowermenu;
import android.os.Build;
/** Explicit, permission-protected handoff after the native SystemUI confirmation. */
public final class NativeRestartActivity extends ConfirmActivity {
 @Override protected boolean nativeConfirmed(){return true;}
 @Override protected boolean authorizedNativeCaller(){
  if(Build.VERSION.SDK_INT<35)return false;
  try{boolean ok=getInitialCaller()!=null&&"com.android.systemui".equals(getInitialCaller().getPackage())&&getInitialCaller().getUid()>=0;
   android.util.Log.i("AdvancedPowerMenu","Native caller authorized: "+ok);return ok;}
  catch(Throwable e){android.util.Log.w("AdvancedPowerMenu","Caller verification failed",e);return false;}
 }
}
