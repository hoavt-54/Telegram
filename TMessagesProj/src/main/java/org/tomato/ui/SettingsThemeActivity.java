/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package org.tomato.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tomato.android.LocaleController;
import org.tomato.android.NotificationCenter;
import org.tomato.messenger.R;
import org.tomato.messenger.TLRPC;
import org.tomato.ui.Adapters.BaseFragmentAdapter;
import org.tomato.ui.Views.ActionBar.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListView listView;
    private ListAdapter listAdapter;
    //private ImageView backgroundImage;
    //private ProgressBar progressBar;
    private int selectedBackground;
    private int selectedColor;
    private ArrayList<TLRPC.WallPaper> wallPapers = new ArrayList<TLRPC.WallPaper>();
    private HashMap<Integer, TLRPC.WallPaper> wallpappersByIds = new HashMap<Integer, TLRPC.WallPaper>();
    private View doneButton;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private TLRPC.PhotoSize loadingSize = null;
    private String currentPicturePath;
    private int selectedColorTheme;
    private static int temporarySelectedColor;
    private static int temporarySelectedStyle;

    private static int [] colorThemes = {R.color.header, R.color.line_header, R.color.whatsapp_header, R.color.wechat_header,
                                         R.color.hangouts_header, R.color.bee_header, R.color.viber_header, R.color.hike_header,
                                         R.color.msngr_header, R.color.gg_messenger_header, R.color.girl_header, R.color.tango_header    };

    private static int [] styleThemes = {R.style.ActionBar_Transparent_TMessages_Start, R.style.ActionBarLine_Transparent_TMessages_Start,
                                         R.style.ActionBarWhatsapp_Transparent_TMessages_Start, R.style.ActionBarWechat_Transparent_TMessages_Start,
                                         R.style.ActionBarHangouts_Transparent_TMessages_Start, R.style.ActionBarBee_Transparent_TMessages_Start,
                                         R.style.ActionBarViber_Transparent_TMessages_Start, R.style.ActionBarHike_Transparent_TMessages_Start,
                                         R.style.ActionBarHike_Transparent_TMessages_Start, R.style.ActionBarMnsg_Transparent_TMessages_Start,
                                         R.style.ActionBarGGmess_Transparent_TMessages_Start, R.style.ActionBarGirl_Transparent_TMessages_Start,
                                         R.style.ActionBarTango_Transparent_TMessages_Start};

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        selectedColorTheme = preferences.getInt(ApplicationLoader.ACTIONBAR_COLOR, R.color.header);
        temporarySelectedColor = selectedColorTheme;


        /*NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);*/


        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        /*NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wallpapersDidLoaded);*/
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        if (fragmentView == null) {
            actionBarLayer.setCustomView(R.layout.settings_do_action_layout);
            Button cancelButton = (Button)actionBarLayer.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishFragment();
                }
            });
            doneButton = actionBarLayer.findViewById(R.id.done_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                if (temporarySelectedColor != selectedColorTheme) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(ApplicationLoader.ACTIONBAR_COLOR, temporarySelectedColor);
                    ApplicationLoader.actionbarColor = temporarySelectedColor;
                    editor.putInt(ApplicationLoader.ACTIONBAR_STYLE, temporarySelectedColor);
                    ApplicationLoader.actionBarStyle = temporarySelectedStyle;


                    //editor.putInt("selectedColor", selectedColor);
                    editor.commit();

                }
                    finishFragment();
                }
            });

            cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            TextView textView = (TextView)doneButton.findViewById(R.id.done_button_text);
            textView.setText(LocaleController.getString("Set", R.string.Set).toUpperCase());

            fragmentView = inflater.inflate(R.layout.settings_theme_layout, container, false);
            listAdapter = new ListAdapter(getParentActivity());

            //progressBar = (ProgressBar)fragmentView.findViewById(R.id.action_progress);
            //backgroundImage = (ImageView)fragmentView.findViewById(R.id.background_image);
            listView = (ListView)fragmentView.findViewById(R.id.theme_listview);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    temporarySelectedColor = SettingsThemeActivity.colorThemes[i];
                    temporarySelectedStyle = SettingsThemeActivity.styleThemes[i];
                    listAdapter.notifyDataSetChanged();
                }
            });

            processSelectedBackground();
        } else {
            ViewGroup parent = (ViewGroup)fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }


    @Override
    public void saveSelfArgs(Bundle args) {
        if (currentPicturePath != null) {
            args.putString("path", currentPicturePath);
        }
    }

    @Override
    public void restoreSelfArgs(Bundle args) {
        currentPicturePath = args.getString("path");
    }

    private void processSelectedBackground() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void didReceivedNotification(int id, final Object... args) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void loadWallpapers() {
    }

    private void fixLayout() {
       /* ViewTreeObserver obs = fragmentView.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                if (listView != null) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.scrollTo(0);
                        }
                    });
                }
                return false;
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        fixLayout();
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;
        private LayoutInflater inflater;
        public ListAdapter(Context context) {
            mContext = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            return SettingsThemeActivity.colorThemes.length;
        }

        @Override
        public Integer getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int i, View convertedView, ViewGroup viewGroup) {
            View view = convertedView;
            ViewHolder holder = null;
            if (view == null){
                view = inflater.inflate(R.layout.settings_themes_item_layout, null);
                holder = new ViewHolder();
                holder.checkButton = (RadioButton) view.findViewById(R.id.theme_item_check_button);
                holder.rowWrapperLayout = (RelativeLayout) view.findViewById(R.id.theme_item_wrapper);
                view.setTag(holder);
            }
            else
            holder = (ViewHolder)view.getTag();

            holder.rowWrapperLayout.setBackgroundResource(colorThemes[i]);
            holder.checkButton.setChecked(colorThemes[i] == temporarySelectedColor);
            if (colorThemes[i] == temporarySelectedColor){
                holder.rowWrapperLayout.setSelected(true);
            }
            else{
                holder.rowWrapperLayout.setSelected(false);
            }
            return view;
        }

        /*@Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            TLRPC.WallPaper wallPaper = wallPapers.get(i - 1);
            if (wallPaper instanceof TLRPC.TL_wallPaperSolid) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }*/

        @Override
        public boolean isEmpty() {
            return false;
        }

        private class ViewHolder{
            public RelativeLayout rowWrapperLayout;
            public RadioButton checkButton;
        }
    }

    public static class ThemeData {
        public int colorResId;
        public boolean isCurrentSelected;

        public ThemeData(int colorResId, boolean isCurrentSelected) {
            this.colorResId = colorResId;
            this.isCurrentSelected = isCurrentSelected;
        }
    }
}
