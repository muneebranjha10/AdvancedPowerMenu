package io.github.s23hype.advancedpowermenu;
import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;
public final class MainActivity extends Activity {
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  LinearLayout l=new LinearLayout(this); l.setOrientation(1);
  int p=(int)(24*getResources().getDisplayMetrics().density); l.setPadding(p,p,p,p);
  TextView title=new TextView(this); title.setText("AdvancedPowerMenu"); title.setTextSize(26); l.addView(title);
  TextView t=new TextView(this); t.setText("Experimental • Use on stable root only.\n\n1. Edit Quick Panel and add Soft restart.\n2. For the optional native power-menu button, enable in LSPosed and scope System UI only.\n3. Save your work before restarting. No automatic reboot.\n\nPower menu integration is not device-tested."); t.setTextSize(16); l.addView(t);
  Button test=new Button(this); test.setText("Open restart confirmation"); test.setOnClickListener(v->startActivity(new Intent(this,ConfirmActivity.class))); l.addView(test);
  setContentView(l);
 }
}
