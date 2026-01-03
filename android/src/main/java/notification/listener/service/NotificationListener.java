package notification.listener.service;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;




@SuppressLint("OverrideAbstract")
@RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification notification) {
        handleNotification(notification);
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


    @RequiresApi(api = VERSION_CODES.KITKAT)
    private void handleNotification(StatusBarNotification notification) {
        String packageName = notification.getPackageName();
        Bundle extras = notification.getNotification().extras;
        String INTENT = "slayer.notification.listener.service.intent";
        Intent intent = new Intent(INTENT);

        intent.putExtra("package_name", packageName);
        intent.putExtra("notification_id", notification.getId());

        if (extras != null) {
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

            intent.putExtra("title", title != null ? title.toString() : "");
            intent.putExtra("message", text != null ? text.toString() : "");
        } else {
            intent.putExtra("title", "");
            intent.putExtra("message", "");
        }

        // Ambil tombol-tombol notifikasi (jika ada)
        Notification.Action[] actions = notification.getNotification().actions;
        if (actions != null && actions.length > 0) {
            intent.putExtra("action_count", actions.length);
            for (int i = 0; i < actions.length; i++) {
                Notification.Action action = actions[i];
                CharSequence actionTitle = action.title;
                intent.putExtra("action_" + i, actionTitle != null ? actionTitle.toString() : "");
            }
        } else {
            intent.putExtra("action_count", 0);
        }

        sendBroadcast(intent);
    }



}
