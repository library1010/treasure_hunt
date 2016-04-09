package com.example.treasurehunt.com.example.treasurehunt.game.scenes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treasurehunt.R;
import com.example.treasurehunt.Score;
import com.example.treasurehunt.com.example.treasurehunt.cell.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 * The main game
 * @author group 8
 */

public class Game extends Activity {

    public static final String GAME_PREFS = "ArithmeticFile";
    private final int minTraps = 84;
    private final int maxTime = 480;
    // Popup
    Handler handler;
    // Texts
    Typeface font, font2;
    TextView levelText, scoreText, timeText, livesText, finalScoreText,
            finalTimeText, trapText;
    // UI
    ImageView mImgViewResult;
    /*
     * Properties
     */
    private TableLayout map;
    private Cell cells[][];
    private int numberOfRows = 0;
    private int numberOfColumns = 0;
    private int totalTraps = 0;
    private int trapsRemain = 0;
    private int level = 1;
    private int lives = 0;
    private int totalScore = 0;
    private int cellWidth = 34;
    private int cellPadding = 2;
    // Tracking time
    private Handler clock;
    private int timer;
    private boolean isGameOver;
    private boolean isGameFinish = false;
    private boolean isGameStart;
    private boolean isMapGen;
    // Sound
    private MediaPlayer mp;
    // Save Score
    private SharedPreferences gamePrefs;
    /*
     * This properties must be set up for handling the clock
     *
     * @author 8B Pham Hung Cuong
     */
    private Runnable updateTimeElapsed = new Runnable() {

        @Override
        public void run() {
            long currentMilliseconds = System.currentTimeMillis();
            --timer;

            if (!isGameOver) {
                timeText.setText(timer);
            }

            // add notification
            clock.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            clock.postDelayed(updateTimeElapsed, 1000);

            if (timer == 0) {
                finishGame(0, 0);
            }

        }
    };

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // @author 8C Pham Duy Hung
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String val1 = extras.getString("Level");
                level = Integer.parseInt(val1);
                val1 = extras.getString("Total Score");
                totalScore = Integer.parseInt(val1);
                val1 = extras.getString("Lives");
                lives = Integer.parseInt(val1);

            } else {
                Toast.makeText(this, "Cannot load the game", Toast.LENGTH_SHORT)
                        .show();
                Intent backToMainMenu = new Intent(Game.this, MainMenu.class);
                startActivity(backToMainMenu);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Cannot load the game", Toast.LENGTH_SHORT)
                    .show();
            Intent backToMainMenu = new Intent(Game.this, MainMenu.class);
            startActivity(backToMainMenu);
        }

        // Share preference of Game class
        gamePrefs = getSharedPreferences(GAME_PREFS, 0);

        gameController(level, totalScore, lives);
        initView();
        startNewGame();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        if (lives > 0 && !isGameFinish) {
            saveGameState(level, totalScore, lives);
        }
        finish();
        super.onBackPressed();
    }

    /*
     * Initial view
     *
     * @author 8B Pham Hung Cuong
     */
    @SuppressWarnings("deprecation")
    private void initView() {

        // @author 8A Tran Trong Viet

        mp = MediaPlayer.create(Game.this, R.raw.flag);

        map = (TableLayout) findViewById(R.id.Map);
        // recordSaver = new Record();

        font = Typeface.createFromAsset(getBaseContext().getAssets(),
                "fonts/FRANCHISE-BOLD.TTF");
        font2 = Typeface.createFromAsset(getBaseContext().getAssets(),
                "fonts/Sketch_Block.ttf");
        levelText = (TextView) findViewById(R.id.levelText);
        levelText.setTypeface(font2);
        scoreText = (TextView) findViewById(R.id.scoreText);
        scoreText.setTypeface(font2);
        timeText = (TextView) findViewById(R.id.timeText);
        timeText.setTypeface(font2);
        livesText = (TextView) findViewById(R.id.livesText);
        livesText.setTypeface(font2);
        trapText = (TextView) findViewById(R.id.trapText);
        trapText.setTypeface(font2);

        levelText.setText("LEVEL " + level);
        scoreText.setText("" + totalScore);
        livesText.setText("" + lives);
        trapText.setText("" + trapsRemain);

        mImgViewResult = (ImageView) findViewById(R.id.img_result);

    }

    /*
     * This method control the level ingame
     *
     * @author 8C Pham Duy Hung
     *
     * @param _level the current level
     *
     * @param _score the current score
     *
     * @param _lives the current lives
     */
    private void gameController(int _level, int _score, int _lives) {

        // Default setting
        isGameOver = false;
        isGameStart = false;
        isMapGen = false;

        numberOfRows = 16;
        numberOfColumns = 30;

        // Tracking time
        clock = new Handler();

        switch (_level) {
            case 1:
                setUpGame(maxTime, minTraps - 50, _score, _lives);
                break;
            case 2:
                setUpGame(maxTime, minTraps + _level, _score, _lives);
                break;
            case 3:
                setUpGame(maxTime, minTraps + _level, _score, _lives);
                break;
            case 4:
                setUpGame(maxTime - 60, minTraps + _level, _score, _lives);
                break;
            case 5:
                setUpGame(maxTime - 60, minTraps + _level, _score, _lives);
                break;
            case 6:
                setUpGame(maxTime - 60, minTraps + _level, _score, _lives);
                break;
            case 7:
                setUpGame(maxTime - 60 * 2, minTraps + _level, _score, _lives);
                break;
            case 8:
                setUpGame(maxTime - 60 * 2, minTraps + _level, _score, _lives);
                break;
            case 9:
                setUpGame(maxTime - 60 * 2, minTraps + _level, _score, _lives);
                break;
            case 10:
                setUpGame(maxTime - 60 * 3, minTraps + _level, _score, _lives);
                break;
            case 11:
                setUpGame(maxTime - 60 * 3, minTraps + _level, _score, _lives);
                break;
            case 12:
                setUpGame(maxTime - 60 * 3, minTraps + _level, _score, _lives);
                break;
            case 13:
                setUpGame(maxTime - 60 * 4, minTraps + _level, _score, _lives);
                break;
            case 14:
                setUpGame(maxTime - 60 * 4, minTraps + _level, _score, _lives);
                break;
            case 15:
                setUpGame(maxTime - 60 * 4, minTraps + _level, _score, _lives);
                break;
            default:
                break;
        }
    }

    /*
     * Set up the game properties
     *
     * @author 8A Tran Trong Viet
     *
     * @param playTime the time of current level
     *
     * @numberOfTraps the number of traps in current level
     *
     * @score the current score
     *
     * @_lives the current lives
     */
    private void setUpGame(int playTime, int numberOfTraps, int score,
                           int _lives) {
        totalTraps = numberOfTraps;
        timer = playTime;
        trapsRemain = numberOfTraps;
        totalScore = score;
        lives = _lives;
    }

    /*
     * Start the game
     *
     * @author 8C Pham Duy Hung
     */
    private void startNewGame() {
        // TODO: generate the initial information

        createMap();
        showMap();

        isGameOver = false;
        isGameStart = false;
        timeText.setText("" + timer);
    }

    /*
     * Create new map
     *
     * @author 8C Pham Duy Hung
     */
    private void createMap() {

        // We make more 2 row and column, the 0 row/column and the last one are
        // not showed
        cells = new Cell[numberOfRows + 2][numberOfColumns + 2];

        for (int row = 0; row < numberOfRows + 2; row++) {
            for (int column = 0; column < numberOfColumns + 2; column++) {
                cells[row][column] = new Cell(this);

                final int currentRow = row;
                final int currentColumn = column;

                cells[row][column].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        onClickOnCellHandle(currentRow, currentColumn);
                    }
                });

                // add Long Click listener
                // this is treated as right mouse click listener
                cells[row][column]
                        .setOnLongClickListener(new OnLongClickListener() {
                            public boolean onLongClick(View view) {
                                // simulate a left-right (middle) click
                                // if it is a long click on an opened trap then
                                // open all surrounding blocks
                                return onLongClickOnCellHandle(currentRow,
                                        currentColumn);
                            }
                        });
            }
        }
    }

    /*
     * This method handles the on click event
     *
     * @author 8C Pham Duy Hung
     *
     * @param currentRow the position of the clicked cell
     *
     * @param currentCol the position of the clicked cell
     */
    private void onClickOnCellHandle(int currentRow, int currentColumn) {

        if (!isGameStart) {
            startTimer();
            isGameStart = true;
        }

        if (!isMapGen) {
            genMap(currentRow, currentColumn);
            isMapGen = true;
        }

        if (!cells[currentRow][currentColumn].isFlagged()) {
            rippleUncover(currentRow, currentColumn);

            if (cells[currentRow][currentColumn].hasTrap()) {
                lives--;
                trapsRemain--;
                livesText.setText("" + lives);
                trapText.setText("" + trapsRemain);
                cells[currentRow][currentColumn].OpenCell();
                cells[currentRow][currentColumn].setFlag();
                if (lives <= 0) {
                    finishGame(currentRow, currentColumn);
                    livesText.setText("0");
                }
            }

            if (checkGameWin(cells[currentRow][currentColumn])) {
                winGame();
            }
        }
    }

    /*
     * This method handles the on long click event
     *
     * @author 8C Pham Duy Hung
     *
     * @param currentRow the position of the clicked cell
     *
     * @param currentCol the position of the clicked cell
     */
    private boolean onLongClickOnCellHandle(int currentRow, int currentColumn) {

        if (!cells[currentRow][currentColumn].isCovered()
                && (cells[currentRow][currentColumn]
                .getSurroundTraps() > 0) && !isGameOver) {
            int nearbyFlaggedBlocks = 0;
            for (int previousRow = -1; previousRow < 2; previousRow++) {
                for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                    if (cells[currentRow + previousRow][currentColumn
                            + previousColumn].isFlagged()) {
                        nearbyFlaggedBlocks++;
                    }
                }
            }

            // if flagged block count is equal to nearby trap count then open
            // nearby blocks
            if (nearbyFlaggedBlocks == cells[currentRow][currentColumn]
                    .getSurroundTraps()) {
                for (int previousRow = -1; previousRow < 2; previousRow++) {
                    for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                        // don't open flagged blocks
                        if (!cells[currentRow + previousRow][currentColumn
                                + previousColumn].isFlagged()) {
                            // open blocks till we get
                            // numbered block
                            rippleUncover(currentRow + previousRow,
                                    currentColumn + previousColumn);

                            // did we clicked a trap
                            if (cells[currentRow + previousRow][currentColumn
                                    + previousColumn].hasTrap()) {

                                cells[currentRow + previousRow][currentColumn
                                        + previousColumn].OpenCell();
                                lives--;
                                livesText.setText("" + lives);
                                trapsRemain--;
                                trapText.setText("" + trapsRemain);
                                if (lives <= 0) {
                                    livesText.setText("0");
                                    finishGame(0, 0);
                                }

                            }
                        }
                    }
                }
            }
            return true;
        }

        // if clicked cells is enabled, clickable or flagged

        flagAndDoubtHandle(currentRow, currentColumn);
        return true;
    }

    /*
     * This method handles the flag and doubt situations
     *
     * @author 8C Pham Duy Hung
     *
     * @param currentRow the position of the clicked cell
     *
     * @param currentCol the position of the clicked cell
     */
    private void flagAndDoubtHandle(int currentRow, int currentColumn) {
        // we got 3 situations
        // 1. empty blocks to flagged
        // 2. flagged to question mark
        // 3. question mark to blank

        if (cells[currentRow][currentColumn].isClickable()
                && (cells[currentRow][currentColumn].isEnabled() || cells[currentRow][currentColumn]
                .isFlagged())) {
            mp.start();
            // case 1. set blank block to flagged
            if (!cells[currentRow][currentColumn].isFlagged()
                    && !cells[currentRow][currentColumn].isDoubted()) {
                cells[currentRow][currentColumn].setFlagIcon();
                cells[currentRow][currentColumn].setFlag();
                trapsRemain--; // reduce trap count
                trapText.setText("" + trapsRemain);
            }
            // case 2. set flagged to question mark
            else if (!cells[currentRow][currentColumn].isDoubted()) {
                cells[currentRow][currentColumn].setDoubt();
                cells[currentRow][currentColumn].setFlagIcon();
                cells[currentRow][currentColumn].setDoubtIcon(true);
                cells[currentRow][currentColumn].setFlag();
                trapsRemain++; // increase trap count
                trapText.setText("" + trapsRemain);
            }
            // case 3. change to blank square
            else {
                cells[currentRow][currentColumn].clearAllIcons();
                cells[currentRow][currentColumn].setDoubt();
                // if it is flagged then increment trap count
                if (cells[currentRow][currentColumn].isFlagged()) {
                    trapsRemain++; // increase trap count
                    trapText.setText("" + trapsRemain);
                }
                // remove flagged status
                cells[currentRow][currentColumn].setFlag();
            }

        }
    }

    /*
     * Show map procedure
     *
     * @author 8C Pham Duy Hung
     */
    private void showMap() {
        // remember we will not show 0th and last Row and Columns
        // they are used for calculation purposes only
        for (int row = 1; row < numberOfRows + 1; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(
                    (cellWidth + 2 * cellPadding) * numberOfColumns, cellWidth
                    + 2 * cellPadding));

            for (int column = 1; column < numberOfColumns + 1; column++) {
                cells[row][column].setLayoutParams(new LayoutParams(cellWidth
                        + 2 * cellPadding, cellWidth + 2 * cellPadding));
                cells[row][column].setPadding(cellPadding, cellPadding,
                        cellPadding, cellPadding);
                tableRow.addView(cells[row][column]);
            }
            map.addView(tableRow, new TableLayout.LayoutParams(
                    (cellWidth + 2 * cellPadding) * numberOfColumns, cellWidth
                    + 2 * cellPadding));
        }
    }

    /*
     * Generate the map
     *
     * @author 8A Tran Trong Viet
     *
     * @param rowClicked the position of clicked cell
     *
     * @param columnClicked the position of clicked cell
     */
    private void genMap(int rowClicked, int columnClicked) {

        genTreasure(rowClicked, columnClicked);
        genTraps(rowClicked, columnClicked);
        setTheNumberOfSurroundingTrap();
    }

    /*
     * Generate the treasure position in map
     *
     * @author 8C Pham Duy Hung
     *
     * @param rowClicked the position of the first-clicked-cell
     *
     * @param columnClicked the position of the first-clicked-cell
     */
    private void genTreasure(int rowClicked, int columnClicked) {
        Random rand = new Random();
        int treasureRow, treasureCol;

        // Set the treasure position
        treasureRow = rand.nextInt(numberOfRows - 1) + 2;
        if (treasureRow >= numberOfRows) {
            treasureRow = numberOfRows - 2;
        }

        treasureCol = rand.nextInt(numberOfColumns - 1) + 2;
        if (treasureCol >= numberOfColumns) {
            treasureCol = numberOfColumns - 2;
        }

        // For debugging
        Log.e("8A >>>>", "Treasure: " + treasureRow + " " + treasureCol);

        // Make sure the treasure is not near the clicked cell
        if (!isTreasureNear(treasureRow, treasureCol, rowClicked, columnClicked)) {
            if (treasureRow < numberOfRows / 2) {
                treasureRow = (numberOfRows - treasureRow) / 2;
                treasureCol = (numberOfColumns - treasureCol) / 2;
            } else {
                treasureRow = (treasureRow) / 2;
                treasureCol = (treasureCol) / 2;
            }
        }

        for (int previousRow = -1; previousRow < 2; previousRow++) {
            for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                if ((previousRow != 0) || (previousColumn != 0)) {
                    cells[treasureRow + previousRow][treasureCol
                            + previousColumn].setTrap();
                } else {
                    cells[treasureRow + previousRow][treasureCol
                            + previousColumn].setTreasure();
                }
            }
        }
    }

    /*
     * Generate the traps position in map
     *
     * @author 8C Pham Duy Hung
     *
     * @param rowClicked the position of the first-clicked-cell
     *
     * @param columnClicked the position of the first-clicked-cell
     */
    private void genTraps(int rowClicked, int columnClicked) {
        Random rand = new Random();
        int trapRow, trapColumn;
        // set traps excluding the location where user clicked
        for (int row = 0; row < totalTraps - 8; row++) {
            trapRow = rand.nextInt(numberOfColumns);
            trapColumn = rand.nextInt(numberOfRows);

            // set the 8 surrounded cells of the clicked cell have no trap
            if (isNearTheClickedCell(trapRow, trapColumn, rowClicked,
                    columnClicked)) {
                if (cells[trapColumn + 1][trapRow + 1].hasTrap()
                        || cells[trapColumn + 1][trapRow + 1].hasTreasure()) {
                    row--; // trap is already there, don't repeat for same block
                }
                // plant trap at this location
                cells[trapColumn + 1][trapRow + 1].setTrap();
            }
            // exclude the user clicked location
            else {
                row--;
            }
        }
    }

    /*
     * Set the number of surrounding trap
     *
     * @author 8C Pham Duy Hung
     */
    private void setTheNumberOfSurroundingTrap() {

        int nearByTrapCount;
        // count number of traps in surrounding blocks
        for (int row = 0; row < numberOfRows + 2; row++) {
            for (int column = 0; column < numberOfColumns + 2; column++) {
                // for each block find nearby trap count
                nearByTrapCount = 0;
                if ((row != 0) && (row != (numberOfRows + 1)) && (column != 0)
                        && (column != (numberOfColumns + 1))) {
                    // check in all nearby blocks
                    for (int previousRow = -1; previousRow < 2; previousRow++) {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++) {
                            if (cells[row + previousRow][column
                                    + previousColumn].hasTrap()) {
                                // a trap was found so increment the counter
                                nearByTrapCount++;
                            }
                        }
                    }
                    cells[row][column].setSurroundTraps(nearByTrapCount);
                }
                // for side rows (0th and last row/column)
                // set count as 9 and mark it as opened
                else {
                    cells[row][column].setSurroundTraps(9);
                    cells[row][column].OpenCell();
                }

            }
        }
    }

    /*
     * Check if this cell is near the clicked cell
     *
     * @author 8C Pham Duy Hung
     *
     * @param rowCheck the row which want to check
     *
     * @param columnCheck the column which want to check
     *
     * @param rowClicked the row of the clicked cell
     *
     * @param columnClick the column of the clicked cell
     */
    private boolean isNearTheClickedCell(int rowCheck, int columnCheck,
                                         int rowClicked, int columnClicked) {
        return ((rowCheck != columnClicked) || (columnCheck != rowClicked))
                && ((rowCheck != columnClicked) || (columnCheck + 1 != rowClicked))
                && ((rowCheck != columnClicked) || (columnCheck + 2 != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck + 1 != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck + 2 != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck + 1 != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck + 2 != rowClicked));
    }

    /*
     * Check if the treasure is near the clicked cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param rowCheck the row which want to check
     *
     * @param columnCheck the column which want to check
     *
     * @param rowClicked the row of the clicked cell
     *
     * @param columnClick the column of the clicked cell
     */
    private boolean isTreasureNear(int rowCheck, int columnCheck,
                                   int rowClicked, int columnClicked) {
        return ((rowCheck != columnClicked) || (columnCheck - 1 != rowClicked))
                && ((rowCheck != columnClicked) || (columnCheck != rowClicked))
                && ((rowCheck != columnClicked) || (columnCheck + 1 != rowClicked))
                && ((rowCheck != columnClicked) || (columnCheck + 2 != rowClicked))
                && ((rowCheck != columnClicked) || (columnCheck + 3 != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck - 1 != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck + 1 != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck + 2 != rowClicked))
                && ((rowCheck + 1 != columnClicked) || (columnCheck + 3 != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck - 1 != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck + 1 != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck + 2 != rowClicked))
                && ((rowCheck + 2 != columnClicked) || (columnCheck + 3 != rowClicked));
    }

    /*
     * Open the cells which surrounded the no-trap-surrounded cell continuously
     *
     * @author 8-B Pham Hung Cuong
     *
     * @param rowClicked the row of the clicked position
     *
     * @param columnClicked the column of the clicked position
     */
    private void rippleUncover(int rowClicked, int columnClicked) {
        if (cells[rowClicked][columnClicked].hasTrap()
                || cells[rowClicked][columnClicked].isFlagged()
                || cells[rowClicked][columnClicked].isDoubted()) {
            return;
        }

        if (!cells[rowClicked][columnClicked].isClickable()) {
            return;
        }

        cells[rowClicked][columnClicked].OpenCell();
        if (cells[rowClicked][columnClicked].getSurroundTraps() != 0) {
            return;
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                // check all the above checked conditions
                // if met then open subsequent blocks
                if (cells[rowClicked + row - 1][columnClicked + column - 1]
                        .isCovered()
                        && (rowClicked + row - 1 > 0)
                        && (columnClicked + column - 1 > 0)
                        && (rowClicked + row - 1 < numberOfRows + 1)
                        && (columnClicked + column - 1 < numberOfColumns + 1)) {
                    rippleUncover(rowClicked + row - 1, columnClicked + column
                            - 1);
                }
            }
        }
    }

    /*
     * Check the game wins or not (should do this for changing rules)
     *
     * @author 8C Pham Duy Hung
     *
     * @param cell the cell which is clicked
     */
    private boolean checkGameWin(Cell cell) {
        return cell.hasTreasure();
    }

    private void winGame() {
        // reset all stuffs
        stopTimer();
        isGameFinish = true;
        isGameStart = false;
        trapsRemain = 0;
        totalScore += 1000;
        scoreText.setText(String.format("%d", totalScore));

        // disable all buttons
        // set flagged all un-flagged blocks
        for (int row = 1; row < numberOfRows + 1; row++) {
            for (int column = 1; column < numberOfColumns + 1; column++) {
                cells[row][column].setClickable(false);
                if (cells[row][column].hasTrap()) {
                    cells[row][column].setTrapIcon(true);
                }
                if (cells[row][column].hasTreasure())
                    cells[row][column].setTreasureIcon();
            }
        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mImgViewResult.setBackgroundResource(R.drawable.congrat);
                mImgViewResult.setVisibility(View.VISIBLE);
                mImgViewResult.bringToFront();
                mImgViewResult.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mImgViewResult.setVisibility(View.GONE);
                        showWinPopUp();
                        handler.removeCallbacks(this);
                    }
                }, 2000);
            }
        }, 500);

    }

    /*
     * Show Winning pop-up
     *
     * @author Pham Hung Cuong
     */
    private void showWinPopUp() {
        if (!isGameOver) {
            isGameOver = true; // mark game as over
            final Dialog popup = new Dialog(Game.this);
            popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popup.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popup.setContentView(R.layout.win_popup);
            // Set dialog title

            popup.setCancelable(false);

            finalScoreText = (TextView) popup.findViewById(R.id.finalScore);
            finalScoreText.setTypeface(font);
            finalScoreText.setText(totalScore);

            finalTimeText = (TextView) popup.findViewById(R.id.finalTime);
            finalTimeText.setTypeface(font);
            finalTimeText.setText(timer);

            popup.dismiss();
            popup.show();

            Button saveRecordBtn = (Button) popup
                    .findViewById(R.id.save_record);
            saveRecordBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            Game.this);

                    alert.setTitle("Enter your name");

                    // Set an EditText view to get user
                    final EditText input = new EditText(Game.this);
                    input.setText("Playername");
                    alert.setView(input);

                    alert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    String value = input.getText().toString();

                                    setHighScore(value, totalScore, level);

                                }
                            });

                    alert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // Canceled.
                                }
                            });
                    alert.show();
                }
            });

            Button quitToMenuBtn = (Button) popup
                    .findViewById(R.id.quit_to_menu);
            quitToMenuBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    popup.dismiss();
                    Intent backToMenu = new Intent(Game.this, MainMenu.class);
                    startActivity(backToMenu);
                    finish();
                }
            });

            Button nextLevelBtn = (Button) popup.findViewById(R.id.next_level);
            nextLevelBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    level++;

                    if (level == 5 || level == 10) {
                        Intent nextLevel = new Intent(Game.this, Quiz.class);
                        nextLevel.putExtra("Level", "" + level);
                        nextLevel.putExtra("Total Score", "" + totalScore);
                        nextLevel.putExtra("Lives", "" + lives);
                        startActivity(nextLevel);
                        finish();
                    } else if (level < 16) {
                        Intent nextLevel = new Intent(Game.this, Game.class);
                        nextLevel.putExtra("Level", "" + level);
                        nextLevel.putExtra("Total Score", "" + totalScore);
                        nextLevel.putExtra("Lives", "" + lives);
                        startActivity(nextLevel);
                        finish();
                    } else {
                        Toast.makeText(Game.this, "Congratulation, you win!!",
                                Toast.LENGTH_SHORT).show();

                        Intent backToMainMenu = new Intent(Game.this,
                                MainMenu.class);
                        startActivity(backToMainMenu);
                        finish();
                    }
                }
            });

            Button postToFbBtn = (Button) popup.findViewById(R.id.post_to_fb);
            postToFbBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    /*
     * Finish the game
     *
     * @author 8B Pham Hung Cuong
     */
    private void finishGame(int currentRow, int currentColumn) {
        stopTimer(); // stop timer
        isGameStart = false;

        // show all traps
        // disable all traps
        for (int row = 1; row < numberOfRows + 1; row++) {
            for (int column = 1; column < numberOfColumns + 1; column++) {
                // disable block
                cells[row][column].disableCell();

                // block has trap and is not flagged
                if (cells[row][column].hasTrap()
                        && !cells[row][column].isFlagged()) {
                    // set trap icon
                    cells[row][column].setTrapIcon(false);
                }

                // block is flagged and doesn't not have trap
                if (!cells[row][column].hasTrap()
                        && cells[row][column].isFlagged()) {
                    // set flag icon
                    cells[row][column].setFlagIcon();
                }

                // block is flagged
                if (cells[row][column].isFlagged()) {
                    // disable the block
                    cells[row][column].setClickable(false);
                }

                // set treasure icon
                if (cells[row][column].hasTreasure()) {
                    // set treasure icon
                    cells[row][column].setTreasureIcon();
                }
            }
        }

        // trigger trap
        cells[currentRow][currentColumn].triggerTrap();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (timer <= 0) {
                    mImgViewResult.setBackgroundResource(R.drawable.timeout);
                }
                mImgViewResult.setVisibility(View.VISIBLE);
                mImgViewResult.bringToFront();
                mImgViewResult.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mImgViewResult.setVisibility(View.GONE);

                        if (!isGameOver) {
                            isGameOver = true; // mark game as over
                            final Dialog popup = new Dialog(Game.this);
                            popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            popup.getWindow()
                                    .setBackgroundDrawable(
                                            new ColorDrawable(
                                                    android.graphics.Color.TRANSPARENT));
                            popup.setContentView(R.layout.finish_popup);
                            // Set dialog title
                            // TODO time up

                            // popup.setTitle("Say something");
                            popup.setCancelable(false);

                            finalScoreText = (TextView) popup
                                    .findViewById(R.id.finalScore);
                            finalScoreText.setTypeface(font);
                            finalScoreText.setText("" + totalScore);

                            finalTimeText = (TextView) popup
                                    .findViewById(R.id.finalTime);
                            finalTimeText.setTypeface(font);
                            finalTimeText.setText("" + timer);

                            popup.show();

                            Button saveRecordBtn = (Button) popup
                                    .findViewById(R.id.save_record);
                            saveRecordBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                                    Game.this);

                                            alert.setTitle("Enter your name");

                                            // Set an EditText view to get user
                                            // input
                                            final EditText input = new EditText(
                                                    Game.this);
                                            input.setText("Playername");
                                            alert.setView(input);

                                            alert.setPositiveButton(
                                                    "Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int whichButton) {
                                                            String value = input
                                                                    .getText()
                                                                    .toString();
                                                            // Do something with
                                                            // value!
                                                            setHighScore(value,
                                                                    totalScore,
                                                                    level);

                                                        }
                                                    });

                                            alert.setNegativeButton(
                                                    "Cancel",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int whichButton) {
                                                            // Canceled.
                                                        }
                                                    });

                                            alert.show();
                                        }
                                    });

                            Button quitToMenuBtn = (Button) popup
                                    .findViewById(R.id.quit_to_menu);
                            quitToMenuBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            popup.dismiss();
                                            Intent backToMenu = new Intent(
                                                    Game.this, MainMenu.class);
                                            startActivity(backToMenu);
                                            finish();
                                        }
                                    });

                            Button postToFbBtn = (Button) popup
                                    .findViewById(R.id.post_to_fb);
                            postToFbBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            popup.dismiss();
                                        }
                                    });
                        }
                    }
                }, 2000);
            }
        }, 500);
    }

    /*
     * Set high score
     *
     * @author 8A Tran Trong Viet
     *
     * @param sc: savedInstanceState: the state of previous game
     */
    public void setHighScore(String playerName, int score, int level) {
        try {
            if (score > 0) {

                SharedPreferences.Editor scoreEdit = gamePrefs.edit();
                // get existing scores
                String scores = gamePrefs.getString("highScores", "");

                // check for scores
                if (scores.length() > 0) {

                    List<Score> scoreStrings = new ArrayList<Score>();
                    String[] exScores = scores.split("\\|");

                    // add score object for each
                    for (String eSc : exScores) {
                        String[] parts = eSc.split(" - ");
                        scoreStrings
                                .add(new Score(parts[0], Integer
                                        .parseInt(parts[1]), Integer
                                        .parseInt(parts[2])));
                    }

                    // new score
                    Score newScore = new Score(playerName, score, level);
                    scoreStrings.add(newScore);
                    Collections.sort(scoreStrings);

                    // get top ten
                    StringBuilder scoreBuild = new StringBuilder("");
                    for (int s = 0; s < scoreStrings.size(); s++) {
                        if (s >= 10)
                            break;
                        if (s > 0)
                            scoreBuild.append("|");
                        scoreBuild.append(scoreStrings.get(s).getScoreText());
                    }
                    // write to prefs
                    scoreEdit.putString("highScores", scoreBuild.toString());
                    scoreEdit.commit();

                } else {
                    // no existing scores
                    scoreEdit.putString("highScores", String.format("%s - %d - %d", playerName, score, level));
                    scoreEdit.commit();
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

	/*
     * Stop the time
	 * 
	 * @author 8B Pham Hung Cuong
	 */

    /*
     * Start time time
     *
     * @author: 8B Pham Hung Cuong
     */
    public void startTimer() {
        clock.removeCallbacks(updateTimeElapsed);
        clock.postDelayed(updateTimeElapsed, 1000);
    }

    public void stopTimer() {
        clock.removeCallbacks(updateTimeElapsed);
    }

    /*
     * Save game to continue
     *
     * @author 8A Tran Trong Viet
     *
     * @param _level the current level
     *
     * @param _score the current score
     *
     * @param _lives the current lives
     */
    private void saveGameState(int _level, int _score, int _lives) {
        SharedPreferences.Editor gameStateEdit = gamePrefs.edit();

        gameStateEdit.putString("saveGame", String.format("%d - %d - %d", _level, _score, _lives));
        gameStateEdit.commit();
    }

}
