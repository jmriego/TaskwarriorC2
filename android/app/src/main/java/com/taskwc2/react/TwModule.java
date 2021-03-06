package com.taskwc2.react;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ViewManager;
import com.taskwc2.App;
import com.taskwc2.MainActivity;
import com.taskwc2.controller.data.AccountController;
import com.taskwc2.controller.data.Controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.kvj.bravo7.log.Logger;
import org.kvj.bravo7.util.Tasks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.taskwc2.controller.tasker.ActivityConfigTaskwarriorChangeEvent;
import com.joaomgcd.taskerpluginlibrary.TaskerPluginConstants;
import net.dinglisch.android.tasker.TaskerPlugin;

/**
 * Created by kvorobyev on 6/1/16.
 */
public class TwModule extends ReactContextBaseJavaModule implements AccountController.AccountControllerListener {

    Logger logger = Logger.forInstance(this);
    Controller controller = App.controller();

    private AccountController accountController() {
        if (null == getCurrentActivity()) return null;
        String id = controller.defaultAccount();
        if (null != getCurrentActivity().getIntent()) { // Have intent
            String _id = getCurrentActivity().getIntent().getStringExtra(App.KEY_ACCOUNT);
            if (!TextUtils.isEmpty(_id)) {
                id = _id;
            }
        }
        return controller.accountController(id, false);
    };

