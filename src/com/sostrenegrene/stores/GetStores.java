package com.sostrenegrene.stores;

import dk.mudlogic.tools.database.MSSql;
import dk.mudlogic.tools.database.SQLResult;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by soren.pedersen on 24-11-2016.
 */
public class GetStores {

    private LogTracer log = new LogFactory().tracer();

    private String s_username = "vemcount";
    private String s_password = "Vemco1234";
    private String s_db_host = "SGSQL01.grenes.local";
    private String s_database = "SGIDrift2013R2";

    private MSSql sql;
    private SQLResult result;

    public GetStores() {
        log.setTracerTitle(GetStores.class);

        //Connect and fetch store_ids

        sql = new MSSql(s_db_host,s_username,s_password,s_database);

        connect_and_get();
    }

    private void connect_and_get() {
        //Connect to database
        try {
            sql.connect();
            result = get_Stores();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

    }

    private SQLResult get_Stores() {
        SQLResult res = null;
        String query = "SELECT No_ AS store_id FROM [SGIDrift2013R2].[dbo].[SGI$Store]";

        try {
            res = sql.query(query);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return res;
    }

    public Hashtable<String,Object>[] getResult() {
        Hashtable<String,Object>[] out = (Hashtable[]) result.getRows().toArray(new Hashtable[result.getRows().size()]);
        return out;
    }

    public int[] getStore_ids() {
        int[] newout;
        ArrayList<Object> out = new ArrayList<>();
        try {
            Hashtable[] res = getResult();

            //out = new int[res.length];
            for (int i = 0; i < res.length; i++) {
                Hashtable t = res[i];

                //out[i] = (int) t.get("store_id");
                String id_value = (String) t.get("store_id");
                try { out.add( Integer.parseInt( id_value ) ); }
                catch(Exception e) { log.warning("ID is not integer: " + id_value); }
            }

            newout = ArrayUtils.toPrimitive(out.toArray(new Integer[out.size()]));
        }
        catch(Exception e) {
            log.error(e.getMessage());
            log.info("Switching to static array");

            newout = StoreIDs.store_ids;
        }

        log.info("Loaded " + newout.length + " store ids");

        return newout;
    }

}
