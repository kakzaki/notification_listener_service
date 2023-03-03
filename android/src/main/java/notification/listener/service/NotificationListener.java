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
        String PACKAGE_NAME = "package_name";
        intent.putExtra(PACKAGE_NAME, packageName);
        String ID = "notification_id";
        intent.putExtra(ID, notification.getId());

        if (extras != null) {
            CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

            String NOTIFICATION_TITLE = "title";
            intent.putExtra(NOTIFICATION_TITLE, title == null ? null : title.toString());
            String NOTIFICATION_CONTENT = "message";
            intent.putExtra(NOTIFICATION_CONTENT, text == null ? null : text.toString());
        }
        sendBroadcast(intent);
    }



}
