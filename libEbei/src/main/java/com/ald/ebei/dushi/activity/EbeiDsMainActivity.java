package com.ald.ebei.dushi.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.activity.EbeiBaseActivity;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.dushi.fragment.EbeiMainFragment;
import com.ald.ebei.dushi.fragment.EbeiAccountFragment;
import com.ald.ebei.util.EbeiBundleKeys;

import java.util.ArrayList;
import java.util.List;

public class EbeiDsMainActivity extends EbeiBaseActivity implements View.OnClickListener {

    private ImageView mainMainIcon, mainAccountIcon;
    private TextView mainMainTv, mainAccountTv;
    private RelativeLayout mainRl, accountRl;
    private EbeiMainFragment mainMainFragment;
    private EbeiAccountFragment mainEbeiAccountFragment;
    private View mBootomLine;
    private LinearLayout mBootomView;
    private List<Fragment> fragmentList = new ArrayList<>();

    //当前是在哪个界面 首页0 账户1 登录界面2 帮助3
    private int curIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ds_main);
        initView();
    }

    private void initView() {
        mainMainIcon = findViewById(R.id.main_main_icon);
        mainAccountIcon = findViewById(R.id.main_account_icon);

        mainMainTv = findViewById(R.id.main_main_tv);
        mainAccountTv = findViewById(R.id.main_account_tv);
        mainRl = findViewById(R.id.main_rl);
        accountRl = findViewById(R.id.account_rl);

        mBootomLine = findViewById(R.id.line);
        mBootomView = findViewById(R.id.ll_bottom_tab);

        mainRl.setOnClickListener(this);
        accountRl.setOnClickListener(this);

        mainMainFragment = EbeiMainFragment.newInstance();
        fragmentList.add(mainMainFragment);

        addFragment(mainMainFragment);
        showFragment(mainMainFragment);
    }


    /*添加fragment*/
    private void addFragment(Fragment fragment) {
        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, fragment).commitAllowingStateLoss();
            /*添加到 fragmentList*/
            fragmentList.add(fragment);
        }
    }

    /*显示fragment*/
    private void showFragment(Fragment fragment) {
        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                /*先隐藏其他fragment*/
                getSupportFragmentManager().beginTransaction().hide(frag).commitAllowingStateLoss();
            }
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
    }


    private void resetUi() {
        mainMainIcon.setImageResource(R.mipmap.icon_homepage_buttom_tab_default_loan);
        mainAccountIcon.setImageResource(R.mipmap.icon_homepage_buttom_tab_default_mine);

        mainMainTv.setTextColor(getResources().getColor(R.color.color_txt_home_default_gray));
        mainAccountTv.setTextColor(getResources().getColor(R.color.color_txt_home_default_gray));
    }

    private void resetClick() {
        resetUi();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (curIndex == 0) {
            mainRl.performClick();
        }
        if (curIndex == 1) {
            accountRl.performClick();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int curr = intent.getIntExtra(EbeiBundleKeys.MAIN_DATA_TAB, 0);
            if (curr == 0) {
                mainRl.performClick();
            }
            if (curr == 1) {
                accountRl.performClick();
            }
        } else {
            mainRl.performClick();
            curIndex = 0;
        }
    }

    @Override
    public void onClick(View view) {
        resetClick();
        int id = view.getId();
        if (id == R.id.main_rl) {
            mainMainIcon.setImageResource(R.mipmap.icon_homepage_buttom_tab_select_loan);
            mainMainTv.setTextColor(getResources().getColor(R.color.color_colorPrimary));
            if (curIndex == 0) return;
            if (mainMainFragment == null) {
                mainMainFragment = EbeiMainFragment.newInstance();
            }
            addFragment(mainMainFragment);
            showFragment(mainMainFragment);
            curIndex = 0;
            mainMainFragment.loadData(EbeiConfig.isLand());
        } else if (id == R.id.account_rl) {
            mainAccountIcon.setImageResource(R.mipmap.icon_homepage_buttom_tab_select_mine);
            mainAccountTv.setTextColor(getResources().getColor(R.color.color_colorPrimary));
            if (curIndex == 1) return;
            if (mainEbeiAccountFragment == null) {
                mainEbeiAccountFragment = EbeiAccountFragment.newInstance();
            }
            addFragment(mainEbeiAccountFragment);
            showFragment(mainEbeiAccountFragment);
            curIndex = 1;
        }
    }


    @Override
    public String getStatName() {
        return "EbeiDsMainActivity";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mainMainFragment != null) {
            mainMainFragment.loadData(EbeiConfig.isLand());
        }
    }
}
