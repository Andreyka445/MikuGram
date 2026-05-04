package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class MikuGramSettingsActivity extends BaseFragment {

    // Настройки
    public static boolean ghostMode = false;
    public static boolean localPremium = false;
    public static boolean saveDeletedMessages = false;

    private static final String PREFS_NAME = "MikuGramSettings";

    // ID строк
    private int rowCount;
    private int featuresHeaderRow;
    private int ghostModeRow;
    private int localPremiumRow;
    private int saveDeletedRow;
    private int featuresInfoRow;

    private RecyclerListView listView;
    private ListAdapter adapter;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        loadSettings();

        rowCount = 0;
        featuresHeaderRow = rowCount++;
        ghostModeRow = rowCount++;
        localPremiumRow = rowCount++;
        saveDeletedRow = rowCount++;
        featuresInfoRow = rowCount++;

        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle("Настройки MikuGram");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        adapter = new ListAdapter(context);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((view, position) -> {
            if (position == ghostModeRow) {
                ghostMode = !ghostMode;
                saveSettings();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(ghostMode);
                }
            } else if (position == localPremiumRow) {
                localPremium = !localPremium;
                saveSettings();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(localPremium);
                }
            } else if (position == saveDeletedRow) {
                saveDeletedMessages = !saveDeletedMessages;
                saveSettings();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(saveDeletedMessages);
                }
            }
        });

        return fragmentView;
    }

    private void loadSettings() {
        SharedPreferences prefs = ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ghostMode = prefs.getBoolean("ghostMode", false);
        localPremium = prefs.getBoolean("localPremium", false);
        saveDeletedMessages = prefs.getBoolean("saveDeletedMessages", false);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("ghostMode", ghostMode);
        editor.putBoolean("localPremium", localPremium);
        editor.putBoolean("saveDeletedMessages", saveDeletedMessages);
        editor.apply();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 3:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawableByKey(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 1:
                default:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == featuresHeaderRow) {
                        headerCell.setText("Функции MikuGram");
                    }
                    break;
                }
                case 1: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == ghostModeRow) {
                        checkCell.setTextAndValueAndCheck(
                            "👻 Режим призрака",
                            "Скрывает факт прочтения и онлайн-статус",
                            ghostMode, true, true
                        );
                    } else if (position == localPremiumRow) {
                        checkCell.setTextAndValueAndCheck(
                            "💎 Локальный премиум",
                            "Разблокирует Premium функции локально",
                            localPremium, true, true
                        );
                    } else if (position == saveDeletedRow) {
                        checkCell.setTextAndValueAndCheck(
                            "💾 Сохранение удалённых",
                            "Сохраняет удалённые сообщения локально",
                            saveDeletedMessages, true, false
                        );
                    }
                    break;
                }
                case 3: {
                    TextInfoPrivacyCell infoCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == featuresInfoRow) {
                        infoCell.setText("🎵 MikuGram — форк Telegram с дополнительными функциями. Некоторые функции находятся в разработке.");
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == featuresHeaderRow) return 0;
            if (position == ghostModeRow || position == localPremiumRow || position == saveDeletedRow) return 1;
            if (position == featuresInfoRow) return 3;
            return 2;
        }
    }
}
