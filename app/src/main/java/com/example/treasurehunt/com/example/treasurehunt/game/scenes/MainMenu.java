package com.example.treasurehunt.com.example.treasurehunt.game.scenes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.treasurehunt.R;

import java.io.IOException;

/*
 * This is a main menu of this game
 * @author group 8
 */
public class MainMenu extends Activity implements OnClickListener {

    public final String GAME_PREFS = "ArithmeticFile";
    /*
     * Properties
     */
    private MediaPlayer mp;
    private Button btn;
    private int level = 1, score = 0, lives = 3;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btn = (Button) findViewById(R.id.settingBtn);

        mp = MediaPlayer.create(MainMenu.this, R.raw.sound1);
        mp.setLooping(true);
        mp.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
     * This code handle the activity
     *
     * @author 8C Pham Duy Hung
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.newGameBtn:
                if (mp.isPlaying() && mp.isLooping()) {
                    btn.setBackgroundResource(R.drawable.mute);
                    mp.pause();
                }

                Intent openNewGame = new Intent(MainMenu.this, Game.class);
                openNewGame.putExtra("Level", "1");
                openNewGame.putExtra("Total Score", "0");
                openNewGame.putExtra("Lives", "3");
                startActivity(openNewGame);
                break;

            case R.id.continueBtn:
                if (mp.isPlaying() && mp.isLooping()) {
                    btn.setBackgroundResource(R.drawable.mute);
                    mp.pause();
                }

                SharedPreferences gameSavePrefs = getSharedPreferences(
                        Game.GAME_PREFS, 0);

                String savedGame1 = gameSavePrefs.getString("saveGame", "");

                if (savedGame1.length() > 0) {
                    String[] parts = savedGame1.split(" - ");

                    level = Integer.parseInt(parts[0]);
                    score = Integer.parseInt(parts[1]);
                    lives = Integer.parseInt(parts[2]);

                    // clear the saved game state
                    gameSavePrefs.edit().putString("saveGame", "").commit();

                    // load saved state to game play
                    Intent openContinueGame = new Intent(MainMenu.this, Game.class);
                    openContinueGame.putExtra("Level", "" + level);
                    openContinueGame.putExtra("Total Score", "" + score);
                    openContinueGame.putExtra("Lives", "" + lives);
                    startActivity(openContinueGame);
                } else {
                    Toast dialog = Toast.makeText(MainMenu.this,
                            "There 's no game to continue !!!", Toast.LENGTH_SHORT);
                    dialog.setGravity(Gravity.CENTER, 0, 0);
                    dialog.setDuration(2000);
                    dialog.show();
                }
                break;

            case R.id.settingBtn:
                if (mp.isPlaying() && mp.isLooping()) {
                    btn.setBackgroundResource(R.drawable.mute);
                    mp.pause();
                    try {
                        mp.prepare();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mp.seekTo(0);
                } else {
                    btn.setBackgroundResource(R.drawable.sound);
                    mp.start();
                }
                break;

            case R.id.recordBtn:
                Intent openRecord = new Intent(MainMenu.this, Record.class);
                startActivity(openRecord);
                break;
            case R.id.instructionBtn:
                Intent instruction = new Intent(MainMenu.this, Instruction.class);
                startActivity(instruction);
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        System.exit(0);
    }

}
