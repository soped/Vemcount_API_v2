package com.sostrenegrene.database;


import com.sostrenegrene.StoreData.StoreData;
import com.sostrenegrene.StoreData.StoreDataRow;
import dk.mudlogic.tools.database.MSSql;
import dk.mudlogic.tools.Objects.arrays.Arrays;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;

import java.sql.SQLException;

/**
 * Created by soren.pedersen on 04-05-2017.
 */
public class DB_Writer {

    private LogTracer log = new LogFactory().tracer();
    private MSSql sql;

    public DB_Writer(MSSql sql) {
        this.sql = sql;
    }

    public void clearDB() {
        String query = "TRUNCATE TABLE vemcount_data";
        log.warning("Clearing all data");
        try {
            sql.query(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save_StoreData(StoreData store) {
        Arrays arrays = new Arrays();
        Object[] o = arrays.split(store.getRows(),1000);

        int total_rows = 0;
        for (int i=0; i<o.length; i++) {
            StoreDataRow[] rows = (StoreDataRow[]) o[i];

            int add=0;
            String inserts = "";
            for (StoreDataRow row : rows) {

                boolean entrance = (row.is_entrance.equals("1"));

                //Only add data if it's from entrance counter
                if ( entrance ) {
                    if (add > 0) {
                        inserts += ",";
                    }

                    inserts += "(" + row.count_in + "," + row.count_out + ",'" + row.created_at + "'," + row.created_at_unix + ",'" + row.ip + "','Company N/A','" + row.sensor_name + "','" + row.custom_shop_id + "','Timekey N/A','" + row.is_entrance + "')";

                    add++;
                }//END if

                total_rows += add;
            }//ENd foreach

            String query = "INSERT INTO vemcount_data (count_in,count_out,created_at,created_at_unix,ip,company_name,shop_name,custom_shop_id,timekey,is_entrance) VALUES " + inserts;
            //log.trace(query);

            try {
                sql.query(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }//END for

        log.trace("Saved " + total_rows + " rows");
    }



}
