package com.sostrenegrene.vemcount;

import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.threads.ThreadsUtils;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by soren.pedersen on 18-04-2017.
 */
public class Request {

    private LogTracer log = new LogFactory().tracer();
    private Hashtable<String,String> options;
    private int store_id;
    private JSONArray result = null;

    /** Starts a new data request from vemcount
     *
     * @param options Hashtable<String,String>
     * @param store_id int
     */
    public Request(Hashtable<String,String> options,int store_id) {
        log.setTracerTitle(Request.class);

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
        API_Request request = new API_Request( dataset );
        //request.request();
        //log.info("Dataset: " + dataset);


        int overloadHalt = 31;
        while( request.isOverloaded() ) {
            log.warning("Request overload. Sleeping for " + overloadHalt + " seconds");
            new ThreadsUtils().sleep( overloadHalt++ );
            request.request();
        }

        result = request.getResponse();
    }

    /** Returns servers request
     *
     * @return JSONArray
     */
    public JSONArray getResult() {
        return result;
    }
}
