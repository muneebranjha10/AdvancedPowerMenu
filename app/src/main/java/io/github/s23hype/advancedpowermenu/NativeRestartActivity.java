package io.github.s23hype.advancedpowermenu;
import android.os.Build;
/** Explicit, permission-protected handoff after the native SystemUI confirmation. */
public final class NativeRestartActivity extends ConfirmActivity {
 @Override protected boolean nativeConfirmed(){return true;}
 @Override protected boolean authorizedNativeCaller(){
  if(Build.VERSION.SDK_INT<35)return false;
  try{return getInitialCaller()!=null&&"com.android.systemui".equals(getInitialCaller().getPackage());}
  catch(Throwable e){return false;}
 }
}
