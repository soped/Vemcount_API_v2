package com.sostrenegrene.StoreData;

import java.util.ArrayList;

/**
 * Created by soren.pedersen on 17-03-2017.
 */
public class StoreData {

    private ArrayList<StoreDataRow> rows = new ArrayList<>();

    public StoreData() {
    }

    public void addRow(StoreDataRow row) {
        rows.add(row);
    }

    public StoreDataRow[] getRows() {
        return rows.toArray(new StoreDataRow[rows.size()]);
    }

    public int total_count_in() {
        int out = 0;
        for (StoreDataRow sdr : getRows()) {
            out += sdr.count_in;
        }

        return out;
    }

    public int total_count_out() {
        int out = 0;
        for (StoreDataRow sdr : getRows()) {
            out += sdr.count_out;
        }

        return out;
    }
}
