package com.example.treasurehunt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import java.util.Random;

/*
 * The instance of this class is the cell in game
 * @author group 8
 */

public class Cell extends Button {
    /*
     * Properties
     */
    private boolean isTrapped; // The cell is trap or not
    private boolean isTreasure; // The cell is treasure or not
    private boolean isFlagged; // The cell is flag
    private boolean isDoubt; // The cell is marked as doubt
    private boolean isCovered; // is cell covered yet
    private boolean isClickable; // can cell accept click events
    private int numberOfTrapInSurrounding; // number of traps in nearby cells

    /*
     * Constructor
     *
     * @author 8B Pham Hung Cuong
     *
     * @param context context
     */
    public Cell(Context context) {
        super(context);
    }

    /*
     * Constructor
     *
     * @author 8A Tran Trong Viet
     */
    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * Constructor
     *
     * @author 8A Tran Trong Viet
     */
    public Cell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * Set default properties for the Cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void setDefaults() {
        isCovered = true;
        isTrapped = false;
        isFlagged = false;
        isDoubt = false;
        isClickable = true;
        numberOfTrapInSurrounding = 0;

        Random r = new Random();
        switch (r.nextInt() % 2) {
            case 0:
                this.setBackgroundResource(R.drawable.cell1);
                break;

            case 1:
                this.setBackgroundResource(R.drawable.cell2);
                break;
            default:
                this.setBackgroundResource(R.drawable.cell1);
                break;
        }

        // this.setBackgroundResource(R.drawable.square_blue);
        setBoldFont();
    }

    /*
     * Is cell covered?
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean isCovered() {
        return isCovered;
    }

    /*
     * Is cell a treasure
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean hasTreasure() {
        return isTreasure;
    }

    /*
     * Is cell a trap
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean hasTrap() {
        return isTrapped;
    }

    /*
     * Is cell flagged
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean isFlagged() {
        return isFlagged;
    }

    /*
     * Is cell marked as doubt
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean isDoubted() {
        return isDoubt;
    }

    /*
     * Can cell receive click event
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean isClickable() {
        return isClickable;
    }

    /*
     * Mark cell as flagged
     *
     * @author 8A Tran Trong Viet
     *
     * @param flagged boolean variable
     */
    public void setFlag(boolean flagged) {
        isFlagged = flagged;
        if (isFlagged) {
            isClickable = true;
        }
    }

    /*
     * Set question mark for the cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param questionMarked boolean variable
     */
    public void setDoubt(boolean questionMarked) {
        isDoubt = questionMarked;
        if (isDoubt) {
            isClickable = true;
        }
    }

    /*
     * Mark the cell as disabled/opened and update the number of nearby traps
     *
     * @author 8A Tran Trong Viet
     *
     * @param number number of traps surrounded this cell
     */
    public void setNumberOfSurroundingTraps(int number) {
        // this.setBackgroundResource(R.drawable.square_grey);
        this.setBackgroundResource(R.drawable.empty);
        updateNumber(number);
    }

    /*
     * Set treasure icon for cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param enabled boolean variable
     */
    public void setTreasureIcon(boolean enabled) {
        // this.setText("T");
        this.setBackgroundResource(R.drawable.treasure);
    }

    /*
     * Set trap icon for cell and set cell as disabled/opened if false is passed
     *
     * @author 8A Tran Trong Viet
     *
     * @param enabled boolean variable
     */
    public void setTrapIcon(boolean enabled) {
        // this.setText("M");

        if (!enabled) {
            // this.setBackgroundResource(R.drawable.square_grey);
            this.setBackgroundResource(R.drawable.trap);
            this.setTextColor(Color.RED);
        } else {
            this.setTextColor(Color.BLACK);
        }
    }

    /*
     * Set trap as flagged and set cell as disabled/opened if false is passed
     *
     * @author 8A Tran Trong Viet
     *
     * @param enabled boolean variable
     */
    public void setFlagIcon(boolean enabled) {
        // this.setText("F");
        this.setBackgroundResource(R.drawable.flag);

        // if (!enabled) {
        // // this.setBackgroundResource(R.drawable.square_grey);
        // this.setBackgroundResource(R.drawable.flag);
        // this.setTextColor(Color.RED);
        // } else {
        // this.setTextColor(Color.BLACK);
        // }
    }

    /*
     * Set trap as doubt and set cell as disabled/opened if false is passed
     *
     * @author 8A Tran Trong Viet
     *
     * @param enabled boolean variable
     */
    public void setDoubtIcon(boolean enabled) {
        // this.setText("?");
        // this.setBackgroundResource(R.drawable.square_blue);
        this.setBackgroundResource(R.drawable.doubt);

        if (!enabled) {
            this.setTextColor(Color.RED);
        } else {
            this.setTextColor(Color.BLACK);
        }
    }

