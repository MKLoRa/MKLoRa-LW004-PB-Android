package com.moko.lw004.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.moko.lw004.R;
import com.moko.lw004.R2;
import com.moko.lw004.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePasswordDialog extends BaseDialog<Object> {
    private final String FILTER_ASCII = "[ -~]*";
    @BindView(R2.id.et_password)
    EditText etPassword;
    @BindView(R2.id.et_password_confirm)
    EditText etPasswordConfirm;
    @BindView(R2.id.tv_password_ensure)
    TextView tvPasswordEnsure;
    private boolean passwordEnable;
    private boolean confirmPasswordEnable;

    public ChangePasswordDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.lw004_dialog_change_password;
    }

    @Override
    protected void renderConvertView(View convertView, Object object) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!(source + "").matches(FILTER_ASCII)) {
                    return "";
                }

                return null;
            }
        };
        etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8), filter});
        etPasswordConfirm.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8), filter});

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEnable = count > 0;
                tvPasswordEnsure.setEnabled(passwordEnable || confirmPasswordEnable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPasswordEnable = count > 0;
                tvPasswordEnsure.setEnabled(passwordEnable || confirmPasswordEnable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R2.id.tv_password_cancel)
    public void onCancel(View view) {
        dismiss();
    }

    @OnClick(R2.id.tv_password_ensure)
    public void onEnsure(View view) {
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();
        if (password.length() != 8) {
            ToastUtils.showToast(getContext(), getContext().getString(R.string.change_password_length));
            return;
        }
        if (passwordConfirm.length() != 8) {
            ToastUtils.showToast(getContext(), getContext().getString(R.string.change_password_length));
            return;
        }
        if (!password.equals(passwordConfirm)) {
            ToastUtils.showToast(getContext(), getContext().getString(R.string.change_password_match));
            return;
        }
        dismiss();
        if (passwordClickListener != null)
            passwordClickListener.onEnsureClicked(password);
    }

    private PasswordClickListener passwordClickListener;

    public void setOnPasswordClicked(PasswordClickListener passwordClickListener) {
        this.passwordClickListener = passwordClickListener;
    }

    public interface PasswordClickListener {

        void onEnsureClicked(String password);
    }

    public void showKeyboard() {
        if (etPassword != null) {
            //设置可获得焦点
            etPassword.setFocusable(true);
            etPassword.setFocusableInTouchMode(true);
            //请求获得焦点
            etPassword.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) etPassword
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etPassword, 0);
        }
    }
}
