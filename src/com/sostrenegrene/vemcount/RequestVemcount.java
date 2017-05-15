package com.sostrenegrene.vemcount;

import com.sostrenegrene.StoreData.Make_Dataset;
import com.sostrenegrene.StoreData.StoreData;
import com.sostrenegrene.StoreData.StoreDataRow;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.threads.ThreadsUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by soren.pedersen on 18-04-2017.
 */
public class RequestVemcount {

    private LogTracer log = new LogFactory().tracer();
    private Hashtable<String,String> options;
    private int store_id;
    private StoreData result = null;
    private JSONArray json_result = null;

    /** Starts a new data request from vemcount
     *
     * @param options Hashtable<String,String>
     * @param store_id int
     */
    public RequestVemcount(Hashtable<String,String> options, int store_id) {
        log.setTracerTitle(RequestVemcount.class);

        this.options = options;
        this.store_id = store_id;

        //Start request
        doRequest();
    }

    /** Creates a new data set to send to vemcount API
     *
     * @return String (json format)
     */
    private String makeDataSet() {
        Make_Dataset ds = new Make_Dataset();
        ArrayList<String[]> period = ds.makePeriod( Integer.parseInt( options.get("days") ) );

        ds.addData("api_key",options.get("vemcount_api_key"));
        ds.addData("type",options.get("vemcount_api_search_type"));
        ds.addData("id",""+this.store_id);
        ds.addData("interval",options.get("vemcount_api_data_interval"));
        ds.addData("limit",options.get("vemcount_api_data_limit"));
        ds.addData("group_by",options.get("vemcount_api_group_by"));
        ds.addData("date_to", period.get(0)[1] );
        ds.addData("date_from", period.get( (period.size()-1) )[0] );

        return ds.toString();
    }

    /** Runs the request
     * Pauses and retries the request, if server returns too many requests
     *
     */
    private void doRequest() {
        log.info("Requesting data for store: " + this.store_id);

        String dataset = makeDataSet();
        Vemcount_API_Request request = new Vemcount_API_Request( dataset );
        //request.request();
        //log.info("Dataset: " + dataset);

        int overloadHalt = 31;
        while( request.isOverloaded() ) {
            log.warning("RequestVemcount overload. Sleeping for " + overloadHalt + " seconds");

            new ThreadsUtils().sleep( overloadHalt++ );
            request.request();
        }

        if (request.isSuccess()) {
            json_result = request.getResponse();
            result = make_StoreData(json_result);
        }
    }

    private StoreData make_StoreData(JSONArray json) {

        StoreData sd = new StoreData();

        JSONObject[] obj = (JSONObject[]) json.toArray(new JSONObject[json.size()]);
        for (JSONObject item : obj) {
            StoreDataRow sr = new StoreDataRow();
            sr.shop_id          = ""+item.get("shop_id");
            sr.custom_shop_id   = ""+item.get("custom_shop_id");
            sr.is_entrance      = ""+item.get("is_entrance");
            sr.ip               = ""+item.get("ip");
            sr.created_at       = ""+item.get("created_at");
            sr.created_at_unix  = ""+item.get("created_at_unix");
            sr.sensor_name      = ""+item.get("sensor_name");
            sr.sensor_id        = ""+item.get("sensor_id");
            sr.count_in         = Integer.parseInt( (String) item.get("count_in") );
            sr.count_out        = Integer.parseInt( (String) item.get("count_out") );

            sd.addRow(sr);
        }

        return sd;
    }

    /** Returns servers request as JSON data
     *
     * @return JSONArray
     */
    public JSONArray getJSONResult() {
        return json_result;
    }

    /** Returns servers request
     *
     * @return StoreData
     */
    public StoreData getResult() {
        return result;
    }
}
