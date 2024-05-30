package com.example.tic_tac_toe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    boolean[][] specialCondition = {{false, false, false},
            {false, false, false},
            {false, false, false}};

    ImageView theImageWeAreLookingFor;

    boolean[][] userState = {{false, false, false},
            {false, false, false},
            {false, false, false}};

    boolean currentUser = true;

    int counter = 0;
    boolean gameWon = false;

    // MediaPlayer for background music
    private MediaPlayer mediaPlayer;

    // SoundPool for sound effects
    private SoundPool soundPool;
    private int victorySoundId;
    private int drawSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);

        // Initialize SoundPool
        soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        victorySoundId = soundPool.load(this, R.raw.victory_sound, 1);
        drawSoundId = soundPool.load(this, R.raw.draw_sound, 1);

        // Find all the ImageViews
        ImageView[] imageViews = new ImageView[]{
                findViewById(R.id.i0),
                findViewById(R.id.i1),
                findViewById(R.id.i2),
                findViewById(R.id.i3),
                findViewById(R.id.i4),
                findViewById(R.id.i5),
                findViewById(R.id.i6),
                findViewById(R.id.i7),
                findViewById(R.id.i8)
        };

        // Set click listeners for each ImageView
        for (final ImageView imageView : imageViews) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click for each ImageView
                    theGameMethod(imageView);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Play victory sound effect
    private void playVictorySound() {
        soundPool.play(victorySoundId, 1, 1, 1, 0, 1);
    }

    // Play draw sound effect
    private void playDrawSound() {
        soundPool.play(drawSoundId, 1, 1, 1, 0, 1);
    }

    public void theGameMethod(View view) {
        boolean conditionForDraw = false;
        theImageWeAreLookingFor = (ImageView) view;
        int id = view.getId();

        if (gameWon) {
            return;
        }

        if (id == R.id.i0) {
            handleImageViewClick(0, 0);
        } else if (id == R.id.i1) {
            handleImageViewClick(0, 1);
        } else if (id == R.id.i2) {
            handleImageViewClick(0, 2);
        } else if (id == R.id.i3) {
            handleImageViewClick(1, 0);
        } else if (id == R.id.i4) {
            handleImageViewClick(1, 1);
        } else if (id == R.id.i5) {
            handleImageViewClick(1, 2);
        } else if (id == R.id.i6) {
            handleImageViewClick(2, 0);
        } else if (id == R.id.i7) {
            handleImageViewClick(2, 1);
        } else if (id == R.id.i8) {
            handleImageViewClick(2, 2);
        }

        if (counter == 9 && !gameWon) {
            conditionForDraw = true;
        }

        if (conditionForDraw) {
            displayGameResult("Draw");
        }
    }

    private void handleImageViewClick(int row, int col) {
        if (!userState[row][col] && !specialCondition[row][col]) {
            counter++;
            if (currentUser) {
                userState[row][col] = true;
                theImageWeAreLookingFor.setImageResource(R.drawable.circle);
                theImageWeAreLookingFor.animate().setDuration(2000);
                currentUser = false;
            } else {
                specialCondition[row][col] = true;
                theImageWeAreLookingFor.setImageResource(R.drawable.star);
                theImageWeAreLookingFor.animate().rotation(360f).setDuration(2000);
                currentUser = true;
            }
        } else {
            Toast.makeText(MainActivity.this, "Already clicked", Toast.LENGTH_SHORT).show();
        }

        // Check for winner
        int winnerResult = winner();
        if (winnerResult == 1 && counter >= 3) {
            if (checkWinner(userState)) {
                displayGameResult("Circle Won");
            } else if (checkWinner(specialCondition)) {
                displayGameResult("Star won");
            }
        }
    }

    // Method to check if there's a winner
    private boolean checkWinner(boolean[][] condition) {
        // Check rows, columns, and diagonals for winning condition
        for (int i = 0; i < 3; i++) {
            if (condition[i][0] && condition[i][1] && condition[i][2]) {
                return true; // Row-wise
            }
            if (condition[0][i] && condition[1][i] && condition[2][i]) {
                return true; // Column-wise
            }
        }
        if (condition[0][0] && condition[1][1] && condition[2][2]) {
            return true; // Diagonal from top-left to bottom-right
        }
        if (condition[0][2] && condition[1][1] && condition[2][0]) {
            return true; // Diagonal from top-right to bottom-left
        }
        return false;
    }

    private int winner() {
        if (checkWinner(userState)) {
            return 1;
        } else if (checkWinner(specialCondition)) {
            return 1;
        } else {
            return 0;
        }
    }

    private void displayGameResult(String result) {
        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setVisibility(View.VISIBLE);
        Button updateText = findViewById(R.id.whoWon);
        updateText.setVisibility(View.VISIBLE);
        updateText.setText(result);
        gameWon = true;

        // Stop the background music
        mediaPlayer.stop();

        if (result.equals("Circle Won") || result.equals("Star won")) {
            playVictorySound();
        } else if (result.equals("Draw")) {
            playDrawSound();
        }
    }

    public void restartActivity(View view) {
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
        gameWon = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer and SoundPool resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (soundPool != null) {
            soundPool.release();
        }
    }
}
