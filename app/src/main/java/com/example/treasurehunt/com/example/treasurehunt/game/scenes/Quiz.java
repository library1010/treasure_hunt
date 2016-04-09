package com.example.treasurehunt.com.example.treasurehunt.game.scenes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * The quiz level
 * 
 * @author group 8
 */

public class Quiz extends Activity {

    public static final String Quiz_PREFS = "ArithmeticFile";
    // Texts
    Typeface font;
    TextView levelText, scoreText, timeText, livesText, finalScoreText,
            finalTimeText;
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
    private int level = 1;
    private int lives = 0;
    private int totalScore = 0;
    private int step = 0;
    private int cellWidth = 34;
    private int cellPadding = 2;
    // Tracking time
    private Handler clock;
    private int timer;
    ;
    private boolean isQuizOver;
    private boolean isQuizStart;
    // Save Score
    private SharedPreferences QuizPrefs;
    /*
     * This properties must be set up for handling the clock
     *
     * @author 8A Tran Trong Viet
     */
    private Runnable updateTimeElasped = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            long currentMilliseconds = System.currentTimeMillis();
            --timer;

            if (!isQuizOver) {
                timeText.setText("" + timer);
            }

            // add notification
            clock.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            clock.postDelayed(updateTimeElasped, 1000);

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
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_root);
        Options option = new Options();
        option.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.gamebg, option);
        if (bmp != null) {
            layout.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
        }

        map = (TableLayout) findViewById(R.id.Map);

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
                Intent backToMainMenu = new Intent(Quiz.this, MainMenu.class);
                startActivity(backToMainMenu);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Cannot load the game", Toast.LENGTH_SHORT)
                    .show();
            Intent backToMainMenu = new Intent(Quiz.this, MainMenu.class);
            startActivity(backToMainMenu);
        }

        initView();
        quizControl(level);
        startQuizGame();
    }

    /*
     * Initial view
     *
     * @author 8A Tran Trong Viet
     */
    private void initView() {

        font = Typeface.createFromAsset(getBaseContext().getAssets(),
                "fonts/FRANCHISE-BOLD.TTF");
        levelText = (TextView) findViewById(R.id.levelText);
        levelText.setTypeface(font);
        scoreText = (TextView) findViewById(R.id.scoreText);
        scoreText.setTypeface(font);
        timeText = (TextView) findViewById(R.id.timeText);
        timeText.setTypeface(font);
        livesText = (TextView) findViewById(R.id.livesText);
        livesText.setTypeface(font);

        levelText.setText("Quiz " + level);
        scoreText.setText("" + totalScore);
        livesText.setText("" + lives);

        mImgViewResult = (ImageView) findViewById(R.id.img_result);

        numberOfRows = 16;
        numberOfColumns = 30;

        clock = new Handler();
    }

    private void quizControl(int _level) {
        switch (_level) {
            case 5:
                setUpQuiz(300, 60);
                break;
            case 10:
                setUpQuiz(300, 90);
                break;
            default:
                break;

        }

    }

    /*
     * Set up the Quiz properties
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
    private void setUpQuiz(int playTime, int numberOfTraps) {
        totalTraps = numberOfTraps;
        timer = playTime;
    }

    /*
     * Start the Quiz
     *
     * @author 8A Tran Trong Viet
     */
    private void startQuizGame() {

        createMap();
        showMap();

        isQuizOver = false;
        isQuizStart = false;
        timeText.setText("" + timer);

        genMap();
    }

    /*
     * Create new map
     *
     * @author 8A Tran Trong Viet
     */
    protected void createMap() {

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
            }
        }
    }

    /*
     * This method handles the on click event
     *
     * @author 8A Tran Trong Viet
     *
     * @param currentRow the position of the clicked cell
     *
     * @param currentCol the position of the clicked cell
     */
    private void onClickOnCellHandle(int currentRow, int currentColumn) {

        if (!isQuizStart) {
            startTimer();
            isQuizStart = true;
        }

        if (cells[currentRow][currentColumn].isCovered()) {
            rippleUncover(currentRow, currentColumn);

            if (cells[currentRow][currentColumn].hasTrap()) {
                cells[currentRow][currentColumn].OpenCell();
                finishGame(currentRow, currentColumn);
            } else {
                step++;
            }

            if (checkGameWin(cells[currentRow][currentColumn])) {
                winGame();
            }
        }
    }

    /*
     * Check the game wins or not (should do this for changing rules)
     *
     * @author 8A Tran Trong Viet
     *
     * @param cell the cell which is clicked
     */
    private boolean checkGameWin(Cell cell) {
        return step == 10;
    }

    /*
     * finish the game
     *
     * @author 8A Tran Trong Viet
     */
    private void finishGame(int currentRow, int currentColumn) {
        stopTimer(); // stop timer
        isQuizStart = false;

        // show all traps
        // disable all traps
        for (int row = 1; row < numberOfRows + 1; row++) {
            for (int column = 1; column < numberOfColumns + 1; column++) {
                // disable block
                // cells[row][column].setCellAsDisabled(false);
                cells[row][column].disableCell();
            }
        }

        // trigger trap
        cells[currentRow][currentColumn].triggerTrap();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mImgViewResult.setBackgroundResource(R.drawable.trapped);
                mImgViewResult.setVisibility(View.VISIBLE);
                mImgViewResult.bringToFront();
                mImgViewResult.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mImgViewResult.setVisibility(View.GONE);

                        if (!isQuizOver) {
                            isQuizOver = true; // mark game as over
                            final Dialog popup = new Dialog(Quiz.this);
                            popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            popup.getWindow()
                                    .setBackgroundDrawable(
                                            new ColorDrawable(
                                                    android.graphics.Color.TRANSPARENT));
                            popup.setContentView(R.layout.win_popup);

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
                                                    Quiz.this);

                                            alert.setTitle("Enter your name");

                                            // Set an EditText view to get user
                                            // input
                                            final EditText input = new EditText(
                                                    Quiz.this);
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
                                                            popup.dismiss();
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
                                            Intent backToMenu = new Intent(
                                                    Quiz.this, MainMenu.class);
                                            startActivity(backToMenu);
                                        }
                                    });

                            Button nextLevelBtn = (Button) popup
                                    .findViewById(R.id.next_level);
                            nextLevelBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            level++;

                                            Intent nextLevel = new Intent(
                                                    Quiz.this, Game.class);
                                            nextLevel.putExtra("Level", ""
                                                    + level);
                                            nextLevel.putExtra("Total Score",
                                                    "" + totalScore);
                                            nextLevel.putExtra("Lives", ""
                                                    + lives);
                                            startActivity(nextLevel);
                                            finish();
                                        }
                                    });

                            Button postToFbBtn = (Button) popup
                                    .findViewById(R.id.post_to_fb);
                            postToFbBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                        }
                                    });
                        }
                    }
                }, 2000);
            }
        }, 500);
    }

    /*
     * Win the game
     *
     * @author 8A Tran Trong Viet
     */
    private void winGame() {
        // reset all stuffs
        stopTimer();
        isQuizStart = false;
        totalScore += 500;

        // disable all buttons
        // set flagged all un-flagged blocks
        for (int row = 1; row < numberOfRows + 1; row++) {
            for (int column = 1; column < numberOfColumns + 1; column++) {
                cells[row][column].setClickable(false);
                if (cells[row][column].hasTrap()) {
                    cells[row][column].setTrapIcon(true);
                }
            }
        }

        Handler handler = new Handler();
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

                        if (!isQuizOver) {
                            isQuizOver = true; // mark game as over
                            final Dialog popup = new Dialog(Quiz.this);
                            popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            popup.getWindow()
                                    .setBackgroundDrawable(
                                            new ColorDrawable(
                                                    android.graphics.Color.TRANSPARENT));
                            popup.setContentView(R.layout.win_popup);

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
                                                    Quiz.this);

                                            alert.setTitle("Enter your name");

                                            // Set an EditText view to get user
                                            // input
                                            final EditText input = new EditText(
                                                    Quiz.this);
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
                                                            popup.dismiss();
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
                                            Intent backToMenu = new Intent(
                                                    Quiz.this, MainMenu.class);
                                            startActivity(backToMenu);
                                        }
                                    });

                            Button nextLevelBtn = (Button) popup
                                    .findViewById(R.id.next_level);
                            nextLevelBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            // TODO Auto-generated method stub
                                            level++;
                                            totalScore += 1000;
                                            lives += 2;

                                            Intent nextLevel = new Intent(
                                                    Quiz.this, Game.class);
                                            nextLevel.putExtra("Level", ""
                                                    + level);
                                            nextLevel.putExtra("Total Score",
                                                    "" + totalScore);
                                            nextLevel.putExtra("Lives", ""
                                                    + lives);
                                            startActivity(nextLevel);
                                            finish();
                                        }
                                    });

                            Button postToFbBtn = (Button) popup
                                    .findViewById(R.id.post_to_fb);
                            postToFbBtn
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                        }
                    }
                }, 2000);
            }
        }, 500);

    }

    /*
     * Show map procedure
     *
     * @author 8A Tran Trong Viet
     */
    protected void showMap() {
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
    private void genMap() {
        genTraps();
        setTheNumberOfSurroundingTrap();

        for (int row = 0; row < numberOfRows + 2; row++) {
            for (int column = 0; column < numberOfColumns + 2; column++) {
                if (cells[row][column].getSurroundTraps() == 0) {
                    rippleUncover(row, column);
                    return;
                }

            }
        }

    }

    /*
     * Generate the traps position in map
     *
     * @author 8A Tran Trong Viet
     *
     * @param rowClicked the position of the first-clicked-cell
     *
     * @param columnClicked the position of the first-clicked-cell
     */
    private void genTraps() {

        Random rand = new Random();
        int trapRow, trapColumn;

        // set traps excluding the location where user clicked
        for (int row = 0; row < totalTraps; row++) {
            trapColumn = rand.nextInt(numberOfColumns);
            trapRow = rand.nextInt(numberOfRows);
            cells[trapRow + 1][trapColumn + 1].setTrap();
        }
    }

    /*
     * Set the number of surrounding trap
     *
     * @author 8A Tran Trong Viet
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
                    cells[row][column]
                            .setSurroundTraps(nearByTrapCount);
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
     * Open the cells which surrounded the no-trap-surrounded cell continuously
     *
     * @author 8A Tran Trong Viet
     *
     * @param rowClicked the row of the clicked position
     *
     * @param columnClicked the column of the clicked position
     */
    private void rippleUncover(int rowClicked, int columnClicked) {
        if (cells[rowClicked][columnClicked].hasTrap()) {
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
     * Set high score
     *
     * @author 8A Tran Trong Viet
     *
     * @param sc: savedInstanceState: the state of previous Quiz
     */
    public void setHighScore(String playerName, int score, int level) {
        try {
            if (score > 0) {

                SharedPreferences.Editor scoreEdit = QuizPrefs.edit();
                // get existing scores
                String scores = QuizPrefs.getString("highScores", "");

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
                    scoreEdit.putString("highScores", "" + playerName + " - "
                            + score + " - " + level);
                    scoreEdit.commit();
                }

            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	/*
     * Stop the time
	 * 
	 * @author 8A Tran Trong Viet
	 */

    /*
     * Start time time
     *
     * @author 8A Tran Trong Viet
     */
    public void startTimer() {
        clock.removeCallbacks(updateTimeElasped);
        clock.postDelayed(updateTimeElasped, 1000);
    }

    public void stopTimer() {
        // disable call backs
        clock.removeCallbacks(updateTimeElasped);
    }
}
