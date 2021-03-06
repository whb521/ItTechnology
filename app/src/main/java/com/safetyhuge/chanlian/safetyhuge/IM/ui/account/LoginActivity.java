package com.safetyhuge.chanlian.safetyhuge.IM.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.safetyhuge.chanlian.safetyhuge.IM.common.CCPAppManager;
import com.safetyhuge.chanlian.safetyhuge.IM.common.base.CCPFormInputView;
import com.safetyhuge.chanlian.safetyhuge.IM.common.dialog.ECProgressDialog;
import com.safetyhuge.chanlian.safetyhuge.IM.common.utils.ECPreferenceSettings;
import com.safetyhuge.chanlian.safetyhuge.IM.common.utils.ECPreferences;
import com.safetyhuge.chanlian.safetyhuge.IM.common.utils.FileAccessor;
import com.safetyhuge.chanlian.safetyhuge.IM.common.utils.ToastUtil;
import com.safetyhuge.chanlian.safetyhuge.IM.core.ClientUser;
import com.safetyhuge.chanlian.safetyhuge.IM.core.ContactsCache;
import com.safetyhuge.chanlian.safetyhuge.IM.storage.ContactSqlManager;
import com.safetyhuge.chanlian.safetyhuge.IM.ui.ECSuperActivity;
import com.safetyhuge.chanlian.safetyhuge.IM.ui.LauncherActivity;
import com.safetyhuge.chanlian.safetyhuge.IM.ui.SDKCoreHelper;
import com.safetyhuge.chanlian.safetyhuge.IM.ui.contact.ContactLogic;
import com.safetyhuge.chanlian.safetyhuge.IM.ui.contact.ECContacts;
import com.safetyhuge.chanlian.safetyhuge.IM.ui.settings.LoginSettingActivity;
import com.safetyhuge.chanlian.safetyhuge.R;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;

import java.io.InvalidClassException;
import java.util.ArrayList;

/**
 * Created by Jorstin on 2015/3/18.
 */
public class LoginActivity extends ECSuperActivity implements
		View.OnClickListener, OnLongClickListener {

	private EditText ipEt;
	private EditText portEt;
	private EditText appkeyEt;
	private EditText tokenEt;
	private EditText mobileEt;
	private EditText mVoipEt;
	private Button signBtn;
	private CCPFormInputView mFormInputView;
	private CCPFormInputView mFormInputViewPassword;
	private ECProgressDialog mPostingdialog;
	ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initResourceRefs();

		getTopBarView().setTopBarToStatus(1, -1,
				R.drawable.btn_style_green,
				null,
				getString(R.string.app_title_switch),
				getString(R.string.app_name), null, this);
		getTopBarView().getmMiddleButton().setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				startActivity(new Intent(LoginActivity.this, ECSetUpServerActivity.class));
				return false;
			}
		});
		

		registerReceiver(new String[] { SDKCoreHelper.ACTION_SDK_CONNECT });

	}

	@Override
	protected void onResume() {

		super.onResume();
		initConfig();
	}

	private void initConfig() {

		String appkey = FileAccessor.getAppKey();
		String token = FileAccessor.getAppToken();
		appkeyEt.setText(appkey);
		tokenEt.setText(token);

		if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(token)) {
			signBtn.setEnabled(false);
			ToastUtil.showMessage(R.string.app_server_config_error_tips);
		}
	}

	private void initResourceRefs() {
		ipEt = (EditText) findViewById(R.id.ip);
		portEt = (EditText) findViewById(R.id.port);
		appkeyEt = (EditText) findViewById(R.id.appkey);
		tokenEt = (EditText) findViewById(R.id.token);
		mFormInputView = (CCPFormInputView) findViewById(R.id.mobile);
		mobileEt = mFormInputView.getFormInputEditView();
//		mobileEt.setInputType(InputType.TYPE_CLASS_PHONE);
		mFormInputViewPassword = (CCPFormInputView) findViewById(R.id.VoIP_mode);
		mVoipEt = mFormInputViewPassword.getFormInputEditView();
		// mVoipEt.setInputType(InputType.TYPE_CLASS_PHONE);
		mobileEt.requestFocus();
		// mobileEt.setText(ECSDKUtils.getLine1Number(this));
		signBtn = (Button) findViewById(R.id.sign_in_button);
		findViewById(R.id.server_config).setOnLongClickListener(this);
		signBtn.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityInit() {
		// super.onActivityInit();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_login;
	}

	private boolean flag = true;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.sign_in_button:
			hideSoftKeyboard();
			String mobile = mobileEt.getText().toString().trim();
			String pass = mVoipEt.getText().toString().trim();
			if (mLoginAuthType == ECInitParams.LoginAuthType.NORMAL_AUTH
					&& TextUtils.isEmpty(mobile)) {
				ToastUtil.showMessage(R.string.input_mobile_error);
				return;
			} else if (mLoginAuthType == ECInitParams.LoginAuthType.PASSWORD_AUTH) {
				if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pass)) {
					ToastUtil.showMessage(R.string.app_input_paras_error);
					return;
				}

			}
			

			String appKey = appkeyEt.getText().toString().trim();
			String token = tokenEt.getText().toString().trim();
			ClientUser clientUser = new ClientUser(mobile);
			clientUser.setAppKey(appKey);
			clientUser.setAppToken(token);
			clientUser.setLoginAuthType(mLoginAuthType);
			clientUser.setPassword(pass);
			CCPAppManager.setClientUser(clientUser);
			
