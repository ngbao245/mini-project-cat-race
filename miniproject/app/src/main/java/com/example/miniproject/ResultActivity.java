package com.example.miniproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvWinningAmount;
    private TextView tvCat1Progress;
    private TextView tvCat2Progress;
    private TextView tvCat3Progress;
    private Button btnGoBack;
    private KonfettiView konfettiView;

    private static final int MAX_PROGRESS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialize UI elements
        tvWinningAmount = findViewById(R.id.tv_winning_amount);
        tvCat1Progress = findViewById(R.id.tv_cat1_progress);
        tvCat2Progress = findViewById(R.id.tv_cat2_progress);
        tvCat3Progress = findViewById(R.id.tv_cat3_progress);
        btnGoBack = findViewById(R.id.buttonBack);
        konfettiView = findViewById(R.id.konfettiView);

        // Set onClickListener for the button
        btnGoBack.setOnClickListener(this);

        // Configure KonfettiView
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0)
                .build();
        konfettiView.start(party);

        // Get data from intent
        int winningAmount = getIntent().getIntExtra("totalMoney", 0);
        int cat1Progress = getIntent().getIntExtra("cat1Progress", 0);
        int cat2Progress = getIntent().getIntExtra("cat2Progress", 0);
        int cat3Progress = getIntent().getIntExtra("cat3Progress", 0);

        int cat1ProgressPercentage = (cat1Progress * 100) / MAX_PROGRESS;
        int cat2ProgressPercentage = (cat2Progress * 100) / MAX_PROGRESS;
        int cat3ProgressPercentage = (cat3Progress * 100) / MAX_PROGRESS;


        // Set text for TextViews
        tvWinningAmount.setText(getString(R.string.amount_won, winningAmount) + "$");
        tvCat1Progress.setText(getString(R.string.cat_progress, 1, cat1ProgressPercentage));
        tvCat2Progress.setText(getString(R.string.cat_progress, 2, cat2ProgressPercentage));
        tvCat3Progress.setText(getString(R.string.cat_progress, 3, cat3ProgressPercentage));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonBack) {
            finish();
        }
    }
}
