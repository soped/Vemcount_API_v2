package com.sostrenegrene.StoreData;

import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.time.TimeHandler;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by soren.pedersen on 12-04-2017.
 */
public class Make_Dataset {

    private LogTracer log = new LogFactory().tracer();
    private Hashtable<String,String> data = new Hashtable<>();

    public Make_Dataset() {
        log.setTracerTitle(Make_Dataset.class);
    }

    private JSONObject setupData() {
        Set ks = data.keySet();
        String[] keys = (String[]) ks.toArray(new String[ks.size()]);

        JSONObject jobj = new JSONObject();
        for (int i=0; i<keys.length; i++) {

            String key = keys[i];
            String value = data.get(key);

            jobj.put(key,value);
        }

        return jobj;
    }

    public void addData(String key,String value) {
        data.put(key,value);
    }

    public JSONObject getDataset() {
        JSONObject o = setupData();

        return o;
    }

    public String toString() {
        return getDataset().toJSONString();
    }


    /** Generate period list to run for each store
     *
     * @param size
     * @return
     */
    public ArrayList<String[]> makePeriod(int size) {

        ArrayList<String[]> period = new ArrayList<>();

        TimeHandler time = new TimeHandler();
        //time.dateFromNow(0,TimeHandler.REVERSE_DATEYEAR_ZEROSHORTTIME);

        int day = 0;
        for (int i=0; i<size; i++) {
            String to = time.dateFromNow(day,TimeHandler.REVERSE_DATEYEAR_ZEROSHORTTIME);
            day -= 1;
            String from = time.dateFromNow(day,TimeHandler.REVERSE_DATEYEAR_ZEROSHORTTIME);
            day -= 1;

            period.add(new String[]{from,to});

            //log.trace(from + "/" + to);
        }


        return period;
    }

}
