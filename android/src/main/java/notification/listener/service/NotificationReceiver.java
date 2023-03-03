package notification.listener.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import java.util.HashMap;
import io.flutter.plugin.common.EventChannel.EventSink;

public class NotificationReceiver extends BroadcastReceiver {

    private final EventSink eventSink;

    public NotificationReceiver(EventSink eventSink) {
        this.eventSink = eventSink;
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onReceive(Context context, Intent intent) {
        String PACKAGE_NAME = "package_name";
        String packageName = intent.getStringExtra(PACKAGE_NAME);
        String NOTIFICATION_TITLE = "title";
        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String NOTIFICATION_CONTENT = "message";
        String content = intent.getStringExtra(NOTIFICATION_CONTENT);
        String ID = "notification_id";
        int id = intent.getIntExtra(ID, -1);


        HashMap<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("packageName", packageName);
        data.put("title", title);
        data.put("content", content);
        eventSink.success(data);
    }
}