    /*
     * Clear all icons/text
     *
     * @author 8A Tran Trong Viet
     */
    public void clearAllIcons() {
        // this.setText("");
        this.setBackgroundResource(R.drawable.cell1);
    }

    /*
     * Set cell as disabled/opened if false is passed else enable/close it
     *
     * @author 8A Tran Trong Viet
     *
     * @param enabled boolean variable
     */
    public void setCellAsDisabled(boolean enabled) {
        if (!enabled) {
            // this.setBackgroundResource(R.drawable.square_grey);
            this.setBackgroundResource(R.drawable.empty);

        } else {
            // this.setBackgroundResource(R.drawable.square_blue);
            // this.setBackgroundResource(R.drawable.empty);
        }

    }

    /*
     * Disable this cell
     *
     * @author 8C Pham Duy Hung
     */
    public void disableCell() {
        isClickable = false;
    }

    /*
     * Enable this cell
     *
     * @author 8C Pham Duy Hung
     */
    public void enableCell() {
        isClickable = false;
    }

    /*
     * Set cell as a treasure
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void setTreasure() {
        isTreasure = true;
    }

    /*
     * Set cell as a trap underneath
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void setTrap() {
        isTrapped = true;
    }

    /*
     * Set font as bold
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    private void setBoldFont() // Delete later
    {
        this.setTypeface(null, Typeface.BOLD);
    }

    /*
     * Uncover this cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void OpenCell() {
        // cannot uncover a trap which is not covered
        if (!isCovered)
            return;

        // setCellAsDisabled(false);
        isCovered = false;

        if (this.numberOfTrapInSurrounding == 0) {
            isClickable = false;
        }

        // check if it has trap
        if (hasTrap()) {
            setTrapIcon(false);
        }
        // update with the nearby trap count
        else {
            setNumberOfSurroundingTraps(numberOfTrapInSurrounding);
        }
    }

    /*
     * Set text as nearby trap count
     *
     * @author 8A Tran Trong Viet
     *
     * @param text number of trap surrounding
     */
    public void updateNumber(int text) {
        if (text != 0) {
            // this.setText(Integer.toString(text));

            // select different color for each number
            // we have 1 - 8 trap count
            switch (text) {
                case 1:
                    this.setBackgroundResource(R.drawable.c1);
                    // this.setTextColor(Color.BLUE);
                    break;
                case 2:
                    this.setBackgroundResource(R.drawable.c2);
                    // this.setTextColor(Color.rgb(0, 100, 0));
                    break;
                case 3:
                    this.setBackgroundResource(R.drawable.c3);
                    // this.setTextColor(Color.RED);
                    break;
                case 4:
                    this.setBackgroundResource(R.drawable.c4);
                    // this.setTextColor(Color.rgb(85, 26, 139));
                    break;
                case 5:
                    this.setBackgroundResource(R.drawable.c5);
                    // this.setTextColor(Color.rgb(139, 28, 98));
                    break;
                case 6:
                    this.setBackgroundResource(R.drawable.c6);
                    // this.setTextColor(Color.rgb(238, 173, 14));
                    break;
                case 7:
                    this.setBackgroundResource(R.drawable.c7);
                    // this.setTextColor(Color.rgb(47, 79, 79));
                    break;
                case 8:
                    this.setBackgroundResource(R.drawable.c8);
                    // this.setTextColor(Color.rgb(71, 71, 71));
                    break;
                case 9:
                    // this.setBackgroundResource(R.drawable.empty);
                    // this.setTextColor(Color.rgb(205, 205, 0));
                    break;
            }
        }
    }

    /*
     * Change the cell icon and color of opened cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void triggerTrap() {
        // TODO: add more effect
        setTrapIcon(true);
        // this.setTextColor(Color.RED);
    }

    /*
     * Get number of nearby traps
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public int getNumberOfTrapsInSurrounding() {
        return numberOfTrapInSurrounding;
    }

    /*
     * Set the numberOfTrapInSurrounding
     *
     * @author 8C Pham Duy Hung
     */
    public void setNumberOfTrapsInSurrounding(int number) {
        numberOfTrapInSurrounding = number;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#toString()
     */
    @Override
    public String toString() {
        if (this.isTrapped) {
            return "This is a trap " + numberOfTrapInSurrounding;
        } else {
            return "This is not a trap " + numberOfTrapInSurrounding;
        }
    }
}
