package io.github.s23hype.advancedpowermenu;
import android.app.PendingIntent;
import android.content.Intent;
import android.service.quicksettings.*;
public final class RestartTile extends TileService {
 @Override public void onStartListening() { super.onStartListening(); Tile t=getQsTile(); if(t!=null){t.setLabel("Soft restart"); t.setState(Tile.STATE_INACTIVE); t.updateTile();} }
 @Override public void onClick(){ super.onClick(); Runnable launch=()->{
  Intent i=new Intent(this,ConfirmActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  startActivityAndCollapse(PendingIntent.getActivity(this,10,i,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT));
 }; if(isLocked()) unlockAndRun(launch); else launch.run(); }
}