//			if(!PatternUtils.isShuZiYing(mobile)){
//				ToastUtil.showMessage("输入的账号不合法");
//				return;
//			}
			mPostingdialog = new ECProgressDialog(this, R.string.login_posting);
			mPostingdialog.show();
			
			SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);
			break;
		case R.id.text_right:
			switchAccountInput();
			break;
		case R.id.text_left:
			break;
		default:
			break;
		}
	}

	private void switchAccountInput() {
		if (mLoginAuthType == ECInitParams.LoginAuthType.NORMAL_AUTH) {
			// 普通登陆模式
			mLoginAuthType = ECInitParams.LoginAuthType.PASSWORD_AUTH;
			mFormInputView .setInputTitle(getString(R.string.login_prompt_VoIP_account));
			mobileEt.setHint(R.string.login_prompt_VoIP_account_tips);
			mFormInputViewPassword.setVisibility(View.VISIBLE);
		} else {
			// 密码登陆模式
			mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;
			mFormInputView .setInputTitle(getString(R.string.login_prompt_mobile));
			mobileEt.setHint(R.string.login_prompt_mobile_hint);
			mFormInputViewPassword.setVisibility(View.GONE);
		}

	}

	/**
	 * 关闭对话框
	 */
	private void dismissPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x2a) {
			doLauncherAction();
		}
	}

	private void doLauncherAction() {
		try {
			Intent intent = new Intent(this, LauncherActivity.class);
			intent.putExtra("launcher_from", 1);
			// 注册成功跳转
			startActivity(intent);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveAccount() throws InvalidClassException {
		String appKey = appkeyEt.getText().toString().trim();
		String token = tokenEt.getText().toString().trim();
		String mobile = mobileEt.getText().toString().trim();
		String voippass = mVoipEt.getText().toString().trim();
		ClientUser user = CCPAppManager.getClientUser();
		if(user == null) {
			user = new ClientUser(mobile);
		} else {
			user.setUserId(mobile);
		}
		user.setAppToken(token);
		user.setAppKey(appKey);
		user.setPassword(voippass);
		user.setLoginAuthType(mLoginAuthType);
		CCPAppManager.setClientUser(user);
		ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_REGIST_AUTO,user.toString(), true);
		// ContactSqlManager.insertContacts(contacts);
		ArrayList<ECContacts> objects = ContactLogic.initContacts();
		objects = ContactLogic.converContacts(objects);
		ContactSqlManager.insertContacts(objects);
	}

	@Override
	protected void handleReceiver(Context context, Intent intent) {
		// super.handleReceiver(context, intent);
		int error = intent.getIntExtra("error", -1);
		if (SDKCoreHelper.ACTION_SDK_CONNECT.equals(intent.getAction())) {
			// 初始注册结果，成功或者失败
			if (SDKCoreHelper.getConnectState() == ECDevice.ECConnectState.CONNECT_SUCCESS
					&& error == SdkErrorCode.REQUEST_SUCCESS) {

				dismissPostingDialog();
				try {
					saveAccount();
				} catch (InvalidClassException e) {
					e.printStackTrace();
				}
				ContactsCache.getInstance().load();
				doLauncherAction();
				return;
			}
			if (intent.hasExtra("error")) {
				if (SdkErrorCode.CONNECTTING == error) {
					return;
				}
				if (error == -1) {
					ToastUtil.showMessage("请检查登陆参数是否正确[" + error + "]");
				}else {
					dismissPostingDialog();
				}
				ToastUtil.showMessage("登录失败，请稍后重试[" + error + "]");
			}
			dismissPostingDialog();
		}
		
		
	}

	@Override
	protected boolean isEnableSwipe() {
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		startActivity(new Intent(this, LoginSettingActivity.class));
		return false;
	}

	@Override
	public boolean isEnableRightSlideGesture() {
		return false;
	}

	@Override
	public void abstracrRegist() {
	}
}
