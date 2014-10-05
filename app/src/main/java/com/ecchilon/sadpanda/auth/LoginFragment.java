package com.ecchilon.sadpanda.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ecchilon.sadpanda.R;
import com.google.inject.Inject;

import roboguice.fragment.RoboDialogFragment;
import roboguice.util.Strings;

/**
 * Created by Alex on 21-9-2014.
 */
public class LoginFragment extends RoboDialogFragment {

    public interface LoginListener {
        void onSuccess();
    }

    @Inject
    private ExhentaiAuth mExhentaiAuth;

    private LoginListener mAuthListener;

    private View mLoginFormView;
    private View mProgressView;

    private EditText mUsername;
    private EditText mPassword;

    public void setLoginListener(LoginListener listener) {
        mAuthListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(mAuthListener == null) {
            throw new IllegalArgumentException("LoginListener can't be null!");
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View loginView = LayoutInflater.from(getActivity()).inflate(R.layout.exhentai_login, null);

        mLoginFormView = loginView.findViewById(R.id.login_form);
        mProgressView = loginView.findViewById(R.id.login_progress);

        mUsername = (EditText) loginView.findViewById(R.id.username);
        mPassword = (EditText) loginView.findViewById(R.id.password);

        FormWatcher watcher =new FormWatcher();
        mUsername.addTextChangedListener(watcher);
        mPassword.addTextChangedListener(watcher);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND) {
                    performLogin();
                    return true;
                }

                return false;
            }
        });

        builder.setView(loginView);
        builder.setTitle(R.string.login_title);
        builder.setPositiveButton(R.string.action_sign_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE) {
                    performLogin();
                }
            }
        });

        return builder.create();
    }

    private void performLogin() {
        showProgress(true);
        mExhentaiAuth.login(mUsername.getText().toString(), mPassword.getText().toString(),
                new ExhentaiAuth.AuthListener() {
                    @Override
                    public void onSuccess() {
                        dismiss();
                        mAuthListener.onSuccess();
                    }

                    @Override
                    public void onFailure(ExhentaiAuth.ExhentaiError error) {
                        showProgress(false);
                        mUsername.setError(error.getErrorMessage());
                        mUsername.requestFocus();
                    }
                }
        );
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class FormWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            if(Strings.isEmpty(s)) {
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            }
            else {
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

    }
}