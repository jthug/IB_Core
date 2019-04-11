package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.base.BaseActivity;
import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.R;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityLanguageSwitchBinding;
import com.lianer.core.lauch.MainAct;

/**
 * 多语言设置
 * @author allison
 */
public class LanguageSwitchAct extends BaseActivity {

    private ActivityLanguageSwitchBinding languageSwitchBinding;
    private int languageType;

    @Override
    protected void initViews() {
        languageSwitchBinding = DataBindingUtil.setContentView(this, R.layout.activity_language_switch);
        initTitleBar();
        initClick();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        languageSwitchBinding.titlebar.showLeftDrawable();
        languageSwitchBinding.titlebar.setRightWidgetVisible(1);
        TextView complete = languageSwitchBinding.titlebar.findViewById(R.id.tv_right);
        complete.setTextColor(getResources().getColor(R.color.c5));
        languageSwitchBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightTextClick() {
                selectLanguage();
            }

            @Override
            public void rightImgClick() {

            }
        });
    }

    /**
     * 初始化点击事件
     */
    private void initClick() {
        languageSwitchBinding.simplifiedChinese.setOnClickListener(v -> {
            languageType = LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
            initLanguageType();
        });
        languageSwitchBinding.english.setOnClickListener(v -> {
            languageType = LanguageType.LANGUAGE_EN;
            initLanguageType();
        });
    }

    @Override
    protected void initData() {
        languageType = MultiLanguageUtil.getInstance().getLanguageType();
        initLanguageType();
    }

    /**
     * 初始化语言类型
     */
    private void initLanguageType() {
        languageSwitchBinding.tvSimplifiedChinese.setTextColor(languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED ? getResources().getColor(R.color.c5) : getResources().getColor(R.color.c8));
        languageSwitchBinding.tvEnglish.setTextColor(languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED ? getResources().getColor(R.color.c8) : getResources().getColor(R.color.c5));
        languageSwitchBinding.ivSimplifiedChinese.setVisibility(languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED ? View.VISIBLE : View.GONE);
        languageSwitchBinding.ivEnglish.setVisibility(languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置语言并重置app
     */
    public void selectLanguage() {
        if (languageType == MultiLanguageUtil.getInstance().getLanguageType()) {
            finish();
        } else {
            MultiLanguageUtil.getInstance().updateLanguage(languageType);
            Intent intent = new Intent(LanguageSwitchAct.this, MainAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
