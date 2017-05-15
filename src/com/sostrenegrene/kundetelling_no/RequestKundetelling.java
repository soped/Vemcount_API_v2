package com.sostrenegrene.kundetelling_no;

import com.sostrenegrene.StoreData.Make_Dataset;
import com.sostrenegrene.StoreData.StoreData;
import com.sostrenegrene.StoreData.StoreDataRow;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by soren.pedersen on 18-04-2017.
 */
public class RequestKundetelling {

    private LogTracer log = new LogFactory().tracer();
    private Hashtable<String,String> options;
    private StoreData result = null;
    private JSONArray json_result = null;

    /** Starts a new data request from vemcount
     *
     * @param options Hashtable<String,String>
     */
    public RequestKundetelling(Hashtable<String,String> options) {
        log.setTracerTitle(RequestKundetelling.class);

        this.options = options;

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

        String to = "";
        String from = "";
        try {
            to = "to=" + URLEncoder.encode(period.get(0)[1], "UTF-8");
            from = "from=" + URLEncoder.encode(period.get((period.size() - 1))[0], "UTF-8");
        }
        catch(Exception e) {}

        String url = "username=" + options.get("kundetelling_api_username")+"&password=" + options.get("kundetelling_api_password") + "&"+from+"&"+to;

        return url;
    }

    /** Runs the request
     * Pauses and retries the request, if server returns too many requests
     *
     */
    private void doRequest() {
        log.info("Requesting data");

        String dataset = makeDataSet();
        Kundetelling_API_Request request = new Kundetelling_API_Request( dataset );
        //request.request();

        if (request.isSuccess()) {
            json_result = request.getResponse();
            result = make_StoreData(json_result);
        }
    }

    private StoreData make_StoreData(JSONArray json) {

        StoreData sd = new StoreData();

        JSONObject[] obj = (JSONObject[]) json.toArray(new JSONObject[json.size()]);
        for (JSONObject item : obj) {
            log.info("Found shop: " + item.get("shop_id"));

            StoreDataRow sr = new StoreDataRow();
            sr.shop_id          = ""+item.get("shop_id");
            sr.custom_shop_id   = ""+item.get("shop_id");
            sr.is_entrance      = "1";
            sr.ip               = "";
            sr.created_at       = ""+item.get("date");
            sr.created_at_unix  = "0";
            sr.sensor_name      = "";
            sr.sensor_id        = "";
            sr.count_in         = Integer.parseInt( (String) item.get("visit_count") );
            sr.count_out        = 0;

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
