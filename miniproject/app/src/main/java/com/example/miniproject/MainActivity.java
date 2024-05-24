package com.example.miniproject;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView tvMoney;
    CheckBox cb1, cb2, cb3;
    SeekBar sb1, sb2, sb3;
    EditText etBetMoney1, etBetMoney2, etBetMoney3;
    Button btnStart, btnReset;
    ImageView cat1, cat2;
    MediaPlayer backgroundMusicPlayer, startSoundPlayer, endSoundPlayer;
    private static final int MAX_PROGRESS = 1000;
    private static final int UPDATE_INTERVAL = 50; // Reduced interval for smoother updates
    Handler handler = new Handler();
    Random random = new Random();
    int progress1 = 0, progress2 = 0, progress3 = 0;
    boolean raceFinished = false;
    int totalMoney = 100;

    // Tree ImageViews
    ImageView tree11, tree12, tree13, tree14, tree21, tree22, tree23, tree24;

    boolean treeAnimationRunning = false;
    private MediaPlayer winSoundEffect;

    private  Button btnNap, btnLogOut;

    // Placeholder arrays for tree positions
    float[] tree1X = new float[4];
    float[] tree1Y = new float[4];
    float[] tree2X = new float[4];
    float[] tree2Y = new float[4];

    ObjectAnimator[] treeAnimators = new ObjectAnimator[8]; // Hold references to the animators

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Existing initialization code
        tvMoney = findViewById(R.id.tvMoney);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        sb1 = findViewById(R.id.sb1);
        sb2 = findViewById(R.id.sb2);
        sb3 = findViewById(R.id.sb3);
        etBetMoney1 = findViewById(R.id.etBetMoney1);
        etBetMoney2 = findViewById(R.id.etBetMoney2);
        etBetMoney3 = findViewById(R.id.etBetMoney3);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        cat1 = findViewById(R.id.cat1);
        cat2 = findViewById(R.id.cat2);

        // Initialize tree ImageViews
        tree11 = findViewById(R.id.tree11);
        tree12 = findViewById(R.id.tree12);
        tree13 = findViewById(R.id.tree13);
        tree14 = findViewById(R.id.tree14);
        tree21 = findViewById(R.id.tree21);
        tree22 = findViewById(R.id.tree22);
        tree23 = findViewById(R.id.tree23);
        tree24 = findViewById(R.id.tree24);

        btnNap = (Button) findViewById(R.id.btnNap);
        btnLogOut = (Button) findViewById(R.id.btnLogOut);

        initializeMediaPlayers();

        etBetMoney1.setEnabled(false);
        etBetMoney2.setEnabled(false);
        etBetMoney3.setEnabled(false);

        sb1.setEnabled(false);
        sb2.setEnabled(false);
        sb3.setEnabled(false);

        btnNap.setEnabled(false);

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetMoney1.setEnabled(isChecked);
            }
        });
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetMoney2.setEnabled(isChecked);
            }
        });
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBetMoney3.setEnabled(isChecked);
            }
        });

        if (savedInstanceState != null) {
            int savedMoney = savedInstanceState.getInt("money");
            int savedCat1 = savedInstanceState.getInt("cat1");
            int savedCat2 = savedInstanceState.getInt("cat2");
            int savedCat3 = savedInstanceState.getInt("cat3");

            tvMoney.setText(String.valueOf(savedMoney));
            sb1.setProgress(savedCat1);
            sb2.setProgress(savedCat2);
            sb3.setProgress(savedCat3);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBets()) {
                    playStartSound();
                    startRace();
                    startCatFlipAnimation();
                    startTreeAnimation();
                    etBetMoney1.setEnabled(false);
                    etBetMoney2.setEnabled(false);
                    etBetMoney3.setEnabled(false);
                    btnStart.setEnabled(false);
                    btnReset.setEnabled(false);
                    cb1.setEnabled(false);
                    cb2.setEnabled(false);
                    cb3.setEnabled(false);
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRace();
                initializeMediaPlayers();
            }
        });

        btnNap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalMoney == 0) {
                    totalMoney += 100;
                    tvMoney.setText(String.valueOf("$" +totalMoney));
                    btnNap.setEnabled(false);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_in.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayers();
    }

    private void initializeMediaPlayers() {
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.background);
        startSoundPlayer = MediaPlayer.create(this, R.raw.start);
        endSoundPlayer = MediaPlayer.create(this, R.raw.end);
        winSoundEffect = MediaPlayer.create(this, R.raw.win_sound);
    }

    private void releaseMediaPlayers() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
            backgroundMusicPlayer = null;
        }
        if (startSoundPlayer != null) {
            startSoundPlayer.release();
            startSoundPlayer = null;
        }
        if (endSoundPlayer != null) {
            endSoundPlayer.release();
            endSoundPlayer = null;
        }
        if (winSoundEffect != null) {
            winSoundEffect.release();
            winSoundEffect = null;
        }
    }

    private void playStartSound() {
        if (startSoundPlayer != null) {
            startSoundPlayer.start();
        }
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setLooping(true);
            backgroundMusicPlayer.start();
        }
    }

    private void playEndSound() {
        if (endSoundPlayer != null) {
            endSoundPlayer.start();
        }
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    private int[] validateCheckboxAndEdittext() {
        int bet1 = 0, bet2 = 0, bet3 = 0;

        if (cb1.isChecked() && !etBetMoney1.getText().toString().isEmpty()) {
            bet1 = Integer.parseInt(etBetMoney1.getText().toString());
        }
        if (cb2.isChecked() && !etBetMoney2.getText().toString().isEmpty()) {
            bet2 = Integer.parseInt(etBetMoney2.getText().toString());
        }
        if (cb3.isChecked() && !etBetMoney3.getText().toString().isEmpty()) {
            bet3 = Integer.parseInt(etBetMoney3.getText().toString());
        }

        return new int[]{bet1, bet2, bet3};
    }

    private boolean checkBets() {
        int[] bets = validateCheckboxAndEdittext();
        int bet1 = bets[0];
        int bet2 = bets[1];
        int bet3 = bets[2];

        int totalBet = bet1 + bet2 + bet3;

        if (totalBet > totalMoney) {
            Toast.makeText(this, "Combined bet exceeds total money available!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void startRace() {
        sb1.setMax(MAX_PROGRESS);
        sb2.setMax(MAX_PROGRESS);
        sb3.setMax(MAX_PROGRESS);

        raceFinished = false;
        progress1 = 0;
        progress2 = 0;
        progress3 = 0;
        sb1.setProgress(0);
        sb2.setProgress(0);
        sb3.setProgress(0);

        // Extract initial positions of trees
        tree1X[0] = tree11.getX();
        tree1X[1] = tree12.getX();
        tree1X[2] = tree13.getX();
        tree1X[3] = tree14.getX();
        tree1Y[0] = tree11.getY();
        tree1Y[1] = tree12.getY();
        tree1Y[2] = tree13.getY();
        tree1Y[3] = tree14.getY();

        tree2X[0] = tree21.getX();
        tree2X[1] = tree22.getX();
        tree2X[2] = tree23.getX();
        tree2X[3] = tree24.getX();
        tree2Y[0] = tree21.getY();
        tree2Y[1] = tree22.getY();
        tree2Y[2] = tree23.getY();
        tree2Y[3] = tree24.getY();

        handler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!raceFinished) {
                updateSeekBars();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };

    private void updateSeekBars() {
        progress1 += random.nextInt(25) + 1; // Updated range
        progress2 += random.nextInt(25) + 1; // Updated range
        progress3 += random.nextInt(25) + 1; // Updated range

        if (progress1 >= MAX_PROGRESS) {
            progress1 = MAX_PROGRESS;
            raceFinished = true;
        }
        if (progress2 >= MAX_PROGRESS) {
            progress2 = MAX_PROGRESS;
            raceFinished = true;
        }
        if (progress3 >= MAX_PROGRESS) {
            progress3 = MAX_PROGRESS;
            raceFinished = true;
        }

        sb1.setProgress(progress1);
        sb2.setProgress(progress2);
        sb3.setProgress(progress3);

        if (raceFinished) {
            handler.removeCallbacks(updateRunnable);
            playEndSound();
            determineWinner();
            if (cb1.isChecked()) {
                etBetMoney1.setEnabled(true);
            }
            if (cb2.isChecked()) {
                etBetMoney2.setEnabled(true);
            }
            if (cb3.isChecked()) {
                etBetMoney3.setEnabled(true);
            }
            btnStart.setEnabled(true);
            btnReset.setEnabled(true);
            cb1.setEnabled(true);
            cb2.setEnabled(true);
            cb3.setEnabled(true);

            stopTreeAnimations();
            resetTreePositions();

            if(totalMoney == 0) {
                btnNap.setEnabled(true);
            }
        }
    }

    private void determineWinner() {
        int winningCat = -1;

        if (progress1 >= MAX_PROGRESS) {
            winningCat = 1;
        } else if (progress2 >= MAX_PROGRESS) {
            winningCat = 2;
        } else if (progress3 >= MAX_PROGRESS) {
            winningCat = 3;
        }

        int[] bets = validateCheckboxAndEdittext();
        int bet1 = bets[0], bet2 = bets[1], bet3 = bets[2];

        if (progress1 >= MAX_PROGRESS) {
            totalMoney += bet1;
        } else {
            totalMoney -= bet1;
        }

        if (progress2 >= MAX_PROGRESS) {
            totalMoney += bet2;
        } else {
            totalMoney -= bet2;
        }

        if (progress3 >= MAX_PROGRESS) {
            totalMoney += bet3;
        } else {
            totalMoney -= bet3;
        }

        tvMoney.setText(String.valueOf("$" +totalMoney));

        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("winningCat", winningCat);
        intent.putExtra("totalMoney", totalMoney);
        intent.putExtra("cat1Progress", progress1);
        intent.putExtra("cat2Progress", progress2);
        intent.putExtra("cat3Progress", progress3);
        startActivity(intent);
    }

    private void resetRace() {
        sb1.setProgress(0);
        sb2.setProgress(0);
        sb3.setProgress(0);
        progress1 = 0;
        progress2 = 0;
        progress3 = 0;
        raceFinished = false;

        // Reset CheckBoxes
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);

        // Reset EditTexts
        etBetMoney1.setText("");
        etBetMoney2.setText("");
        etBetMoney3.setText("");

        // Disable EditTexts
        etBetMoney1.setEnabled(false);
        etBetMoney2.setEnabled(false);
        etBetMoney3.setEnabled(false);

        // Remove callbacks and stop animations
        handler.removeCallbacks(updateRunnable);
        releaseMediaPlayers();

        // Re-initialize media players
        initializeMediaPlayers();

        // Stop any ongoing animations and reset tree positions
        stopTreeAnimations();
        resetTreePositions();
    }

    private void stopTreeAnimations() {
        for (ObjectAnimator animator : treeAnimators) {
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    private void resetTreePositions() {
        tree11.setX(tree1X[0]);
        tree12.setX(tree1X[1]);
        tree13.setX(tree1X[2]);
        tree14.setX(tree1X[3]);
        tree21.setX(tree2X[0]);
        tree22.setX(tree2X[1]);
        tree23.setX(tree2X[2]);
        tree24.setX(tree2X[3]);
    }

    private void startCatFlipAnimation() {
        ObjectAnimator cat1FlipAnimator = ObjectAnimator.ofFloat(cat1, "rotationY", 0f, 360f);
        cat1FlipAnimator.setDuration(1000); // Set duration to 1 second
        cat1FlipAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely
        cat1FlipAnimator.start();

        ObjectAnimator cat2FlipAnimator = ObjectAnimator.ofFloat(cat2, "rotationY", 0f, 360f);
        cat2FlipAnimator.setDuration(1000); // Set duration to 1 second
        cat2FlipAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely
        cat2FlipAnimator.start();
    }

    private void startTreeAnimation() {
        treeAnimators[0] = animateTrees(tree11);
        treeAnimators[1] = animateTrees(tree12);
        treeAnimators[2] = animateTrees(tree13);
        treeAnimators[3] = animateTrees(tree14);

        treeAnimators[4] = animateTrees(tree21);
        treeAnimators[5] = animateTrees(tree22);
        treeAnimators[6] = animateTrees(tree23);
        treeAnimators[7] = animateTrees(tree24);
    }

    private ObjectAnimator animateTrees(ImageView tree) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(tree, "translationX", -300f); // Change to translationX
        animator.setDuration(2000); // Set duration for the animation
        animator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat the animation indefinitely
        animator.setRepeatMode(ObjectAnimator.RESTART); // Restart the animation when it reaches the end
        animator.start();
        return animator;
    }
}
