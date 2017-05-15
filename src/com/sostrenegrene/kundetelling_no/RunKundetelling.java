package com.sostrenegrene.kundetelling_no;

import com.sostrenegrene.Main;
import com.sostrenegrene.database.DB_Writer;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;

import java.util.Hashtable;

/**
 * Created by soren.pedersen on 05-05-2017.
 */
public class RunKundetelling {

    private LogTracer log = new LogFactory().tracer();

    public RunKundetelling(Hashtable<String,String> options) {
            RequestKundetelling req = new RequestKundetelling(options);

            try {
                new DB_Writer(Main.SQL).save_StoreData(req.getResult());
                //log.trace(req.getJSONResult().toJSONString());
            }
            catch(Exception e) {
                log.warning("result was null");
            }

    }

}
