package com.example.treasurehunt.com.example.treasurehunt.cell;

import android.content.Context;

/**
 * Created by library on 2016/04/10.
 */
public class Cells {
    private Cell cells[][];
    int rows;
    int cols;

    public Cells(Context context, int rows, int cols) {
        cells = new Cell[rows + 2][cols + 2];
        this.rows = rows;
        this.cols = cols;
    }
}
