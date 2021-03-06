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
package com.codelab.billing.sku;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codelab.sample.R;

/**
 * SkuHolder for quick access to row's views
 */
public final class SkuHolder extends RecyclerView.ViewHolder {

    public TextView title, description, price;
    public Button button;
    public ImageView skuIcon;

    /**
     * Handler for a button click on particular row
     */
    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }

    public SkuHolder(final View itemView, final OnButtonClickListener clickListener) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        price = itemView.findViewById(R.id.price);
        description = itemView.findViewById(R.id.description);
        skuIcon = itemView.findViewById(R.id.sku_icon);
        button = itemView.findViewById(R.id.state_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onButtonClicked(getAdapterPosition());
                }
            });
        }
    }
}
