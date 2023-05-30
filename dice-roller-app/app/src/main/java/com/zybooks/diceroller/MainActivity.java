package com.zybooks.diceroller;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements RollLengthDialogFragment.OnRollLengthSelectedListener {

    public static final int MAX_DICE = 3;
    private final String TIMER_LENGTH_STATE = "timerLengthState";
    private final String DICE_VISIBLE_STATE = "diceVisibleState";
    private final String DICE_ONE_STATE = "diceOneState";
    private final String DICE_TWO_STATE = "diceTwoState";
    private final String DICE_THREE_STATE = "diceThreeState";
    private int mVisibleDice;
    private Dice[] mDice;
    private ImageView[] mDiceImageViews;
    private Menu mMenu;
    private CountDownTimer mTimer;
    private long mTimerLength = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an array of Dice
        mDice = new Dice[MAX_DICE];
        for (int i = 0; i < MAX_DICE; i++) {
            mDice[i] = new Dice(i + 1);
        }

        // Create an array of ImageViews
        mDiceImageViews = new ImageView[MAX_DICE];
        mDiceImageViews[0] = findViewById(R.id.dice1);
        mDiceImageViews[1] = findViewById(R.id.dice2);
        mDiceImageViews[2] = findViewById(R.id.dice3);

        // All dice are initially visible
        mVisibleDice = MAX_DICE;

        if (savedInstanceState == null) {
            showDice();
        }
        else {
            mTimerLength = savedInstanceState.getLong(TIMER_LENGTH_STATE);

            mVisibleDice = savedInstanceState.getInt(DICE_VISIBLE_STATE);
            changeDiceVisibility(mVisibleDice);

            int diceOneState = savedInstanceState.getInt(DICE_ONE_STATE);
            int diceTwoState = savedInstanceState.getInt(DICE_TWO_STATE);
            int diceThreeState = savedInstanceState.getInt(DICE_THREE_STATE);
            if ( mVisibleDice == 1) {
                mDice[0].setNumber(diceOneState);
            } else if (mVisibleDice == 2) {
                mDice[0].setNumber(diceOneState);
                mDice[1].setNumber(diceTwoState);
            } else if (mVisibleDice == 3) {
                mDice[0].setNumber(diceOneState);
                mDice[1].setNumber(diceTwoState);
                mDice[2].setNumber(diceThreeState);
            }
            showDice();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TIMER_LENGTH_STATE, mTimerLength);
        outState.putInt(DICE_VISIBLE_STATE, mVisibleDice);
        outState.putInt(DICE_ONE_STATE, mDice[0].getNumber());
        outState.putInt(DICE_TWO_STATE, mDice[1].getNumber());
        outState.putInt(DICE_THREE_STATE, mDice[2].getNumber());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        mMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRollLengthClick(int which) {
        // Convert to milliseconds
        mTimerLength = 1000L * (which + 1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Determine which menu option was chosen
        if (item.getItemId() == R.id.action_one) {
            changeDiceVisibility(1);
            showDice();

            return true;
        }
        else if (item.getItemId() == R.id.action_two) {
            changeDiceVisibility(2);
            showDice();

            return true;
        }
        else if (item.getItemId() == R.id.action_three) {
            changeDiceVisibility(3);
            showDice();

            return true;
        }
        else if (item.getItemId() == R.id.action_stop) {
            mTimer.cancel();
            item.setVisible(false);
            mMenu.findItem(R.id.action_roll).setVisible(true);

            return true;
        }
        else if (item.getItemId() == R.id.action_roll) {
            rollDice();

            return true;
        }
        else if (item.getItemId() == R.id.action_roll_length) {
            RollLengthDialogFragment dialog = new RollLengthDialogFragment();
            dialog.show(getSupportFragmentManager(), "rollLengthDialog");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDice() {
        // Display only the number of dice visible
        for (int i = 0; i < mVisibleDice; i++) {
            Drawable diceDrawable = ContextCompat.getDrawable(this, mDice[i].getImageId());
            mDiceImageViews[i].setImageDrawable(diceDrawable);
            mDiceImageViews[i].setContentDescription(Integer.toString(mDice[i].getNumber()));
        }
    }

    private void changeDiceVisibility(int numVisible) {
        mVisibleDice = numVisible;

        // Make dice visible
        for (int i = 0; i < numVisible; i++) {
            mDiceImageViews[i].setVisibility(View.VISIBLE);
        }

        // Hide remaining dice
        for (int i = numVisible; i < MAX_DICE; i++) {
            mDiceImageViews[i].setVisibility(View.GONE);
        }
    }

    private void rollDice() {
        mMenu.findItem(R.id.action_stop).setVisible(true);
        mMenu.findItem(R.id.action_roll).setVisible(false);

        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new CountDownTimer(mTimerLength, 100) {
            public void onTick(long millisUntilFinished) {
                for (int i = 0; i < mVisibleDice; i++) {
                    mDice[i].roll();
                }
                showDice();
        }

            public void onFinish() {
                mMenu.findItem(R.id.action_stop).setVisible(false);
                mMenu.findItem(R.id.action_roll).setVisible(true);
            }
        }.start();
    }
}
