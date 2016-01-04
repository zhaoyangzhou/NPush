package com.push.m.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.push.m.R;

/**
 * 使用的是开源工程 ActionSheetForAndroid-master 
 * 		https://github.com/baoyongzhang/ActionSheetForAndroid
 * UIActionSheet
 * @author baoyz
 * @date 2014-6-6
 *
 */
public class ActionSheet extends Fragment implements OnClickListener {

	private static final String ARG_TITLE = "title";
	private static final String ARG_CANCEL_BUTTON_TITLE = "cancel_button_title";
	private static final String ARG_OTHER_BUTTON_TITLES = "other_button_titles";
	private static final String ARG_OTHER_BUTTON_ICONS = "other_button_icons";
	private static final String ARG_CANCELABLE_ONTOUCHOUTSIDE = "cancelable_ontouchoutside";
	private static final int CANCEL_BUTTON_ID = 100;
	private static final int BG_VIEW_ID = 10;
	private static final int TRANSLATE_DURATION = 200;
	private static final int ALPHA_DURATION = 300;

	private boolean mDismissed = true;
	private ActionSheetListener mListener;
	private View mView;
	private LinearLayout mPanel;
	private ViewGroup mGroup;
	private View mBg;
	private Attributes mAttrs;
	private boolean isCancel = true;

	public void show(FragmentManager manager, String tag) {
		if (!mDismissed) {
			return;
		}
		mDismissed = false;
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.addToBackStack(null);
		ft.commit();
	}

	public void dismiss() {
		if (mDismissed) {
			return;
		}
		mDismissed = true;
		getFragmentManager().popBackStack();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.remove(this);
		ft.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			View focusView = getActivity().getCurrentFocus();
			if (focusView != null) {
				imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
			}
		}

		mAttrs = readAttribute();

		mView = createView();
		mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

		createItems();

