package com.sostrenegrene.vemcount;

import com.sostrenegrene.Main;
import com.sostrenegrene.database.DB_Writer;
import com.sostrenegrene.stores.GetStores;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.threads.ThreadsUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Hashtable;

/**
 * Created by soren.pedersen on 05-05-2017.
 */
public class RunVemcount {

    private LogTracer log = new LogFactory().tracer();

    public RunVemcount(Hashtable<String,String> options) {
        int[] ids;

        try {
            ids = custom_shop_list();
        }
        catch(NullPointerException e) {
            GetStores getstores = new GetStores();
            ids = getstores.getStore_ids();
        }


        for (int i=0; i<ids.length; i++) {
            int id = ids[i];
            RequestVemcount req = new RequestVemcount(options,id);

            try {
                new DB_Writer(Main.SQL).save_StoreData(req.getResult());

                //log.trace(req.getJSONResult().toJSONString());
            }
            catch(Exception e) {
                log.warning("result was null");
            }

            new ThreadsUtils().sleep(1);
        }

    }

    private int[] custom_shop_list() {
        JSONParser parser = new JSONParser();
        JSONArray list = null;
        int[] out = null;

        try {
            list = (JSONArray) parser.parse( Main.OPTIONS.get("shops") );
            out = new int[list.size()];

            for (int i=0; i<list.size(); i++) {
                out[i] = Integer.parseInt(list.get(i).toString());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return out;
    }

}
