/*
 * This is the source code of Telegram for Android v. 1.4.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package org.tomato.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.tomato.messenger.FileLog;
import org.tomato.messenger.R;
import org.tomato.messenger.TLRPC;
import org.tomato.messenger.UserConfig;
import org.tomato.ui.ApplicationLoader;
import org.tomato.ui.Views.NumberPicker;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Locale;

public class AndroidUtilities {

    public static SharedPreferences mSettings;
    public static final int TELEGRAM_BUBBLE_STYLE = 0;
    public static final int WECHAT_BUBBLE_STYLE = 1;
    public static final int HANGOUT_BUBBLE_STYLE = 2;
    public static final int HIKE1_BUBBLE_STYLE = 31;
    public static final int HIKE2_BUBBLE_STYLE = 32;
    public static final int HIKE3_BUBBLE_STYLE = 33;
    public static final int HIKE4_BUBBLE_STYLE = 34;
    public static final int HIKE5_BUBBLE_STYLE = 35;
    public static final int HIKE6_BUBBLE_STYLE = 36;
    public static final int HIKE7_BUBBLE_STYLE = 37;
    public static final int HIKE8_BUBBLE_STYLE = 38;
    public static final int HIKE9_BUBBLE_STYLE = 39;
    public static final int HIKE10_BUBBLE_STYLE = 40;
    public static final int LINE_BUBBLE_STYLE = 4;
    public static final int TANGO_BUBBLE_STYLE = 5;
    public static final int VIBER_BUBBLE_STYLE = 6;
    public static final int WHATSAPP_BUBBLE_STYLE = 7;
    public static final int ZALO_BUBBLE_STYLE = 8;


    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<String, Typeface>();
    private static int prevOrientation = -10;
    private static boolean waitingForSms = false;
    private static final Object smsLock = new Object();

    public static int statusBarHeight = 0;
    public static float density = 1;
    public static Point displaySize = new Point();
    public static Integer photoSize = null;
    private static Boolean isTablet = null;

    public static int[] arrColors = {0xffee4928, 0xff41a903, 0xffe09602, 0xff0f94ed, 0xff8f3bf7, 0xfffc4380, 0xff00a1c4, 0xffeb7002};
    public static int[] arrUsersAvatars = {
            R.drawable.user_red,
            R.drawable.user_green,
            R.drawable.user_yellow,
            R.drawable.user_blue,
            R.drawable.user_violet,
            R.drawable.user_pink,
            R.drawable.user_aqua,
            R.drawable.user_orange};

    public static int[] arrGroupsAvatars = {
            R.drawable.group_red,
            R.drawable.group_green,
            R.drawable.group_yellow,
            R.drawable.group_blue,
            R.drawable.group_violet,
            R.drawable.group_pink,
            R.drawable.group_aqua,
            R.drawable.group_orange};

    public static int[] arrBroadcastAvatars = {
            R.drawable.broadcast_red,
            R.drawable.broadcast_green,
            R.drawable.broadcast_yellow,
            R.drawable.broadcast_blue,
            R.drawable.broadcast_violet,
            R.drawable.broadcast_pink,
            R.drawable.broadcast_aqua,
            R.drawable.broadcast_orange};

    static {
        density = ApplicationLoader.applicationContext.getResources().getDisplayMetrics().density;
        checkDisplaySize();
    }

    public static void lockOrientation(Activity activity) {
        if (activity == null || prevOrientation != -10) {
            return;
        }
        try {
            prevOrientation = activity.getRequestedOrientation();
            WindowManager manager = (WindowManager)activity.getSystemService(Activity.WINDOW_SERVICE);
            if (manager != null && manager.getDefaultDisplay() != null) {
                int rotation = manager.getDefaultDisplay().getRotation();
                int orientation = activity.getResources().getConfiguration().orientation;

                if (rotation == Surface.ROTATION_270) {
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        if (Build.VERSION.SDK_INT >= 9) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        } else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    }
                } else if (rotation == Surface.ROTATION_90) {
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (Build.VERSION.SDK_INT >= 9) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        } else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                } else if (rotation == Surface.ROTATION_0) {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (Build.VERSION.SDK_INT >= 9) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        } else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 9) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        } else {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public static void unlockOrientation(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            if (prevOrientation != -10) {
                activity.setRequestedOrientation(prevOrientation);
                prevOrientation = -10;
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public static Typeface getTypeface(String assetPath) {
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), assetPath);
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    FileLog.e("Typefaces", "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    return null;
                }
            }
            return typefaceCache.get(assetPath);
        }
    }

    public static boolean isWaitingForSms() {
        boolean value = false;
        synchronized (smsLock) {
            value = waitingForSms;
        }
        return value;
    }

    public static void setWaitingForSms(boolean value) {
        synchronized (smsLock) {
            waitingForSms = value;
        }
    }

    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        ((InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputManager.isActive(view);
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static File getCacheDir() {
        if (Environment.getExternalStorageState() == null || Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            try {
                File file = ApplicationLoader.applicationContext.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }
        try {
            File file = ApplicationLoader.applicationContext.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return new File("");
    }

    public static int dp(int value) {
        return (int)(Math.max(1, density * value));
    }

    public static int dpf(float value) {
        return (int)Math.ceil(density * value);
    }

    public static float dpf2(float value) {
        return density * value;
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager)ApplicationLoader.applicationContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    if(android.os.Build.VERSION.SDK_INT < 13) {
                        displaySize.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySize);
                    }
                    FileLog.e("tmessages", "display size = " + displaySize.x + " " + displaySize.y);
                }
            }
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public static long makeBroadcastId(int id) {
        return 0x0000000100000000L | ((long)id & 0x00000000FFFFFFFFL);
    }

    public static int getMyLayerVersion(int layer) {
        return layer & 0xffff;
    }

    public static int getPeerLayerVersion(int layer) {
        return (layer >> 16) & 0xffff;
    }

    public static int setMyLayerVersion(int layer, int version) {
        return layer & 0xffff0000 | version;
    }

    public static int setPeerLayerVersion(int layer, int version) {
        return layer & 0x0000ffff | (version << 16);
    }

    public static void RunOnUIThread(Runnable runnable) {
        RunOnUIThread(runnable, 0);
    }

    public static void RunOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationLoader.applicationHandler.post(runnable);
        } else {
            ApplicationLoader.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void CancelRunOnUIThread(Runnable runnable) {
        ApplicationLoader.applicationHandler.removeCallbacks(runnable);
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isTablet);
        }
        return isTablet;
    }

    public static boolean isSmallTablet() {
        float minSide = Math.min(displaySize.x, displaySize.y) / density;
        return minSide <= 700;
    }

    public static int getMinTabletSide() {
        if (!isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = smallSide * 35 / 100;
            if (leftSide < dp(320)) {
                leftSide = dp(320);
            }
            return smallSide - leftSide;
        } else {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int maxSide = Math.max(displaySize.x, displaySize.y);
            int leftSide = maxSide * 35 / 100;
            if (leftSide < dp(320)) {
                leftSide = dp(320);
            }
            return Math.min(smallSide, maxSide - leftSide);
        }
    }

    public static int getColorIndex(int id) {
        int[] arr;
        if (id >= 0) {
            arr = arrUsersAvatars;
        } else {
            arr = arrGroupsAvatars;
        }
        try {
            String str;
            if (id >= 0) {
                str = String.format(Locale.US, "%d%d", id, UserConfig.getClientUserId());
            } else {
                str = String.format(Locale.US, "%d", id);
            }
            if (str.length() > 15) {
                str = str.substring(0, 15);
            }
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            int b = digest[Math.abs(id % 16)];
            if (b < 0) {
                b += 256;
            }
            return Math.abs(b) % arr.length;
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return id % arr.length;
    }

    public static int getColorForId(int id) {
        if (id / 1000 == 333) {
            return 0xff0f94ed;
        }
        return arrColors[getColorIndex(id)];
    }

    public static int getUserAvatarForId(int id) {
        if (id / 1000 == 333 || id / 1000 == 777) {
            return R.drawable.telegram_avatar;
        }
        return arrUsersAvatars[getColorIndex(id)];
    }

    public static int getGroupAvatarForId(int id) {
        return arrGroupsAvatars[getColorIndex(-Math.abs(id))];
    }

    public static int getBroadcastAvatarForId(int id) {
        return arrBroadcastAvatars[getColorIndex(-Math.abs(id))];
    }

    public static int getPhotoSize() {
        if (photoSize == null) {
            if (Build.VERSION.SDK_INT >= 16) {
                photoSize = 1280;
            } else {
                photoSize = 800;
            }
        }
        return photoSize;
    }

    public static String formatTTLString(int ttl) {
        if (ttl < 60) {
            return LocaleController.formatPluralString("Seconds", ttl);
        } else if (ttl < 60 * 60) {
            return LocaleController.formatPluralString("Minutes", ttl / 60);
        } else if (ttl < 60 * 60 * 24) {
            return LocaleController.formatPluralString("Hours", ttl / 60 / 60);
        } else if (ttl < 60 * 60 * 24 * 7) {
            return LocaleController.formatPluralString("Days", ttl / 60 / 60 / 24);
        } else {
            int days = ttl / 60 / 60 / 24;
            if (ttl % 7 == 0) {
                return LocaleController.formatPluralString("Weeks", days / 7);
            } else {
                return String.format("%s %s", LocaleController.formatPluralString("Weeks", days / 7), LocaleController.formatPluralString("Days", days % 7));
            }
        }
    }

    public static AlertDialog.Builder buildTTLAlert(final Context context, final TLRPC.EncryptedChat encryptedChat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 60 * 60) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 60 * 60 * 24) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 60 * 60 * 24 * 7) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(5);
        }
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                if (value == 0) {
                    return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                } else if (value >= 1 && value < 16) {
                    return AndroidUtilities.formatTTLString(value);
                } else if (value == 16) {
                    return AndroidUtilities.formatTTLString(30);
                } else if (value == 17) {
                    return AndroidUtilities.formatTTLString(60);
                } else if (value == 18) {
                    return AndroidUtilities.formatTTLString(60 * 60);
                } else if (value == 19) {
                    return AndroidUtilities.formatTTLString(60 * 60 * 24);
                } else if (value == 20) {
                    return AndroidUtilities.formatTTLString(60 * 60 * 24 * 7);
                }
                return "";
            }
        });
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int oldValue = encryptedChat.ttl;
                which = numberPicker.getValue();
                if (which >= 0 && which < 16) {
                    encryptedChat.ttl = which;
                } else if (which == 16) {
                    encryptedChat.ttl = 30;
                } else if (which == 17) {
                    encryptedChat.ttl = 60;
                } else if (which == 18) {
                    encryptedChat.ttl = 60 * 60;
                } else if (which == 19) {
                    encryptedChat.ttl = 60 * 60 * 24;
                } else if (which == 20) {
                    encryptedChat.ttl = 60 * 60 * 24 * 7;
                }
                if (oldValue != encryptedChat.ttl) {
                    SendMessagesHelper.getInstance().sendTTLMessage(encryptedChat, null);
                    MessagesStorage.getInstance().updateEncryptedChatTTL(encryptedChat);
                }
            }
        });
        return builder;
    }

    public static void clearCursorDrawable(EditText editText) {
        if (editText == null || Build.VERSION.SDK_INT < 12) {
            return;
        }
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.setInt(editText, 0);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }


    /*backgroundDrawableIn = getResources().getDrawable(R.drawable.msg_in);
    backgroundDrawableInSelected = getResources().getDrawable(R.drawable.msg_in_selected);
    backgroundDrawableOut = getResources().getDrawable(R.drawable.msg_out);
    backgroundDrawableOutSelected = getResources().getDrawable(R.drawable.msg_out_selected);
    backgroundMediaDrawableIn = getResources().getDrawable(R.drawable.msg_in_photo);
    backgroundMediaDrawableInSelected = getResources().getDrawable(R.drawable.msg_in_photo_selected);
    backgroundMediaDrawableOut = getResources().getDrawable(R.drawable.msg_out_photo);
    backgroundMediaDrawableOutSelected = getResources().getDrawable(R.drawable.msg_out_photo_selected);
    checkDrawable = getResources().getDrawable(R.drawable.msg_check);
    halfCheckDrawable = getResources().getDrawable(R.drawable.msg_halfcheck);
    clockDrawable = getResources().getDrawable(R.drawable.msg_clock);
    checkMediaDrawable = getResources().getDrawable(R.drawable.msg_check_w);
    halfCheckMediaDrawable = getResources().getDrawable(R.drawable.msg_halfcheck_w);
    clockMediaDrawable = getResources().getDrawable(R.drawable.msg_clock_photo);
    errorDrawable = getResources().getDrawable(R.drawable.msg_warning);
    mediaBackgroundDrawable = getResources().getDrawable(R.drawable.phototime);
    */

    public static int getBackgroundDrawableIn(){



        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chat_from_bg_normal;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_msg_bubble_left;
            case WHATSAPP_BUBBLE_STYLE:
                return R.drawable.balloon_incoming_normal;
                
            case HIKE1_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE2_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE3_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE4_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE5_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE6_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE7_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE8_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE9_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;
            case HIKE10_BUBBLE_STYLE:
                return R.drawable.hike0_ic_bubble_white;


            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
                    return R.drawable.line_v2_img_chats_bg_01;
                
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_in;
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_incoming_normal;
            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_in_text_normal;
            default:

                
        }

        return 0;
    }


    public static int getBackgroundDrawableInSelected(){



        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chat_from_bg_pressed;
            case WHATSAPP_BUBBLE_STYLE:
                return R.drawable.balloon_incoming_focused;
                
            case HIKE1_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;

            case HIKE2_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE3_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE4_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE5_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE6_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE7_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE8_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE9_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
            case HIKE10_BUBBLE_STYLE:
                return R.drawable.hike0_pressed_ic_bubble_white;
                
            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
                return R.drawable.line_v2_img_chats_bg_01_pressed;
                
            case TELEGRAM_BUBBLE_STYLE:
              return  R.drawable.msg_in_selected;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_media_msg_bubble_pressed_left;
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_incoming_selected;

            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_in_text_pressed;
            default:

                
        }

        return 0;
    }



    public static int getbackgroundDrawableOut(){


        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chat_to_bg_normal;
            case WHATSAPP_BUBBLE_STYLE:
                return R.drawable.balloon_outgoing_normal;
                
            case HIKE1_BUBBLE_STYLE:
                return R.drawable.hike1_ic_bubble_celebration_space;
            case HIKE2_BUBBLE_STYLE:
                return R.drawable.hike2_ic_bubble_blue;
            case HIKE3_BUBBLE_STYLE:
                return R.drawable.hike3_ic_bubble_music;
            case HIKE4_BUBBLE_STYLE:
                return R.drawable.hike4_ic_bubble_forest_study_sporty;
            case HIKE5_BUBBLE_STYLE:
                return R.drawable.hike5_ic_bubble_mr_right_exam;
            case HIKE6_BUBBLE_STYLE:
                return R.drawable.hike6_ic_bubble_love_floral_bikers_kisses_valentines_girly;
            case HIKE7_BUBBLE_STYLE:
                return R.drawable.hike7_ic_bubble_night;
            case HIKE8_BUBBLE_STYLE:
                return R.drawable.hike8_ic_bubble_rains_beach_2;
            case HIKE9_BUBBLE_STYLE:
                return R.drawable.hike9_ic_bubble_owl;
            case HIKE10_BUBBLE_STYLE:
                return R.drawable.hike10_ic_bubble_smiley_cheers_pets_sporty_cupcakes;

            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
                return R.drawable.line_v2_img_chats_bg_02;
                
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_out;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_msg_bubble_right;

            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_text_outgoing_selected;
            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_out_text_normal;

            default:

                
        }

        return 0;
    }


    public static int getbackgroundDrawableOutSelected(){



        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chat_to_bg_pressed;
            case WHATSAPP_BUBBLE_STYLE:
                return R.drawable.balloon_outgoing_focused;
                
            case HIKE1_BUBBLE_STYLE:
                return R.drawable.hike1_pressed_ic_bubble_celebration_space_pressed;
            case HIKE2_BUBBLE_STYLE:
                return R.drawable.hike2_pressed_ic_bubble_chatty_beachy_techy;
            case HIKE3_BUBBLE_STYLE:
                return R.drawable.hike3_pressed_ic_bubble_creepy;
            case HIKE4_BUBBLE_STYLE:
                return R.drawable.hike4_pressed_ic_bubble_green;
            case HIKE5_BUBBLE_STYLE:
                return R.drawable.hike5_pressed_ic_bubble_hikin_couple_mountain;
            case HIKE6_BUBBLE_STYLE:
                return R.drawable.hike6_pressed_ic_bubble_hikin_couple_mountain;
            case HIKE7_BUBBLE_STYLE:
                return R.drawable.hike7_pressed_ic_bubble_starry_space;
            case HIKE8_BUBBLE_STYLE:
                return R.drawable.hike8_pressed_ic_bubble_starry_space;
            case HIKE9_BUBBLE_STYLE:
                return R.drawable.hike9_pressed_ic_bubble_owl;
            case HIKE10_BUBBLE_STYLE:
                return R.drawable.hike10_pressed_ic_bubble_smiley_cheers_pets_sporty_cupcakes;
                
            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
                return R.drawable.line_v2_img_chats_bg_02_pressed;

                
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_out_selected;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_media_msg_bubble_pressed_right;
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_text_outgoing_normal;

            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_out_text_pressed;
            default:

                
        }

        return 0;
    }



    public static int getbackgroundMediaDrawableIn(){



        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chatfrom_bg_pic_from;




                
            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
            case HIKE1_BUBBLE_STYLE:
            case HIKE2_BUBBLE_STYLE:
            case HIKE3_BUBBLE_STYLE:
            case HIKE4_BUBBLE_STYLE:
            case HIKE5_BUBBLE_STYLE:
            case HIKE6_BUBBLE_STYLE:
            case HIKE7_BUBBLE_STYLE:
            case HIKE8_BUBBLE_STYLE:
            case HIKE9_BUBBLE_STYLE:
            case HIKE10_BUBBLE_STYLE:
            case WHATSAPP_BUBBLE_STYLE:
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_in_photo;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_media_msg_bubble_left;
                
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_media_incoming_normal;
            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_media_in_normal;

            default:

                
        }

        return 0;
    }



    public static int getbackgroundMediaDrawableInSelected(){


        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chatfrom_bg_pic_from;




                
            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
            case HIKE1_BUBBLE_STYLE:
            case HIKE2_BUBBLE_STYLE:
            case HIKE3_BUBBLE_STYLE:
            case HIKE4_BUBBLE_STYLE:
            case HIKE5_BUBBLE_STYLE:
            case HIKE6_BUBBLE_STYLE:
            case HIKE7_BUBBLE_STYLE:
            case HIKE8_BUBBLE_STYLE:
            case HIKE9_BUBBLE_STYLE:
            case HIKE10_BUBBLE_STYLE:
            case WHATSAPP_BUBBLE_STYLE:
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_in_photo_selected;

            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_media_msg_bubble_pressed_left;
                
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viviber_media_incoming_selected;

            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_media_in_pressed;
            default:

                
        }

        return 0;
    }


    public static int getbackgroundMediaDrawableOut(){


        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chatfrom_bg_pic_to;



                
            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
            case HIKE1_BUBBLE_STYLE:
            case HIKE2_BUBBLE_STYLE:
            case HIKE3_BUBBLE_STYLE:
            case HIKE4_BUBBLE_STYLE:
            case HIKE5_BUBBLE_STYLE:
            case HIKE6_BUBBLE_STYLE:
            case HIKE7_BUBBLE_STYLE:
            case HIKE8_BUBBLE_STYLE:
            case HIKE9_BUBBLE_STYLE:
            case HIKE10_BUBBLE_STYLE:
            case WHATSAPP_BUBBLE_STYLE:
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_out_photo;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_media_msg_bubble_right;
                
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_media_outgoing_normal;

            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_media_out_normal;
            default:

                
        }

        return 0;
    }





    public static int getbackgroundMediaDrawableOutSelected(){



        switch (ApplicationLoader.balloonType){
            case WECHAT_BUBBLE_STYLE:
                return R.drawable.wechat_chatfrom_bg_pic_to;



                
            case TANGO_BUBBLE_STYLE:
                

            case LINE_BUBBLE_STYLE:
            case HIKE1_BUBBLE_STYLE:
            case HIKE2_BUBBLE_STYLE:
            case HIKE3_BUBBLE_STYLE:
            case HIKE4_BUBBLE_STYLE:
            case HIKE5_BUBBLE_STYLE:
            case HIKE6_BUBBLE_STYLE:
            case HIKE7_BUBBLE_STYLE:
            case HIKE8_BUBBLE_STYLE:
            case HIKE9_BUBBLE_STYLE:
            case HIKE10_BUBBLE_STYLE:
            case WHATSAPP_BUBBLE_STYLE:
            case TELEGRAM_BUBBLE_STYLE:
                return R.drawable.msg_out_photo_selected;
            case HANGOUT_BUBBLE_STYLE:
                return R.drawable.hangouts_media_msg_bubble_pressed_right;
                
            case VIBER_BUBBLE_STYLE:
                return R.drawable.viber_media_outgoing_selected;

            case ZALO_BUBBLE_STYLE:
                return R.drawable.zalo_media_out_pressed;
            default:

                
        }

        return 0;
    }
}
