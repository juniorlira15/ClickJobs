package com.victall.clickjobs.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.victall.clickjobs.R;
import com.victall.clickjobs.databinding.ActivityScrollingBinding;


public class MeusServicosActivity extends AppCompatActivity {

    private ActivityScrollingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
//        CollapsingToolbarLayout toolBarLayout = binding.toolbarMeusSericos;
//        toolBarLayout.setTitle(getTitle());
//
//        FloatingActionButton actionButton = binding.fabMeusServicos;
//        actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MeusServicosActivity.this, "asdsadsd", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}