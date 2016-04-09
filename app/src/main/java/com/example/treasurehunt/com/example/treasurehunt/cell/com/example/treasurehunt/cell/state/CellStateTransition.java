package com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state;

/**
 * Created by library on 2016/04/09.
 */
public interface CellStateTransition {
    /**
     * @return
     */
    boolean isFlagged();

    /**
     *
     * @return
     */
    boolean isDoubt();

    /**
     * Move the cell to the other state
     *
     * @param cell
     */
    void nextState(CellStateTransitionHandle cell);
}
