package notification.listener.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class NotificationListenerServicePlugin implements FlutterPlugin,
        ActivityAware,
        MethodChannel.MethodCallHandler,
        PluginRegistry.ActivityResultListener,
        EventChannel.StreamHandler {

    private static final String CHANNEL_TAG = "x-slayer/notifications_channel";
    private static final String EVENT_TAG = "x-slayer/notifications_event";

    private MethodChannel channel;
    private EventChannel eventChannel;
    private NotificationReceiver notificationReceiver;

    private Context context;
    private Activity mActivity;

    private MethodChannel.Result pendingResult;

    private static final int REQUEST_CODE_FOR_NOTIFICATIONS = 1199;

    private void safeSuccess(Object value) {
        if (pendingResult != null) {
            pendingResult.success(value);
            pendingResult = null;
        } else {
            Log.w("NotificationPlugin", "No pending result to reply to");
        }
    }

    @Override
    @SuppressLint("WrongConstant")
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();

        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_TAG);
        channel.setMethodCallHandler(this);

        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), EVENT_TAG);
        eventChannel.setStreamHandler(this);
    }

    public static boolean isPermissionGranted(Context context) {
        String packageName = context.getPackageName();
        String flat = Settings.Secure.getString(
                context.getContentResolver(),
                "enabled_notification_listeners"
        );

        if (!TextUtils.isEmpty(flat)) {
            String[] names = flat.split(":");
            for (String name : names) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                if (componentName != null) {
                    if (TextUtils.equals(packageName, componentName.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

        switch (call.method) {

            case "isPermissionGranted":
                result.success(isPermissionGranted(context));
                break;

            case "requestPermission":
                pendingResult = result;

                if (mActivity == null) {
                    safeSuccess(false);
                    return;
                }

                Intent intent =
                        new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                mActivity.startActivityForResult(intent, REQUEST_CODE_FOR_NOTIFICATIONS);
                break;

            default:
                result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
        binding.addActivityResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        mActivity = null;
    }

    @SuppressLint({"WrongConstant", "UnspecifiedRegisterReceiverFlag"})
    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {

        IntentFilter intentFilter = new IntentFilter();
        String INTENT = "slayer.notification.listener.service.intent";
        intentFilter.addAction(INTENT);

        notificationReceiver = new NotificationReceiver(events);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            context.registerReceiver(notificationReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(notificationReceiver, intentFilter);
        }

        Intent listenerIntent = new Intent(context, NotificationReceiver.class);
        context.startService(listenerIntent);

        Log.i("NotificationPlugin", "Started the notifications tracking service.");
    }

    @Override
    public void onCancel(Object arguments) {
        if (notificationReceiver != null) {
            context.unregisterReceiver(notificationReceiver);
            notificationReceiver = null;
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_FOR_NOTIFICATIONS) {

            boolean granted = isPermissionGranted(context);
            safeSuccess(granted);

            return true;
        }

        return false;
    }
}
