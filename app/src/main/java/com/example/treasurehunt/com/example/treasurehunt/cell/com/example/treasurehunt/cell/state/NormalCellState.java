package com.example.treasurehunt.com.example.treasurehunt.cell.com.example.treasurehunt.cell.state;

/**
 * Created by library on 2016/04/09.
 */
public class NormalCellState implements CellStateTransition {

    @Override
    public boolean isFlagged() {
        return false;
    }

    @Override
    public boolean isDoubt() {
        return false;
    }

    @Override
    public void nextState(CellStateTransitionHandle cell) {
        cell.setCellState(new FlagCellState());
    }
}
