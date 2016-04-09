package com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state;

/**
 * Created by library on 2016/04/09.
 */
public interface CellStateTransitionHandle {

    void setCellState(CellStateTransition cellState);

    /**
     * It is not handle the cell click event. Just set other state for the cell
     */
    void onCellClick();
}
