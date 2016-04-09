package com.example.treasurehunt.com.example.treasurehunt.cell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.treasurehunt.R;
import com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state.CellCondition;
import com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state.CellStateTransition;
import com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state.CellStateTransitionHandle;

import java.util.Random;

/*
 * The instance of this class is the cell in game
 * @author group 8
 */

public class Cell extends Button implements CellStateTransitionHandle {
    private static final int[] resourceIds = new int[] {R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6, R.drawable.c7, R.drawable.c8};

    private CellStatus cellStatus;
    private CellCondition cellCondition;
    private CellStateTransition cellState;
    private boolean isFlagged; // The cell is flag
    private boolean isDoubt; // The cell is marked as doubt
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
        cellCondition = CellCondition.COVER;
        cellStatus = CellStatus.NONE;
        isFlagged = false;
        isDoubt = false;
        isClickable = true;
        numberOfTrapInSurrounding = 0;

        switch (new Random().nextInt() % 2) {
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
        return cellCondition == CellCondition.COVER;
    }

    /*
     * Is cell a treasure
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean hasTreasure() {
        return cellStatus == CellStatus.TREASURE;
    }

    /*
     * Is cell a trap
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean hasTrap() {
        return cellStatus == CellStatus.TRAP;
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
        cellStatus = CellStatus.TREASURE;
    }

    /*
     * Set cell as a trap underneath
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void setTrap() {
        cellStatus = CellStatus.TRAP;
    }

    /*
     * Set font as bold
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    private void setBoldFont() {
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
        if (cellCondition == CellCondition.OPEN)
            return;

        cellCondition = CellCondition.OPEN;
        if (this.numberOfTrapInSurrounding == 0) {
            isClickable = false;
        }

        if (hasTrap()) {
            setTrapIcon(false);
            return;
        }

        // update with the nearby trap count
        setNumberOfSurroundingTraps(numberOfTrapInSurrounding);
    }

    /*
     * Set text as nearby trap count
     *
     * @author 8A Tran Trong Viet
     *
     * @param text number of trap surrounding
     */
    protected void updateNumber(int resourceId) {
        if (resourceId > 0 || resourceId < 9) {
            setBackgroundResource(resourceIds[resourceId - 1]);
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
        setTrapIcon(true);
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
        if (hasTrap()) {
            return String.format("This is a trap %s", numberOfTrapInSurrounding);
        } else {
            return String.format("This is not a trap %s", numberOfTrapInSurrounding);
        }
    }

    @Override
    public void setCellState(CellStateTransition cellState) {
        this.cellState = cellState;
    }

    @Override
    public void onCellClick() {
        this.cellState.nextState(this);
    }
}
