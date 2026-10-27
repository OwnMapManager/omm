package io.github.ownmapmanager.omm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * v94: przyciski „Zakończ” i „Punkt” w powiadomieniu nagrywania – bez otwierania OMM.
 * Odbiornik nie jest dostępny dla innych aplikacji (exported=false w manifeście).
 */
public class OmmRecActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        OmmRecNotifPlugin.fire(intent.getStringExtra(OmmRecNotifPlugin.EXTRA));
    }
}
