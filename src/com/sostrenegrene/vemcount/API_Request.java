package com.sostrenegrene.vemcount;

import com.sostrenegrene.Main;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.web.http.HttpQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by soren.pedersen on 12-04-2017.
 */
public class API_Request {

    private LogTracer log = new LogFactory().tracer();
    private HttpQuery web = new HttpQuery();

    private String dataset;
    private String response;
    private JSONArray response_json = null;
    private boolean response_success;
    private boolean request_overload = false;

    public API_Request(String dataset) {
        log.setTracerTitle(API_Request.class);

        this.dataset = dataset;

        //Run request right away
        request();
    }

    /** Returns the request response if successful
     *
     * @return JSONArray | NULL
     */
    public JSONArray getResponse() {
        return response_json;
    }

    /** Returns if the request was a success
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return response_success;
    }

    /** Request overload on server
     *
     * @return boolean
     */
    public boolean isOverloaded() { return request_overload; }

    /** Sends a request to the API server
     *
     */
    public void request() {

        String query = "data="+dataset;
        //query = "data={\"api_key\":\"E1Re6cqAy8Imw7UfAmxLieAXUKU2UfOg\",\"limit\":\"1000\",\"group_by\":\"shop_id\",\"interval\":\"15min\",\"id\":\"9564\",\"type\":\"custom_shop_id\",\"date_from\":\"2017-05-04 00:00\"}";
        //log.trace(query);

        response = web.get(Main.OPTIONS.get("vemcount_api_url"),query);
        //log.trace( response );
        //log.trace(web.getResponseMessage());

        switch(web.getResponseCode()) {
            case 422:
                //Unprocessable Entity
                response_success = false;
                request_overload = false;

                log.error("Response Status: " + web.getResponseCode() + " " + web.getResponseMessage());
                break;

            case 429:
                //Too many requests
                response_success = false;
                request_overload = true;

                log.error("Response Status: " + web.getResponseCode() + " " + web.getResponseMessage());
                break;

            default:
                response_success = true;
                request_overload = false;
                response_json = response_toJSON(response);

                log.error("Response Status: " + web.getResponseCode() + " " + web.getResponseMessage());
                break;
        }

    }

    /** Parses http result to JSONArray
     *
     * @param response
     * @return JSONArray
     */
    private JSONArray response_toJSON(String response) {

        JSONArray jarray;

        //Try to parse response to JSONObject
        try {
            JSONObject jobj = (JSONObject) new JSONParser().parse(response);

            //Try to get "data" field from Object
            try {
                jarray = (JSONArray) jobj.get("data");
            }
            //If it fails return empty JSONArray
            catch (Exception e) {
                log.error("No data object in response!");
                log.error("Response was: " + response);
                log.error(e.getMessage());

                response_success = false;
                //jarray = new JSONArray();
                jarray = null;
            }
        }
        //If JOSNObject could not be parsed return empty JSONArray
        catch (ParseException e) {
            log.error("Could not parse response as JSON");
            log.error("Response was: " + response);

            response_success = false;
            jarray = new JSONArray();
        }

        return jarray;
    }


}