		mGroup.addView(mView);
		mBg.startAnimation(createAlphaInAnimation());
		mPanel.startAnimation(createTranslationInAnimation());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(TRANSLATE_DURATION);
		return an;
	}

	private Animation createAlphaInAnimation() {
		AlphaAnimation an = new AlphaAnimation(0, 1);
		an.setDuration(ALPHA_DURATION);
		return an;
	}

	private Animation createTranslationOutAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				0, type, 1);
		an.setDuration(TRANSLATE_DURATION);
		an.setFillAfter(true);
		return an;
	}

	private Animation createAlphaOutAnimation() {
		AlphaAnimation an = new AlphaAnimation(1, 0);
		an.setDuration(ALPHA_DURATION);
		an.setFillAfter(true);
		return an;
	}

	private View createView() {
		FrameLayout parent = new FrameLayout(getActivity());
		parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mBg = new View(getActivity());
		mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mBg.setBackgroundColor(Color.argb(136, 0, 0, 0));
		mBg.setId(BG_VIEW_ID);
		mBg.setOnClickListener(this);

		mPanel = new LinearLayout(getActivity());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		mPanel.setLayoutParams(params);
		mPanel.setOrientation(LinearLayout.VERTICAL);

		parent.addView(mBg);
		parent.addView(mPanel);
		return parent;
	}

	private void createItems() {
		int rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, getActivity().getResources().getDisplayMetrics());
		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconParams.setMargins(0, 0, rightMargin, 0);
		
		/**标题*/
		TextView title = new TextView(getActivity());
		title.setBackgroundDrawable(mAttrs.otherButtonTopBackground);
		title.setText(getTitle());
		title.setTextColor(mAttrs.cancelButtonTextColor);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.titleTextSize);
		title.setGravity(Gravity.CENTER);
		mPanel.addView(title);
				
		String[] titles = getOtherButtonTitles();
		int[] icons = getOtherButtonIcons();
		if (titles != null) {
			for (int i = 0; i < titles.length; i++) {
				LinearLayout buttonLine = new LinearLayout(getActivity());
				buttonLine.setGravity(Gravity.CENTER);
				buttonLine.setOrientation(LinearLayout.HORIZONTAL);
				buttonLine.setId(CANCEL_BUTTON_ID + i + 1);
				buttonLine.setOnClickListener(this);
				buttonLine.setBackgroundDrawable(getOtherButtonBg(titles, i));
				
				if(icons != null && icons[i] > 0) {
					ImageView icon = new ImageView(getActivity());
					icon.setImageResource(icons[i]);
					icon.setLayoutParams(iconParams);
					buttonLine.addView(icon);
				}
				
				
				TextView bt = new TextView(getActivity());
				bt.setText(titles[i]);
				bt.setTextColor(mAttrs.otherButtonTextColor);
				bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
				buttonLine.addView(bt);
				
				LinearLayout.LayoutParams params = createButtonLayoutParams();
				params.topMargin = mAttrs.otherButtonSpacing;
				mPanel.addView(buttonLine, params);
			}
		}
		Button bt = new Button(getActivity());
		bt.getPaint().setFakeBoldText(true);
		bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
		bt.setId(CANCEL_BUTTON_ID);
		bt.setBackgroundDrawable(mAttrs.cancelButtonBackground);
		bt.setText(getCancelButtonTitle());
		bt.setTextColor(mAttrs.cancelButtonTextColor);
		bt.setOnClickListener(this);
		LinearLayout.LayoutParams params = createButtonLayoutParams();
		params.topMargin = mAttrs.cancelButtonMarginTop;
		mPanel.addView(bt, params);

		mPanel.setBackgroundDrawable(mAttrs.background);
		mPanel.setPadding(mAttrs.padding, mAttrs.padding, mAttrs.padding,
				mAttrs.padding);
	}

	public LinearLayout.LayoutParams createButtonLayoutParams() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		return params;
	}

	private Drawable getOtherButtonBg(String[] titles, int i) {
		if (i == (titles.length - 1)) {
			return mAttrs.otherButtonBottomBackground;
		} else {
			return mAttrs.getOtherButtonMiddleBackground();
		}
	}

	@Override
	public void onDestroyView() {
		mPanel.startAnimation(createTranslationOutAnimation());
		mBg.startAnimation(createAlphaOutAnimation());
		mView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mGroup.removeView(mView);
			}
		}, ALPHA_DURATION);
		if (mListener != null) {
			mListener.onDismiss(this, isCancel);
		}
		super.onDestroyView();
	}

	private Attributes readAttribute() {
		Attributes attrs = new Attributes(getActivity());
		TypedArray a = getActivity().getTheme().obtainStyledAttributes(null,
				R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
		Drawable background = a
				.getDrawable(R.styleable.ActionSheet_actionSheetBackground);
		if (background != null) {
			attrs.background = background;
		}
		Drawable cancelButtonBackground = a
				.getDrawable(R.styleable.ActionSheet_cancelButtonBackground);
		if (cancelButtonBackground != null) {
			attrs.cancelButtonBackground = cancelButtonBackground;
		}
		Drawable otherButtonTopBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonTopBackground);
		if (otherButtonTopBackground != null) {
			attrs.otherButtonTopBackground = otherButtonTopBackground;
		}
		Drawable otherButtonMiddleBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
		if (otherButtonMiddleBackground != null) {
			attrs.otherButtonMiddleBackground = otherButtonMiddleBackground;
		}
		Drawable otherButtonBottomBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonBottomBackground);
		if (otherButtonBottomBackground != null) {
			attrs.otherButtonBottomBackground = otherButtonBottomBackground;
		}
		Drawable otherButtonSingleBackground = a
				.getDrawable(R.styleable.ActionSheet_otherButtonSingleBackground);
		if (otherButtonSingleBackground != null) {
			attrs.otherButtonSingleBackground = otherButtonSingleBackground;
		}
		attrs.cancelButtonTextColor = a.getColor(
				R.styleable.ActionSheet_cancelButtonTextColor,
				attrs.cancelButtonTextColor);
		attrs.otherButtonTextColor = a.getColor(
				R.styleable.ActionSheet_otherButtonTextColor,
				attrs.otherButtonTextColor);
		attrs.padding = (int) a.getDimension(
				R.styleable.ActionSheet_actionSheetPadding, attrs.padding);
		attrs.otherButtonSpacing = (int) a.getDimension(
				R.styleable.ActionSheet_otherButtonSpacing,
				attrs.otherButtonSpacing);
		attrs.cancelButtonMarginTop = (int) a.getDimension(
				R.styleable.ActionSheet_cancelButtonMarginTop,
				attrs.cancelButtonMarginTop);
		attrs.actionSheetTextSize = a.getDimensionPixelSize(R.styleable.ActionSheet_actionSheetTextSize, (int) attrs.actionSheetTextSize);
		attrs.titleTextSize = a.getDimensionPixelSize(R.styleable.ActionSheet_titleTextSize, (int) attrs.titleTextSize);

		a.recycle();
		return attrs;
	}
	
	private String getTitle() {
		return getArguments().getString(ARG_TITLE);
	}

	private String getCancelButtonTitle() {
		return getArguments().getString(ARG_CANCEL_BUTTON_TITLE);
	}

	private String[] getOtherButtonTitles() {
		return getArguments().getStringArray(ARG_OTHER_BUTTON_TITLES);
	}
	
	private int[] getOtherButtonIcons() {
		return getArguments().getIntArray(ARG_OTHER_BUTTON_ICONS);
	}

	private boolean getCancelableOnTouchOutside() {
		return getArguments().getBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE);
	}

	public void setActionSheetListener(ActionSheetListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == BG_VIEW_ID && !getCancelableOnTouchOutside()) {
			return;
		}
		dismiss();
		if (v.getId() != CANCEL_BUTTON_ID && v.getId() != BG_VIEW_ID) {
			if (mListener != null) {
				mListener.onOtherButtonClick(this, v.getId() - CANCEL_BUTTON_ID
						- 1);
			}
			isCancel = false;
		}
	}

	public static Builder createBuilder(Context context,
			FragmentManager fragmentManager) {
		return new Builder(context, fragmentManager);
	}

	private static class Attributes {
		private Context mContext;

		public Attributes(Context context) {
			mContext = context;
			this.background = new ColorDrawable(Color.TRANSPARENT);
			this.cancelButtonBackground = new ColorDrawable(Color.BLACK);
			ColorDrawable gray = new ColorDrawable(Color.GRAY);
			this.otherButtonTopBackground = gray;
			this.otherButtonMiddleBackground = gray;
			this.otherButtonBottomBackground = gray;
			this.otherButtonSingleBackground = gray;
			this.cancelButtonTextColor = Color.WHITE;
			this.otherButtonTextColor = Color.BLACK;
			this.padding = dp2px(20);
			this.otherButtonSpacing = dp2px(2);
			this.cancelButtonMarginTop = dp2px(10);
			this.actionSheetTextSize = dp2px(16);
			this.titleTextSize = dp2px(12);
		}
		
		private int dp2px(int dp){
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					dp, mContext.getResources().getDisplayMetrics());
		}

		public Drawable getOtherButtonMiddleBackground() {
			if (otherButtonMiddleBackground instanceof StateListDrawable) {
				TypedArray a = mContext.getTheme().obtainStyledAttributes(null,
						R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
				otherButtonMiddleBackground = a
						.getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
				a.recycle();
			}
			return otherButtonMiddleBackground;
		}

		Drawable background;
		Drawable cancelButtonBackground;
		Drawable otherButtonTopBackground;
		Drawable otherButtonMiddleBackground;
		Drawable otherButtonBottomBackground;
		Drawable otherButtonSingleBackground;
		int cancelButtonTextColor;
		int otherButtonTextColor;
		int padding;
		int otherButtonSpacing;
		int cancelButtonMarginTop;
		float actionSheetTextSize;
		float titleTextSize;
	}

	public static class Builder {

		private Context mContext;
		private FragmentManager mFragmentManager;
		private String mTitle;
		private String mCancelButtonTitle;
		private String[] mOtherButtonTitles;
		private int[] mOtherButtonIcons;
		private String mTag = "actionSheet";
		private boolean mCancelableOnTouchOutside;
		private ActionSheetListener mListener;

		public Builder(Context context, FragmentManager fragmentManager) {
			mContext = context;
			mFragmentManager = fragmentManager;
		}

		public Builder setCancelButtonTitle(String title) {
			mCancelButtonTitle = title;
			return this;
		}

		public Builder setCancelButtonTitle(int strId) {
			return setCancelButtonTitle(mContext.getString(strId));
		}

		public Builder setOtherButtonTitles(String... titles) {
			mOtherButtonTitles = titles;
			return this;
		}
		
		public Builder setOtherButtonIcons(int... icons) {
			mOtherButtonIcons = icons;
			return this;
		}
		
		public Builder setTitle(String title) {
			mTitle = title;
			return this;
		}

		public Builder setTag(String tag) {
			mTag = tag;
			return this;
		}

		public Builder setListener(ActionSheetListener listener) {
			this.mListener = listener;
			return this;
		}

		public Builder setCancelableOnTouchOutside(boolean cancelable) {
			mCancelableOnTouchOutside = cancelable;
			return this;
		}

		public Bundle prepareArguments() {
			Bundle bundle = new Bundle();
			bundle.putString(ARG_TITLE, mTitle);
			bundle.putString(ARG_CANCEL_BUTTON_TITLE, mCancelButtonTitle);
			bundle.putStringArray(ARG_OTHER_BUTTON_TITLES, mOtherButtonTitles);
			bundle.putIntArray(ARG_OTHER_BUTTON_ICONS, mOtherButtonIcons);
			bundle.putBoolean(ARG_CANCELABLE_ONTOUCHOUTSIDE,
					mCancelableOnTouchOutside);
			return bundle;
		}

		public ActionSheet show() {
			ActionSheet actionSheet = (ActionSheet) Fragment.instantiate(
					mContext, ActionSheet.class.getName(), prepareArguments());
			actionSheet.setActionSheetListener(mListener);
			actionSheet.show(mFragmentManager, mTag);
			return actionSheet;
		}

	}

	public static interface ActionSheetListener {

		void onDismiss(ActionSheet actionSheet, boolean isCancel);

		void onOtherButtonClick(ActionSheet actionSheet, int index);
	}

}