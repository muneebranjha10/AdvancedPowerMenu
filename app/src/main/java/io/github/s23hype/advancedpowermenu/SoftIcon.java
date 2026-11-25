package io.github.s23hype.advancedpowermenu;
import android.graphics.*;
import android.graphics.drawable.Drawable;
/** Resolution-independent icon. Never depends on bitmap density or a fixed source crop. */
public final class SoftIcon extends Drawable {
 private final int width,height;
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
 public SoftIcon(int w,int h){width=w;height=h;}
 @Override public int getIntrinsicWidth(){return width;}
 @Override public int getIntrinsicHeight(){return height;}
 @Override public void draw(Canvas c){
  Rect b=getBounds();float size=Math.min(b.width(),b.height());
  if(size<=0)return;
  int save=c.save();c.translate(b.exactCenterX(),b.exactCenterY());c.scale(size/100f,size/100f);
  p.setStyle(Paint.Style.FILL);p.setColor(0xff386ee8);c.drawCircle(0,0,44,p);
  p.setColor(0xffffffff);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(5.2f);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);
  // Two opposing circular arrows, distinct from the original power glyph.
  c.drawArc(-23,-23,23,23,205,142,false,p);
  c.drawArc(-23,-23,23,23,25,142,false,p);
  Path path=new Path();path.moveTo(12,-14);path.lineTo(23,-6);path.lineTo(25,-19);
  path.moveTo(-12,14);path.lineTo(-23,6);path.lineTo(-25,19);
  c.drawPath(path,p);
  p.setStyle(Paint.Style.FILL);c.drawCircle(0,0,3.5f,p);
  c.restoreToCount(save);
 }
 @Override public void setAlpha(int a){p.setAlpha(a);invalidateSelf();}
 @Override public void setColorFilter(ColorFilter f){p.setColorFilter(f);invalidateSelf();}
 @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
