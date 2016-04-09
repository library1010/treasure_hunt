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
import com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state.DoubtCellState;
import com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state.FlagCellState;
import com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state.NormalCellState;

import java.util.Random;

/*
 * The instance of this class is the cell in game
 * @author group 8
 */

public class Cell extends Button implements CellStateTransitionHandle {
    private static final int[] resourceIds = new int[]{R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6, R.drawable.c7, R.drawable.c8};

    private CellStatus cellStatus;
    private CellCondition cellCondition;
    private CellStateTransition cellState;

    /**
     * Number of traps in the nearby cells
     */
    private int surroundTraps;

    /*
     * Constructor
     *
     * @author 8B Pham Hung Cuong
     *
     * @param context context
     */
    public Cell(Context context) {
        super(context);
        setDefaults();
    }

    /*
     * Constructor
     *
     * @author 8A Tran Trong Viet
     */
    public Cell(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaults();
    }

    /*
     * Constructor
     *
     * @author 8A Tran Trong Viet
     */
    public Cell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaults();
    }

    /*
     * Set default properties for the Cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    private void setDefaults() {
        cellCondition = CellCondition.COVER;
        cellStatus = CellStatus.NONE;
        cellState = new NormalCellState();
        surroundTraps = 0;

        if (new Random().nextInt() % 2 == 1) {
            this.setBackgroundResource(R.drawable.cell2);
        } else {
            this.setBackgroundResource(R.drawable.cell1);
        }

        this.setTypeface(null, Typeface.BOLD);
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
        return cellState.isFlagged();
    }

    /*
     * Is cell marked as doubt
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean isDoubted() {
        return cellState.isDoubt();
    }

    /*
     * Can cell receive click event
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public boolean isClickable() {
        return cellCondition == CellCondition.COVER
                || (cellCondition == CellCondition.OPEN && surroundTraps != 0);
    }

    /*
     * Mark cell as flagged
     *
     * @author 8A Tran Trong Viet
     *
     * @param flagged boolean variable
     */
    public void setFlag() {
        cellState = new FlagCellState();
    }

    /*
     * Set question mark for the cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param questionMarked boolean variable
     */
    public void setDoubt() {
        cellState = new DoubtCellState();
    }

    /*
     * Mark the cell as disabled/opened and update the number of nearby traps
     *
     * @author 8A Tran Trong Viet
     *
     * @param number number of traps surrounded this cell
     */
    public void setSurroundTraps(int number) {
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
    public void setTreasureIcon() {
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
        if (!enabled) {
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
    public void setFlagIcon() {
        this.setBackgroundResource(R.drawable.flag);
    }

    /*
     * Set trap as doubt and set cell as disabled/opened if false is passed
     *
     * @author 8A Tran Trong Viet
     *
     * @param enabled boolean variable
     */
    public void setDoubtIcon(boolean enabled) {
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
        this.setBackgroundResource(R.drawable.cell1);
    }

    /*
     * Disable this cell
     *
     * @author 8C Pham Duy Hung
     */
    public void disableCell() {
        cellCondition = CellCondition.END_GAME;
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
     * Uncover this cell
     *
     * @author 8A Tran Trong Viet
     *
     * @param
     */
    public void OpenCell() {
        if (cellCondition != CellCondition.COVER)
            return;

        cellCondition = CellCondition.OPEN;

        if (hasTrap()) {
            setTrapIcon(false);
            return;
        }

        // update with the nearby trap count
        setSurroundTraps(surroundTraps);
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
    public int getSurroundTraps() {
        return surroundTraps;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#toString()
     */
    @Override
    public String toString() {
        if (hasTrap()) {
            return String.format("This is a trap %s", surroundTraps);
        }
        return String.format("This is not a trap %s", surroundTraps);
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
