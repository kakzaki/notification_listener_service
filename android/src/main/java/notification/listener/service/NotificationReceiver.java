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
        String packageName = intent.getStringExtra("package_name");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("message");
        int id = intent.getIntExtra("notification_id", -1);
        int actionCount = intent.getIntExtra("action_count", 0);


        HashMap<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("packageName", packageName != null ? packageName : "");
        data.put("title", title != null ? title : "");
        data.put("content", content != null ? content : "");
        data.put("actionCount", actionCount);


        for (int i = 0; i < actionCount; i++) {
            String key = "action_" + i;
            String actionTitle = intent.getStringExtra(key);
            data.put(key, actionTitle != null ? actionTitle : "");
        }

        eventSink.success(data);
    }

}
