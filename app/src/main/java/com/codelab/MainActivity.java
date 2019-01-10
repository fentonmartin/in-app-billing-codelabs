/*
 * Copyright 2017 Google Inc. All Rights Reserved.
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
 * limitations under the License.
 */

package com.codelab;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.codelab.billing.BillingManager;
import com.codelab.billing.BillingProvider;
import com.codelab.billing.sku.SkuFragment;
import com.codelab.sample.R;

/**
 * Example game using Play Billing library.
 * <p>
 * Please follow steps inside the codelab to understand the best practices for this new library.
 */
public class MainActivity extends FragmentActivity implements BillingProvider {

    // Debug tag, for logging
    private static final String TAG = "MainActivity";
    // Tag for a dialog that allows us to find it when screen was rotated
    private static final String DIALOG_TAG = "dialog";

    private BillingManager mBillingManager;
    private SkuFragment mSkuFragment;
    private MainController mMainController;

    private View mScreenWait, mScreenMain;
    private ImageView mGasImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the controller and load game data
        mMainController = new MainController(this);

        // Try to restore dialog fragment if we were showing it prior to screen rotation
        if (savedInstanceState != null) {
            mSkuFragment = (SkuFragment) getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        }
        initBilling();

        mScreenWait = findViewById(R.id.screen_wait);
        mScreenMain = findViewById(R.id.screen_main);
        mGasImageView = findViewById(R.id.gas_gauge);

        // Specify purchase and drive buttons listeners
        // Note: This couldn't be done inside *.xml for Android TV since TV layout is inflated via AppCompat
        findViewById(R.id.button_purchase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Purchase button clicked.");
                startBilling();
            }
        });
        findViewById(R.id.button_drive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDriveButtonClicked(view);
            }
        });

        showRefreshedUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBillingManager.destroy();
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    private void initBilling() {
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this);
    }

    public void startBilling() {
        if (mSkuFragment == null) {
            mSkuFragment = new SkuFragment();
        }
        if (!isSkuFragmentShown()) {
            mSkuFragment.show(getSupportFragmentManager(), DIALOG_TAG);
        }
    }

    public boolean isSkuFragmentShown() {
        return mSkuFragment != null && mSkuFragment.isVisible();
    }

    /**
     * Drive button clicked. Burn gas!
     */
    public void onDriveButtonClicked(View arg0) {
        Log.d(TAG, "Drive button clicked.");

        if (mMainController.isTankEmpty()) {
            alert(R.string.alert_no_gas);
        } else {
            mMainController.useGas();
            alert(R.string.alert_drove);
            updateUi();
        }
    }

    /**
     * Remove loading spinner and refresh the UI
     */
    public void showRefreshedUi() {
        setWaitScreen(false);
        updateUi();

        if (isSkuFragmentShown()) {
            mSkuFragment.refreshUI();
        }
    }

    /**
     * Show an alert dialog to the user
     *
     * @param messageId String id to display inside the alert dialog
     */
    @UiThread
    void alert(@StringRes int messageId) {
        alert(messageId, null);
    }

    /**
     * Show an alert dialog to the user
     *
     * @param messageId     String id to display inside the alert dialog
     * @param optionalParam Optional attribute for the string
     */
    @UiThread
    void alert(@StringRes int messageId, Object optionalParam) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("Dialog could be shown only from the main thread");
        }

        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setNeutralButton("OK", null);

        if (optionalParam == null) {
            bld.setMessage(messageId);
        } else {
            bld.setMessage(getResources().getString(messageId, optionalParam));
        }
        bld.create().show();
    }

    /**
     * Enables or disables the "please wait" screen.
     */
    private void setWaitScreen(boolean set) {
        mScreenMain.setVisibility(set ? View.GONE : View.VISIBLE);
        mScreenWait.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    /**
     * Update UI to reflect model
     */
    @UiThread
    private void updateUi() {
        Log.d(TAG, "Updating the UI. Thread: " + Thread.currentThread().getName());

        // Update gas gauge to reflect tank status
        mGasImageView.setImageResource(mMainController.getTankResId());
    }
}
