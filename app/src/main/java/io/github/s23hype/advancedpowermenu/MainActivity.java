package io.github.s23hype.advancedpowermenu;
import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import java.io.File;
import java.util.concurrent.TimeUnit;
public final class MainActivity extends Activity {
 private static final int BG=0xff0b1020, FG=0xfff5f7ff, MUTED=0xffaab8d1, BLUE=0xff386ee8;
 private LinearLayout page; private ScrollView scroll;
 private TextView rootStatus; private Button rootButton; private boolean setup; private boolean checking;
 private final String channel="https://t.me/s23_hype";
 private int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);show(b!=null&&b.getBoolean("setup"));}
 @Override protected void onSaveInstanceState(Bundle b){b.putBoolean("setup",setup);super.onSaveInstanceState(b);}
 private GradientDrawable rounded(int color,float radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 private TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,1);t.setLineSpacing(dp(2),1f);return t;}
 private void add(View v,int gap){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(gap);page.addView(v,lp);}
 private Button button(String label,boolean primary,Runnable run){Button b=new Button(this);b.setAllCaps(false);b.setText(label);b.setTextSize(15);b.setTextColor(FG);b.setMinHeight(dp(52));b.setBackground(new android.graphics.drawable.RippleDrawable(ColorStateList.valueOf(0x33ffffff),rounded(primary?BLUE:0xff222c42,18),null));b.setOnClickListener(v->run.run());return b;}
 private void telegram(){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(channel)));}catch(ActivityNotFoundException e){Toast.makeText(this,channel,Toast.LENGTH_LONG).show();}}
 private void show(boolean guide){
  setup=guide;rootStatus=null;rootButton=null;
  scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
  page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(24),dp(16),dp(24),dp(28));scroll.addView(page,new ScrollView.LayoutParams(-1,-2));setContentView(scroll);
  if(!guide){
   TextView tag=text("S23 HYPE  /  SYSTEM TOOLS",12,0xff92adff,true);tag.setLetterSpacing(.1f);add(tag,12);
   Hero hero=new Hero();LinearLayout.LayoutParams hl=new LinearLayout.LayoutParams(-1,dp(290));hl.topMargin=dp(14);page.addView(hero,hl);
   TextView title=text("Soft Restart",36,FG,true);title.setGravity(Gravity.CENTER);add(title,8);
   TextView desc=text("A faster way to restart Android.\nBuilt for the Samsung power menu.",16,MUTED,false);desc.setGravity(Gravity.CENTER);add(desc,12);
   TextView credit=text("Made by s23_hype",17,FG,true);credit.setGravity(Gravity.CENTER);credit.setOnClickListener(v->telegram());add(credit,28);
   TextView link=text(channel,13,0xff92adff,false);link.setGravity(Gravity.CENTER);link.setOnClickListener(v->telegram());add(link,6);
   add(button("Continue to setup",true,()->show(true)),24);
   add(button("Join Telegram channel",false,this::telegram),10);
   hero.setAlpha(0f);hero.setScaleX(.87f);hero.setScaleY(.87f);hero.setRotationX(12);hero.animate().alpha(1).scaleX(1).scaleY(1).rotationX(0).setDuration(650).setInterpolator(new DecelerateInterpolator()).start();
  }else{
   add(text("GET STARTED",12,0xff92adff,true),16);add(text("Set up Soft Restart",29,FG,true),8);
   add(text("Use a stable rooted device. No commands run automatically.",15,MUTED,false),10);
   step("01","Grant Superuser access","Open KernelSU → Superuser → Soft Restart → Allow. Or tap Check root below to request access.");
   rootStatus=text(checking?"Checking root…":"Root status: not checked",14,MUTED,false);add(rootStatus,8);
   rootButton=button("Check / request root access",true,this::checkRoot);rootButton.setEnabled(!checking);add(rootButton,10);
   step("02","Enable LSPosed module","Open LSPosed → Modules → Soft Restart → Enable. Select only System UI (com.android.systemui).");
   step("03","Restart normally once","Save your work and restart your phone normally to load the module.");
   step("04","Use the power menu","Tap Soft restart, then tap the confirmation icon again. Android restarts its userspace without a normal full reboot.");
   step("05","Add Quick Panel tile","Expand Quick Panel → Edit buttons → add Soft restart. The tile does not require LSPosed.");
   add(text("Safety: save your work first. If root is unstable or SystemUI misbehaves, do not restart using this module. Disable it in LSPosed. Firmware support is not guaranteed.",14,0xffe7be91,false),20);
   add(button("Open restart confirmation",false,()->startActivity(new Intent(this,ConfirmActivity.class))),20);
   add(button("Join @s23_hype",false,this::telegram),10);add(button("Back to welcome",false,()->show(false)),10);
  }
 }
 private void step(String n,String title,String body){
  LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(18),dp(15),dp(18),dp(15));card.setBackground(rounded(0xff172138,22));
  card.addView(text(n+"  /  "+title,17,FG,true));TextView detail=text(body,14,MUTED,false);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(7);card.addView(detail,p);add(card,12);
 }
 private void checkRoot(){if(checking)return;checking=true;rootButton.setEnabled(false);rootStatus.setText("Checking root…");
  new Thread(()->{String result="Root unavailable or permission denied.";
   java.lang.Process proc=null;
   try{proc=new ProcessBuilder("su","-c","test \"$(id -u)\" = 0 && test -x /data/adb/ksud && /data/adb/ksud --help | grep -q soft-reboot").redirectErrorStream(true).redirectOutput(new File("/dev/null")).start();
    if(proc.waitFor(25,TimeUnit.SECONDS)&&proc.exitValue()==0)result="Root granted • KernelSU soft restart available";
   }catch(Exception ignored){}finally{if(proc!=null)proc.destroyForcibly();}
   final String msg=result;runOnUiThread(()->{checking=false;if(isFinishing()||isDestroyed())return;if(rootStatus!=null)rootStatus.setText(msg);if(rootButton!=null)rootButton.setEnabled(true);});
  },"soft-restart-root-check").start();
 }
 private final class Hero extends View {
  final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);final SoftIcon badge=new SoftIcon(100,100);
  Hero(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
  @Override protected void onDraw(Canvas c){super.onDraw(c);float cx=getWidth()/2f,cy=getHeight()*.46f,r=Math.min(getWidth()*.31f,getHeight()*.32f);
   p.setShader(new RadialGradient(cx,cy,r*1.8f,new int[]{0x60386ee8,0x20386ee8,0x00386ee8},null,Shader.TileMode.CLAMP));c.drawCircle(cx,cy,r*1.8f,p);p.setShader(null);
   p.setColor(0x38000000);c.drawOval(cx-r,cy+r*1.08f,cx+r,cy+r*1.37f,p);
   p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(1));p.setColor(0x55648fff);c.drawOval(cx-r*1.48f,cy-r*.31f,cx+r*1.48f,cy+r*.31f,p);p.setStyle(Paint.Style.FILL);
   p.setShader(new LinearGradient(cx,cy-r,cx,cy+r*1.18f,0xff6b9fff,0xff142d75,Shader.TileMode.CLAMP));c.drawCircle(cx,cy+r*.09f,r,p);p.setShader(null);
   badge.setBounds((int)(cx-r),(int)(cy-r),(int)(cx+r),(int)(cy+r));badge.draw(c);
   p.setColor(0x339fc6ff);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(2));c.drawArc(cx-r*.83f,cy-r*.83f,cx+r*.83f,cy+r*.83f,205,105,false,p);p.setStyle(Paint.Style.FILL);
  }
 }
}
