package io.github.s23hype.advancedpowermenu;
import android.app.AndroidAppHelper;
import android.app.KeyguardManager;
import android.content.*;
import android.os.Build;
import android.os.SystemClock;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.content.pm.PackageManager;
import android.util.Log;
import java.lang.reflect.*;
import java.util.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
public final class PowerHook implements IXposedHookLoadPackage {
 private static final String PKG="io.github.s23hype.advancedpowermenu", NS="com.samsung.android.globalactions.";
 private static final String TAG="AdvancedPowerMenu";
 private static final Set<String> KNOWN=Set.of("getActionInfo","setActionInfo","onPress","onPressSecureConfirm","onLongPress","getState","setState","getIcon","setIcon","getText","setText","setIntent","setIntentAction","showBeforeProvisioning","isAvailableShow","updateState","showTipPopup","dismissTipPopup");
 private static boolean unlocked(Context c){KeyguardManager k=c.getSystemService(KeyguardManager.class);return k!=null&&!k.isDeviceLocked()&&!k.isKeyguardLocked();}
 @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam p){
  if(!"com.android.systemui".equals(p.packageName)||!p.packageName.equals(p.processName)||!"samsung".equalsIgnoreCase(Build.MANUFACTURER))return;
  if(Build.VERSION.SDK_INT<36||Build.VERSION.SDK_INT>37){XposedBridge.log(TAG+": unsupported SDK "+Build.VERSION.SDK_INT);return;}
  try {
   ClassLoader cl=p.classLoader;
   Class<?> presenter=Class.forName(NS+"presentation.SamsungGlobalActionsPresenter",false,cl);
   Class<?> model=Class.forName(NS+"presentation.viewmodel.ActionViewModel",false,cl);
   Class<?> info=Class.forName(NS+"presentation.viewmodel.ActionInfo",false,cl);
   if(!model.isInterface()){XposedBridge.log(TAG+": action model is not an interface");return;}
   for(Method m:model.getMethods())if(!KNOWN.contains(m.getName())){XposedBridge.log(TAG+": unsupported interface method "+m.getName());return;}
   Method valid=presenter.getMethod("getValidActions");
   if(!List.class.isAssignableFrom(valid.getReturnType()))return;
   Method getInfo=model.getMethod("getActionInfo"), getName=info.getMethod("getName"), getIcon=info.getMethod("getIcon"), getType=info.getMethod("getViewType"), getState=model.getMethod("getState");
   Method setName=info.getMethod("setName",String.class),setLabel=info.getMethod("setLabel",String.class),setIcon=info.getMethod("setIcon",int.class),setType=info.getMethod("setViewType",getType.getReturnType());
   Method setDesc=info.getMethod("setDescription",String.class),setStateLabel=info.getMethod("setStateLabel",String.class);
   Method dismiss=presenter.getMethod("dismissDialog",boolean.class);
   Method confirm=presenter.getMethod("confirmAction",model);
   Method confirming=presenter.getMethod("isActionConfirming");
   Field selected=presenter.getDeclaredField("mActionConfirming"); selected.setAccessible(true);
   if(selected.getType()!=model)return;
   // Only the custom action's icon is touched, including its native confirmation view.
   Class<?> item=Class.forName(NS+"presentation.view.GlobalActionsContentItemView",false,cl);
   Class<?> factory=Class.forName(NS+"presentation.view.ResourceFactory",false,cl);
   Class<?> resourceType=Class.forName(NS+"presentation.view.ResourceType",false,cl);
   Field vm=item.getDeclaredField("mViewModel"), rf=item.getDeclaredField("mResourceFactory");
   vm.setAccessible(true);rf.setAccessible(true);
   Object iconType=resourceType.getField("ID_ICON").get(null);
   Method resourceGet=factory.getMethod("get",resourceType);
   Method attrs=item.getMethod("setViewAttrs",View.class,boolean.class);
   XposedBridge.hookMethod(attrs,new XC_MethodHook(){
    @Override protected void afterHookedMethod(MethodHookParam h){
     if(h.hasThrowable())return;
     try{
      Object action=vm.get(h.thisObject); if(!model.isInstance(action))return;
      Object ai=getInfo.invoke(action);if(ai==null||!"apm_soft_restart".equals(getName.invoke(ai)))return;
      View root=(View)h.args[0]; int id=(Integer)resourceGet.invoke(rf.get(h.thisObject),iconType);
      View target=root.findViewById(id);
      if(target instanceof ImageView){
       ImageView image=(ImageView)target;
       Drawable originalIcon=image.getDrawable();
       int w=originalIcon!=null?originalIcon.getIntrinsicWidth():-1;
       int ih=originalIcon!=null?originalIcon.getIntrinsicHeight():-1;
       int fallback=Math.round(56*root.getResources().getDisplayMetrics().density);
       image.setScaleType(ImageView.ScaleType.FIT_CENTER);
       image.setImageDrawable(new SoftIcon(w>0?w:fallback,ih>0?ih:fallback));
      }
     }catch(Throwable e){XposedBridge.log(TAG+": icon unchanged: "+e);}
    }
   });
   XposedBridge.hookMethod(valid,new XC_MethodHook(){
    private boolean failed;
    @Override protected void afterHookedMethod(MethodHookParam h){
     if(failed||h.hasThrowable()||!(h.getResult() instanceof List<?>))return;
     try{
      Context c=AndroidAppHelper.currentApplication(); if(c==null||!unlocked(c)){XposedBridge.log(TAG+": no unlocked SystemUI context");return;}
      if(c.checkSelfPermission("android.permission.STATUS_BAR_SERVICE")!=PackageManager.PERMISSION_GRANTED){XposedBridge.log(TAG+": SystemUI lacks restart handoff permission");return;}
      Intent launch=new Intent().setClassName(PKG,PKG+".NativeRestartActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
      // Explicit component; package visibility can hide it from resolveActivity.
      List<?> nativeList=(List<?>)h.getResult(); if(nativeList.size()<2||nativeList.size()>16){XposedBridge.log(TAG+": unsupported action count "+nativeList.size());return;}
      Object restart=null, original=null; StringBuilder names=new StringBuilder();
      for(Object action:nativeList){if(!model.isInstance(action))return; Object ai=getInfo.invoke(action); if(ai==null)return; String name=(String)getName.invoke(ai);names.append(name).append(" ");if("apm_soft_restart".equals(name))return;if("restart".equals(name)){restart=action;original=ai;}}
      if(restart==null||original==null){XposedBridge.log(TAG+": no restart in "+names);return;}
      Object viewType=getType.invoke(original); int icon=(Integer)getIcon.invoke(original); if(viewType==null||icon<=0){XposedBridge.log(TAG+": restart missing native view type/icon");return;}
      int central=0; for(Object a:nativeList){Object ai=getInfo.invoke(a); if(viewType.equals(getType.invoke(ai)))central++;} if(central<1||central>6){XposedBridge.log(TAG+": unsupported central action count "+central);return;}
      Object state=getState.invoke(restart), custom=info.getConstructor().newInstance();
      setName.invoke(custom,"apm_soft_restart");setLabel.invoke(custom,"Soft restart");setDesc.invoke(custom,"Tap again to soft restart your phone");setStateLabel.invoke(custom,"");setIcon.invoke(custom,icon);setType.invoke(custom,viewType);
      final Object owner=h.thisObject; final long[] armed={0}; final boolean[] fired={false};
      Object proxy=Proxy.newProxyInstance(model.getClassLoader(),new Class<?>[]{model},(self,m,args)->{
       switch(m.getName()){
        case "getActionInfo":return custom;
        case "getState":return state;
        case "isAvailableShow":return true;
        case "showBeforeProvisioning":return false;
        case "hashCode":return System.identityHashCode(self);
        case "equals":return args!=null&&args.length==1&&self==args[0];
        case "toString":return "AdvancedPowerMenu";
        case "onPress":
         try{
          if(!unlocked(c)||fired[0])return null;
          long now=SystemClock.elapsedRealtime();
          Object current=selected.get(owner);
          if(Boolean.TRUE.equals(confirming.invoke(owner))){
           if(current!=self||armed[0]==0||now-armed[0]<450)return null;
           if(now-armed[0]>60000){armed[0]=0;return null;}
           fired[0]=true;armed[0]=0;
           dismiss.invoke(owner,false);
           c.startActivity(new Intent(launch));
          }else{
           armed[0]=now;
           confirm.invoke(owner,self);
           if(selected.get(owner)!=self)armed[0]=0;
          }
         }catch(Throwable e){armed[0]=0;Log.w(TAG,"Action skipped",e);XposedBridge.log(e);}return null;
        case "onPressSecureConfirm":case "onLongPress":return null;
        default:return null;
       }
      });
      ArrayList<Object> copy=new ArrayList<>(nativeList); copy.add(proxy); h.setResult(copy); XposedBridge.log(TAG+": added Soft restart to "+names);
     }catch(Throwable e){failed=true;Log.w(TAG,"Power hook disabled for this process",e);XposedBridge.log(e);}
    }
   });
   XposedBridge.log(TAG+": installed power menu hook; SDK "+Build.VERSION.SDK_INT);
  }catch(Throwable e){Log.w(TAG,"Unsupported power menu; left unchanged",e);XposedBridge.log(e);}
 }
}
