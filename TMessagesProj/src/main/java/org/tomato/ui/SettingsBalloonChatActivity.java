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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.tomato.android.AndroidUtilities;
import org.tomato.android.LocaleController;
import org.tomato.android.NotificationCenter;
import org.tomato.messenger.R;
import org.tomato.messenger.TLRPC;
import org.tomato.ui.Adapters.BaseFragmentAdapter;
import org.tomato.ui.Cells.ChatBaseCell;
import org.tomato.ui.Views.ActionBar.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsBalloonChatActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
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
    private int selectedBaloonType;
    private static int temporarySelectedType;

    private static List<BallonChatBgData> chatBgDatas;
    private static int [] hikePreviews = {R.drawable.hike1_ic_bubble_celebration_space, R.drawable.hike2_ic_bubble_blue,
                                          R.drawable.hike3_ic_bubble_music, R.drawable.hike4_ic_bubble_forest_study_sporty,
                                          R.drawable.hike5_ic_bubble_mr_right_exam, R.drawable.hike6_ic_bubble_love_floral_bikers_kisses_valentines_girly,
                                          R.drawable.hike7_ic_bubble_night, R.drawable.hike8_ic_bubble_rains_beach_2,
                                          R.drawable.hike9_ic_bubble_owl, R.drawable.hike10_ic_bubble_smiley_cheers_pets_sporty_cupcakes
                                                        };

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        selectedBaloonType = preferences.getInt(ApplicationLoader.BALLON_TYPE, AndroidUtilities.TELEGRAM_BUBBLE_STYLE);
        temporarySelectedType = selectedBaloonType;
        if (chatBgDatas == null) {
            chatBgDatas = new ArrayList<BallonChatBgData>();
            //add default
            BallonChatBgData defaultData = new BallonChatBgData(AndroidUtilities.TELEGRAM_BUBBLE_STYLE, R.drawable.msg_out,
                            selectedBaloonType == AndroidUtilities.TELEGRAM_BUBBLE_STYLE);
            chatBgDatas.add(defaultData);

            //add line style
            BallonChatBgData lineData = new BallonChatBgData(AndroidUtilities.LINE_BUBBLE_STYLE, R.drawable.line_v2_img_chats_bg_02,
                    selectedBaloonType == AndroidUtilities.LINE_BUBBLE_STYLE);
            chatBgDatas.add(lineData);

            //add viber style
            BallonChatBgData viberData = new BallonChatBgData(AndroidUtilities.VIBER_BUBBLE_STYLE, R.drawable.viber_text_outgoing_normal,
                    selectedBaloonType == AndroidUtilities.VIBER_BUBBLE_STYLE);
            chatBgDatas.add(viberData);

            //add ưhatsapp style
            BallonChatBgData whatsappData = new BallonChatBgData(AndroidUtilities.WHATSAPP_BUBBLE_STYLE, R.drawable.balloon_outgoing_normal,
                    selectedBaloonType == AndroidUtilities.WHATSAPP_BUBBLE_STYLE);
            chatBgDatas.add(whatsappData);

            //add ưhatsapp style
            BallonChatBgData zaloData = new BallonChatBgData(AndroidUtilities.ZALO_BUBBLE_STYLE, R.drawable.zalo_out_text_normal,
                    selectedBaloonType == AndroidUtilities.ZALO_BUBBLE_STYLE);
            chatBgDatas.add(zaloData);

            //add wechat style
            BallonChatBgData wechatData = new BallonChatBgData(AndroidUtilities.WECHAT_BUBBLE_STYLE, R.drawable.wechat_chat_to_bg_normal,
                    selectedBaloonType == AndroidUtilities.WECHAT_BUBBLE_STYLE);
            chatBgDatas.add(wechatData);

            //add hangouts style
            BallonChatBgData hangoutsData = new BallonChatBgData(AndroidUtilities.HANGOUT_BUBBLE_STYLE, R.drawable.hangouts_msg_bubble_left,
                    selectedBaloonType == AndroidUtilities.HANGOUT_BUBBLE_STYLE);
            chatBgDatas.add(hangoutsData);

            // add hike style
            for (int i = 1; i <= 10; i ++){
                BallonChatBgData hikeData = new BallonChatBgData(30 + i, hikePreviews[i -1 ],
                        selectedBaloonType == (30 + i));
                chatBgDatas.add(hikeData);

            }

            // add

        }

        /*NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);*/


        /*selectedBackground = preferences.getInt("selectedBackground", 1000001);
        selectedColor = preferences.getInt("selectedColor", 0);
        MessagesStorage.getInstance().getWallpapers();
        File toFile = new File(ApplicationLoader.applicationContext.getFilesDir(), "wallpaper-temp.jpg");
        toFile.delete();*/
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



                if (temporarySelectedType != selectedBaloonType) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(ApplicationLoader.BALLON_TYPE, temporarySelectedType);
                    ApplicationLoader.balloonType = temporarySelectedType;





                    //editor.putInt("selectedColor", selectedColor);
                    editor.commit();
                    //ApplicationLoader.cachedWallpaper = null;
                    ChatBaseCell.backgroundDrawableIn = null;
                    new ChatBaseCell(ApplicationLoader.applicationContext);
                }
                    finishFragment();
                }
            });

            cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            TextView textView = (TextView)doneButton.findViewById(R.id.done_button_text);
            textView.setText(LocaleController.getString("Set", R.string.Set).toUpperCase());

            fragmentView = inflater.inflate(R.layout.settings_baloon_chat_layout, container, false);
            listAdapter = new ListAdapter(getParentActivity());

            //progressBar = (ProgressBar)fragmentView.findViewById(R.id.action_progress);
            //backgroundImage = (ImageView)fragmentView.findViewById(R.id.background_image);
            listView = (ListView)fragmentView.findViewById(R.id.ballon_listview);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    temporarySelectedType = SettingsBalloonChatActivity.chatBgDatas.get(i).collectionType;
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
            return SettingsBalloonChatActivity.chatBgDatas.size();
        }

        @Override
        public BallonChatBgData getItem(int i) {
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
                view = inflater.inflate(R.layout.settings_baloon_item_layout, null);
                holder = new ViewHolder();
                holder.balloonPreviewImg = (ImageView)view.findViewById(R.id.ballon_previewImg);
                holder.checkButton = (RadioButton) view.findViewById(R.id.ballon_item_check_button);
                holder.rowWrapperLayout = (RelativeLayout) view.findViewById(R.id.ballon_item_wrapper);
                view.setTag(holder);
            }
            else
            holder = (ViewHolder)view.getTag();

            BallonChatBgData data = SettingsBalloonChatActivity.chatBgDatas.get(i);
            holder.balloonPreviewImg.setImageDrawable(mContext.getResources().getDrawable(data.previewImageId));
            holder.checkButton.setChecked(data.collectionType == temporarySelectedType);
            if (data.collectionType == temporarySelectedType){
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
            public ImageView balloonPreviewImg;
            public RadioButton checkButton;
        }
    }

    public static class BallonChatBgData {
        public int collectionType;
        public int previewImageId;
        public boolean isCurrentSelected;

        public BallonChatBgData(int collectionType, int previewImageId, boolean isCurrentSelected) {
            this.collectionType = collectionType;
            this.previewImageId = previewImageId;
            this.isCurrentSelected = isCurrentSelected;
        }
    }
}
