/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.settings;

import android.annotation.Nullable;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Base fragment to be shared for PIN/Pattern/Password confirmation fragments.
 */
public abstract class ConfirmDeviceCredentialBaseFragment extends InstrumentedFragment
        implements FingerprintUiHelper.Callback {

    public static final String PACKAGE = "com.android.settings";
    public static final String TITLE_TEXT = PACKAGE + ".ConfirmCredentials.title";
    public static final String HEADER_TEXT = PACKAGE + ".ConfirmCredentials.header";
    public static final String DETAILS_TEXT = PACKAGE + ".ConfirmCredentials.details";
    public static final String ALLOW_FP_AUTHENTICATION =
            PACKAGE + ".ConfirmCredentials.allowFpAuthentication";
    public static final String DARK_THEME = PACKAGE + ".ConfirmCredentials.darkTheme";
    public static final String SHOW_CANCEL_BUTTON =
            PACKAGE + ".ConfirmCredentials.showCancelButton";
    public static final String SHOW_WHEN_LOCKED =
            PACKAGE + ".ConfirmCredentials.showWhenLocked";

    private FingerprintUiHelper mFingerprintHelper;
    private boolean mAllowFpAuthentication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllowFpAuthentication = getActivity().getIntent().getBooleanExtra(
                ALLOW_FP_AUTHENTICATION, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFingerprintHelper = new FingerprintUiHelper(
                (ImageView) view.findViewById(R.id.fingerprintIcon),
                (TextView) view.findViewById(R.id.errorText), this);
        boolean showCancelButton = getActivity().getIntent().getBooleanExtra(
                SHOW_CANCEL_BUTTON, false);
        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setVisibility(showCancelButton ? View.VISIBLE : View.GONE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAllowFpAuthentication) {
            mFingerprintHelper.startListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAllowFpAuthentication) {
            mFingerprintHelper.stopListening();
        }
    }

    @Override
    public void onAuthenticated() {
        // Check whether we are still active.
        if (getActivity() != null && getActivity().isResumed()) {
            authenticationSucceeded();
        }
    }

    protected abstract void authenticationSucceeded();

    @Override
    public void onFingerprintIconVisibilityChanged(boolean visible) {
    }
}
