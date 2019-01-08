package com.ov3rk1ll.kinocast.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ov3rk1ll.kinocast.api.DeeplinkParser;
import com.ov3rk1ll.kinocast.api.Parser;
import com.ov3rk1ll.kinocast.api.mirror.Host;
import com.ov3rk1ll.kinocast.data.ViewModel;
import com.ov3rk1ll.kinocast.utils.Utils;


public class DeeplinkActivity extends AppCompatActivity {

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        url = getIntent().getData().toString();
        if(!Utils.isStringEmpty(url)){
            DeeplinkParser dp = new DeeplinkParser();
            ViewModel viewModel =  dp.loadDetail(url);

            if(viewModel != null){
                Parser.setInstance(dp);
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.ARG_ITEM, viewModel);
                intent.putExtra(DetailActivity.ARG_ITEM, viewModel);
                startActivity(intent);
                finish();
                url = null;
                return;
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
