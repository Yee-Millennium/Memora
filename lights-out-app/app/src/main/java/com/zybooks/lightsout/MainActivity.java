package com.zybooks.lightsout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final String GAME_STATE = "gameState";
    private final String LIGHT_ON_COLOR = "lightOnColor";
    private final String LIGHT_ON_COLOR_ID = "lightOnColorId";
    private LightsOutGame mGame;
    private GridLayout mLightGrid;
    private int mLightOnColor;
    private int mLightOffColor;
    private int mLightOnColorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLightGrid = findViewById(R.id.light_grid);
        mLightOnColorId = R.color.yellow;

        // Add the same click handler to all grid buttons
        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);
            gridButton.setOnClickListener(this::onLightButtonClick);
        }

        // Add long click handler to first button (row 0, column 0) to toggle cheat mode
        Button cheatButton = (Button) mLightGrid.getChildAt(0);
        cheatButton.setLongClickable(true);
        cheatButton.setOnLongClickListener(v -> {
            // long click
            cheatMode();
            return true;
        });

        mLightOnColor = ContextCompat.getColor(this, R.color.yellow);
        mLightOffColor = ContextCompat.getColor(this, R.color.black);

        mGame = new LightsOutGame();

        if (savedInstanceState == null) {
            startGame();
        }
        else {
            String gameState = savedInstanceState.getString(GAME_STATE);
            mGame.setState(gameState);
            mLightOnColor = savedInstanceState.getInt(LIGHT_ON_COLOR);
            mLightOnColorId = savedInstanceState.getInt(LIGHT_ON_COLOR_ID);
            setButtonColors();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(GAME_STATE, mGame.getState());
        outState.putInt(LIGHT_ON_COLOR, mLightOnColor);
        outState.putInt(LIGHT_ON_COLOR_ID, mLightOnColorId);
    }

    private void startGame() {
        mGame.newGame();
        setButtonColors();
    }

    public void onNewGameClick(View view) {
        startGame();
    }

    private void setButtonColors() {
        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);

            // Find the button's row and col
            int row = buttonIndex / LightsOutGame.GRID_SIZE;
            int col = buttonIndex % LightsOutGame.GRID_SIZE;

            if (mGame.isLightOn(row, col)) {
                gridButton.setBackgroundColor(mLightOnColor);
                gridButton.setContentDescription(getString(R.string.light_on));
            } else {
                gridButton.setBackgroundColor(mLightOffColor);
                gridButton.setContentDescription(getString(R.string.light_off));
            }
        }
    }

    private void onLightButtonClick(View view) {

        // Find the button's row and col
        int buttonIndex = mLightGrid.indexOfChild(view);
        int row = buttonIndex / LightsOutGame.GRID_SIZE;
        int col = buttonIndex % LightsOutGame.GRID_SIZE;

        mGame.selectLight(row, col);
        setButtonColors();

        // Congratulate the user if the game is over
        if (mGame.isGameOver()) {
            Toast.makeText(this, R.string.congrats, Toast.LENGTH_SHORT).show();
        }
    }

    public void onHelpClick(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void onChangeColorClick(View view) {
        // Send the current color ID to ColorActivity
        Intent intent = new Intent(this, ColorActivity.class);
        intent.putExtra(ColorActivity.EXTRA_COLOR, mLightOnColorId);
        mColorResultLauncher.launch(intent);
    }

    private void cheatMode() {
        for (int buttonIndex = 0; buttonIndex < mLightGrid.getChildCount(); buttonIndex++) {
            Button gridButton = (Button) mLightGrid.getChildAt(buttonIndex);
            gridButton.setBackgroundColor(mLightOffColor);
            gridButton.setContentDescription(getString(R.string.light_off));
        }
        mGame.cheatMode(this);
    }

    private final ActivityResultLauncher<Intent> mColorResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        // Create the "on" button color from the chosen color ID from ColorActivity
                        mLightOnColorId = data.getIntExtra(ColorActivity.EXTRA_COLOR, R.color.yellow);
                        mLightOnColor = ContextCompat.getColor(MainActivity.this, mLightOnColorId);
                        setButtonColors();
                    }
                }
            }
        });
}