    public TwModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        AccountController acc = accountController();
        if (null != acc) { // Un-Subscribe
            acc.listeners().remove(this);
        }
    }

    @Override
    public String getName() {
        return "TwModule";
    }

    private int fromMap(ReadableMap map, String attr) {
        int value = 0;
        if (map.hasKey(attr)) value = map.getInt(attr);
        return value;
    }

    private JSONObject objectFromMap(ReadableMap map, String attr) {
        JSONObject result = new JSONObject();
        try {
            if (map.hasKey(attr) && map.getType(attr).equals(ReadableType.Map)) {
                ReadableMap data = map.getMap(attr);
                for (ReadableMapKeySetIterator it = data.keySetIterator();it.hasNextKey();) {
                    String key = it.nextKey();
                    if (data.getType(key).equals(ReadableType.String)) {
                            result.put(key, data.getString(key));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @ReactMethod
    public void scheduleSync(int seconds, ReadableMap timers) {
        AccountController acc = accountController();
        if (null == acc) return;
        acc.rememberTimers(fromMap(timers, "normal"), fromMap(timers, "error"), objectFromMap(timers, "extra"));
        acc.scheduleSync(seconds);
    }

    @ReactMethod
    public void init(ReadableMap config, Promise promise) {
        AccountController acc = accountController();
        if (null == acc) {
            promise.resolve(null);
            return;
        };
        WritableNativeMap map = new WritableNativeMap();
        map.putString("id", acc.id());
        map.putString("title", acc.name());
        promise.resolve(map);
        if (null != acc) { // Subscribe
            acc.listeners().add(this);
        }
    }

    @ReactMethod
    public void editTaskrc(Promise promise) {
        AccountController acc = accountController();
        if (null == acc) {
            promise.reject("no_account", "Not configured");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_EDIT);
        Uri uri = FileProvider.getUriForFile(controller.context(), controller.context().getPackageName(), acc.taskrc());
        intent.setDataAndType(uri, "text/plain");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            getCurrentActivity().startActivityForResult(intent, App.EDIT_TASKRC_REQUEST);
            promise.resolve(true);
        } catch (Exception e) {
            logger.e(e, "Failed to edit file");
            promise.reject("no_editor", "No editor app is associated with plain-text files. Install one");
        }
    }

    @ReactMethod
    public void addProfile(final Promise promise) {
        new Tasks.SimpleTask<String>() {

            @Override
            protected String doInBackground() {
                return controller.createAccount(null);
            }

            @Override
            protected void onPostExecute(String s) {
                promise.resolve(s);
            }
        }.exec();
    }


    @ReactMethod
    public void backup(final Promise promise) {
        final AccountController acc = accountController();
        if (null == acc) {
            promise.reject("no_account", "Not configured");
            return;
        }
        new Tasks.SimpleTask<String>() {

            @Override
            protected String doInBackground() {
                return acc.makeBackup();
            }

            @Override
            protected void onPostExecute(String s) {
                if (s == null)
                    promise.resolve(s);
                else
                    promise.reject("error", s);
            }
        }.exec();
    }

    @ReactMethod
    public void removeProfile(final String id, final Promise promise) {
        new Tasks.SimpleTask<Boolean>() {

            @Override
            protected Boolean doInBackground() {
                return controller.removeAccount(id);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                promise.resolve(aBoolean);
            }
        }.exec();
    }

    @ReactMethod
    public void finish() {
        getCurrentActivity().finish();
    }

    @ReactMethod
    public void profileDefault(final String id, final Promise promise) {
        new Tasks.SimpleTask<Boolean>() {

            @Override
            protected Boolean doInBackground() {
                return controller.setDefault(id);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                promise.resolve(aBoolean);
            }
        }.exec();
    }

    @ReactMethod
    public void openProfile(String id) {
        Intent intent = new Intent(controller.context(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(App.KEY_ACCOUNT, id);
        getCurrentActivity().startActivity(intent);
    }


    @ReactMethod
    public void profiles(final Promise promise) {
        new Tasks.SimpleTask<WritableNativeArray>() {

            @Override
            protected WritableNativeArray doInBackground() {
                List<String> folders = controller.accountFolders();
                WritableNativeArray arr = new WritableNativeArray();
                String defaultAccount = controller.defaultAccount();
                for (String folder : folders) {
                    AccountController ac = controller.accountController(folder, false);
                    if (null != ac) {
                        WritableNativeMap map = new WritableNativeMap();
                        map.putString("id", ac.id());
                        map.putString("title", ac.name());
                        map.putBoolean("default", ac.id().equals(defaultAccount));
                        arr.pushMap(map);
                    }
                }
                return arr;
            }

            @Override
            protected void onPostExecute(WritableNativeArray data) {
                promise.resolve(data);
            }
        }.exec();
    }

    @Override
    public void onSync(final boolean finish) {
        new Tasks.VerySimpleTask(){

            @Override
            protected void doInBackground() {
                DeviceEventManagerModule.RCTDeviceEventEmitter jsModule =
                        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
                if (null != jsModule) { // Send notification
                    WritableMap params = Arguments.createMap();
                    params.putBoolean("finish", finish);
                    jsModule.emit("sync", params);
                }
            }
        }.exec();
    }

    public static void requestQuery(Context context) {
        Intent requestQueryIntentForTasker = new Intent(TaskerPluginConstants.ACTION_REQUEST_QUERY)
                .putExtra(TaskerPluginConstants.EXTRA_ACTIVITY,
                        ActivityConfigTaskwarriorChangeEvent.class.getName());;
        TaskerPlugin.Event.addPassThroughMessageID(requestQueryIntentForTasker);
        context.sendBroadcast(requestQueryIntentForTasker);
    }

    abstract class ArrayEater implements AccountController.StreamConsumer {

        final List<String> array = new ArrayList<>();

        @Override
        public void eat(String line) {
            array.add(line);
        }

        String[] array() {
            return array.toArray(new String[size()]);
        }

        int size() {
            return array.size();
        }
    }

    private boolean hasString(String[] strings, String searchString) {
        return Arrays.asList(strings).contains(searchString);
    }

    @ReactMethod
    public void call(ReadableArray args, ReadableMap config, final Callback linesEater) {
//        logger.d("Call:", args, linesEater, acc);
        final AccountController acc = accountController();
        long mod = acc.lastModified();
        if (null == acc) { // Invalid
            linesEater.invoke("error", "Profile not configured");
            return;
        }
        final ArrayEater out = new ArrayEater(){

            @Override
            public void flush() {
            }
        };
        final ArrayEater err = new ArrayEater(){

            @Override
            public void flush() {
            }
        };
        final String[] arguments = new String[args.size()];
        for (int i = 0; i < args.size(); i++) {
            arguments[i] = args.getString(i);
        }
        boolean question = false;
        if (config.hasKey("question")) { // Expect question in response
            question = config.getBoolean("question");
        }
        final boolean finalQuestion = question;
        Tasks.SimpleTask<Integer> task = new Tasks.SimpleTask<Integer>() {
            @Override
            protected Integer doInBackground() {
                return acc.callTask(out, err, finalQuestion, false, arguments);
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (acc.lastModified() > mod) {
                    requestQuery(controller.context());
                }
                Object[] result = new Object[out.size()+err.size()+4];
                result[0] = "success";
                result[1] = code;
                result[2] = out.size();
                result[3] = err.size();
                System.arraycopy(out.array(), 0, result, 4, out.size());
                System.arraycopy(err.array(), 0, result, 4+out.size(), err.size());
                linesEater.invoke(result);
            }
        };
        task.exec();
    }

    public static class TwPackage implements ReactPackage {

        public TwPackage() {
        }

        @Override
        public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
            List<NativeModule> modules = new ArrayList<>();
            modules.add(new TwModule(reactContext));
            return modules;
        }

        @Override
        public List<Class<? extends JavaScriptModule>> createJSModules() {
            return Collections.emptyList();
        }

        @Override
        public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
            return Collections.emptyList();
        }
    }
}
