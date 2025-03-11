package com.example.sosgame;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView playerOneScore, playerTwoScore, playerStatus;
    private Button[] buttons = new Button[25];
    private Button buttonS,buttonO ,buttonRestart;
    private  int playerOneScoreCount, playerTwoScoreCount, roundCount;
    boolean isPlayerOneActive,isPlayerTwoActive;
    private ArrayList<String> gameState = new ArrayList<>(25);
    private String symbol = "";

    //identify all winning positions
    private int[][] winningPositions = {
            // rows
            {0, 1, 2}, {1, 2, 3}, {2, 3, 4},
            {5, 6, 7}, {6, 7, 8}, {7, 8, 9},
            {10, 11, 12}, {11, 12, 13}, {12, 13, 14},
            {15, 16, 17}, {16, 17, 18}, {17, 18, 19},
            {20, 21, 22}, {21, 22, 23}, {22, 23, 24},
            // columns
            {0, 5, 10}, {5, 10, 15}, {10, 15, 20},
            {1, 6, 11}, {6, 11, 16}, {11, 16, 21},
            {2, 7, 12}, {7, 12, 17}, {12, 17, 22},
            {3, 8, 13}, {8, 13, 18}, {13, 18, 23},
            {4, 9, 14}, {9, 14, 19}, {14, 19, 24},
            // diagonals
            // left to right diagonals
            {0, 6, 12}, {1, 7, 13}, {2, 8, 14}, {5, 11, 17}, {6, 12, 18}, {7, 13, 19}, {10, 16, 22}, {11, 17, 23}, {12, 18, 24},
            // right to left diagonals
            {2, 6, 10}, {3, 7, 11}, {4, 8, 12}, {7, 11, 15}, {8, 12, 16}, {9, 13, 17}, {12, 16, 20}, {13, 17, 21}, {14, 18, 22}
    };

    private ArrayList<int[]> winningPositionsList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerOneScore =(TextView) findViewById(R.id.playerOneScore);
        playerTwoScore = (TextView) findViewById(R.id.playerTwoScore);
        playerStatus = (TextView) findViewById(R.id.playerStatus);
        buttonS = (Button) findViewById(R.id.buttonS);
        buttonO = (Button) findViewById(R.id.buttonO);
        buttonRestart = findViewById(R.id.buttonRestart);
        buttonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        Collections.addAll(winningPositionsList, winningPositions);

        for(int i=0;i<25;i++){
            gameState.add("");
        }
        playerStatus.setText("Player One's turn");
        playerStatus.setTextColor(Color.GREEN);
        // Create a zoom animation
        Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out);
        // Set animation listener to repeat the animation
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                playerStatus.startAnimation(zoomAnimation); // Restart the animation
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        playerStatus.startAnimation(zoomAnimation);

        for(int i = 0; i < buttons.length; i++){
            String buttonID = "btn_" + i;
            int resourceID = getResources().getIdentifier(buttonID,"id",getPackageName());
            buttons[i] = findViewById(resourceID);
            buttons[i].setOnClickListener(this);
            buttons[i].setTag(i);
        }
        roundCount = 0;
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        isPlayerOneActive = true;
    }
    @Override
    public void onClick(View view) {
        // Ensure that a player chooses a symbol before playing
        if (symbol.equals("")) {
            playerStatus.setTextColor(Color.RED);
            playerStatus.setText("Choose a symbol (S or O) before playing");

            // Create a zoom animation
            Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out);

            // Set animation listener to repeat the animation
            zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    playerStatus.startAnimation(zoomAnimation); // Restart the animation
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            playerStatus.startAnimation(zoomAnimation);
            return;
        }
        int gameStatePointer = (int) view.getTag();
        if (gameState.get(gameStatePointer).equals("")) {
            if (isPlayerOneActive) {
                playerStatus.setText("Player One's turn");
                ((Button) view).setText(symbol);
                ((Button) view).setTextColor(Color.GREEN);
                buttonS.setTextColor(Color.WHITE);
                buttonS.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                buttonO.setTextColor(Color.WHITE);
                buttonO.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                gameState.set(gameStatePointer,symbol);
                // Check for SOS sequence and update scores
                if (checkSOS()) {
                    if (gameState.contains(""))
                        Toast.makeText(this, "Player One: +Extra Turn!", Toast.LENGTH_SHORT).show();
                    playerOneScoreCount++;
                    isPlayerOneActive = true;
                    updatePlayerScore();
                    if (checkSOS()) {
                        playerOneScoreCount++;
                        isPlayerOneActive = true;
                        updatePlayerScore();
                    }
                }else{
                    isPlayerOneActive = false;
                    isPlayerTwoActive = true;
                }
            } else {
                playerStatus.setText("Player Two's turn");
                ((Button) view).setText(symbol);
                ((Button) view).setTextColor(Color.RED);
                buttonS.setTextColor(Color.WHITE);
                buttonS.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                buttonO.setTextColor(Color.WHITE);
                buttonO.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                gameState.set(gameStatePointer,symbol);

                // Check for SOS sequence and update scores
                if (checkSOS()) {
                    if (gameState.contains(""))
                        Toast.makeText(this, "Player Two: +Extra Turn!", Toast.LENGTH_SHORT).show();
                    isPlayerTwoActive = true;
                    playerTwoScoreCount++;
                    updatePlayerScore();
                    if (checkSOS()) {
                        isPlayerTwoActive = true;
                        playerTwoScoreCount++;
                        updatePlayerScore();
                    }
                }else {
                    isPlayerOneActive = true;
                    isPlayerTwoActive = false;
                }
            }
            if (gameState.contains("")) {
                if (isPlayerOneActive) {
                    playerStatus.setTextColor(Color.GREEN);
                    playerStatus.setText("Player One's turn");
                } else {
                    playerStatus.setTextColor(Color.RED);
                    playerStatus.setText("Player Two's turn");
                }
            }
            if (!gameState.contains("")) {
                String winner;
                if (playerOneScoreCount > playerTwoScoreCount) {
                    winner = "Player One Won!";
                } else if (playerTwoScoreCount > playerOneScoreCount) {
                    winner = "Player Two Won!";
                } else {
                    winner = "It's a Draw!";
                }
                playerStatus.setTextColor(ContextCompat.getColor(this, R.color.purple_500));
                playerStatus.setText(winner);
                Animation fastZoomAnimation = AnimationUtils.loadAnimation(this,R.anim.fast_zoom_in_out);
                // Set animation listener to repeat the animation
                fastZoomAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playerStatus.startAnimation(fastZoomAnimation); // Restart the animation
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                playerStatus.startAnimation(fastZoomAnimation);
                return; // End the game
            }
            symbol = "";
        }
    }

    public boolean checkSOS(){
        for (int[] winningPos : winningPositionsList) {
            if (gameState.get(winningPos[0]).equalsIgnoreCase("S") &&
                    gameState.get(winningPos[1]).equalsIgnoreCase("O") &&
                    gameState.get(winningPos[2]).equalsIgnoreCase("S") &&
                    !gameState.get(winningPos[0]).equalsIgnoreCase(""))
            {
                winningPositionsList.remove(winningPos);
                (buttons[winningPos[0]]).setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200));
                (buttons[winningPos[1]]).setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200));
                (buttons[winningPos[2]]).setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200));
                (buttons[winningPos[0]]).setTextColor(Color.BLACK);
                (buttons[winningPos[1]]).setTextColor(Color.BLACK);
                (buttons[winningPos[2]]).setTextColor(Color.BLACK);
                return true;
            }
        }
        return false;
    }

    public  void updatePlayerScore(){
        playerOneScore.setText(Integer.toString(playerOneScoreCount));
        playerTwoScore.setText(Integer.toString(playerTwoScoreCount));
    }

    public void onChooseSymbolClicked(View view) {
        Button buttonS = findViewById(R.id.buttonS);
        Button buttonO = findViewById(R.id.buttonO);
        if(isPlayerOneActive){
            if (view.getId() == R.id.buttonS) {
                view.setBackgroundColor(Color.GREEN);
                buttonS.setTextColor(Color.BLACK);
                buttonO.setTextColor(Color.WHITE);
                buttonO.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                symbol =  "S";
            }
            else {
                view.setBackgroundColor(Color.GREEN);
                buttonO.setTextColor(Color.BLACK);
                buttonS.setTextColor(Color.WHITE);
                buttonS.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                symbol = "O";
            }
        }else {
            if (view.getId() == R.id.buttonS) {
                view.setBackgroundColor(Color.RED);
                buttonS.setTextColor(Color.BLACK);
                buttonO.setTextColor(Color.WHITE);
                buttonO.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                symbol =  "S";
            }
            else {
                view.setBackgroundColor(Color.RED);
                buttonO.setTextColor(Color.BLACK);
                buttonS.setTextColor(Color.WHITE);
                buttonS.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
                symbol = "O";
            }
        }
    }

    private void restartGame() {
        // Reset game-related variables
        roundCount = 0;
        playerOneScoreCount = 0;
        playerTwoScoreCount = 0;
        isPlayerOneActive = true;
        symbol = "";

        Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out);
        Animation fastZoomAnimation = AnimationUtils.loadAnimation(this,R.anim.fast_zoom_in_out);
        // Set animation listener to repeat the animation
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                playerStatus.startAnimation(zoomAnimation); // Restart the animation
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        playerStatus.startAnimation(zoomAnimation);

        // Clear the game state
        for (int i = 0; i < gameState.size(); i++) {
            gameState.set(i, "");
            buttons[i].setText("");
            buttons[i].setBackgroundColor(Color.parseColor("#413F43")); // Reset button color
            buttons[i].setTextColor(Color.BLACK); // Reset text color
        }

        // Update player scores
        updatePlayerScore();

        // Reset player status text and color
        playerStatus.setText("Player One's turn");
        playerStatus.setTextColor(Color.GREEN);

        // Clear symbol selection
        buttonS.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        buttonS.setTextColor(Color.WHITE);
        buttonO.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500));
        buttonO.setTextColor(Color.WHITE);

        // Reset winning positions list
        winningPositionsList.clear();
        Collections.addAll(winningPositionsList, winningPositions);
    }

}