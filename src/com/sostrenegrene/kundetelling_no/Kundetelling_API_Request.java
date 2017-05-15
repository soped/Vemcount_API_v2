package com.sostrenegrene.kundetelling_no;

import com.sostrenegrene.Main;
import com.sostrenegrene.StoreData.StoreDataRow;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.time.TimeHandler;
import dk.mudlogic.tools.web.http.HttpQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Created by soren.pedersen on 12-04-2017.
 */
public class Kundetelling_API_Request {

    private LogTracer log = new LogFactory().tracer();
    private HttpQuery web = new HttpQuery(false);

    private String dataset;
    private String response;
    private JSONArray response_json = null;
    private boolean response_success;
    private boolean request_overload = false;

    public Kundetelling_API_Request(String dataset) {
        log.setTracerTitle(Kundetelling_API_Request.class);

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

    /** RequestVemcount overload on server
     *
     * @return boolean
     */
    public boolean isOverloaded() { return request_overload; }

    /** Sends a request to the API server
     *
     */
    public void request() {

        String query = dataset;

        //query = "username=kundeteller@sostrenegrene.com&password=8RUEnL&from=01-05-2017%2000:00&to=11-05-2017%2000:00";

        //log.trace(Main.OPTIONS.get("kundetelling_api_url") + query);

        response = web.get(Main.OPTIONS.get("kundetelling_api_url"),query,"\n");

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

                log.warning("Response Status: " + web.getResponseCode() + " " + web.getResponseMessage());
                break;

            case 504:
                //Gateway timeout (act like overload)
                response_success = false;
                request_overload = true;
                log.error("Response Status: " + web.getResponseCode() + " " + web.getResponseMessage());
                break;

            default:
                response_success = true;
                request_overload = false;
                response_json = response_toJSON(response);

                log.info("Response Status: " + web.getResponseCode() + " " + web.getResponseMessage());
                break;
        }

    }

    private JSONArray response_toJSON(String xml) {
        String out = null;
        JSONArray ja;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            Document doc = builder.parse(input);

            //doc.getDocumentElement().normalize();
            NodeList nl = doc.getDocumentElement().getChildNodes();
            Node n = nl.item(0);
            out = n.getTextContent();

            //Remove header names from string (Id;Date;VisitCount)
            //out = out.substring(out.indexOf("VisitCount")+10);//+10 to remove visitcount

            //log.info(">>>" + out);
            String[] rows = out.split("\\n");
            ja = new JSONArray();
            for (int i=0; i<rows.length; i++) {
                String[] items = rows[i].split(";");

                items[1] = items[1].replace(".","-");
                TimeHandler time = new TimeHandler();
                String timestamp = time.reformatTime(items[1],TimeHandler.REVERSE_DATEYEAR_TIME);

                //log.info(items[1] + ": " + timestamp);
                JSONObject jo = new JSONObject();
                jo.put("shop_id",items[0]);
                jo.put("date",timestamp);
                jo.put("visit_count",items[2]);

                if ( (!items[0].equals("Id")) | (!items[1].equals("Date")) | (!items[2].equals("VisitCount")) ) {
                    ja.add(jo);
                }
            }


        }
        catch(Exception e) {
            log.error("No XML data in response!");
            //log.error("Response was: " + response);
            log.error(e.getMessage());

            response_success = false;
            ja = null;
        }

        return ja;
    }


}
