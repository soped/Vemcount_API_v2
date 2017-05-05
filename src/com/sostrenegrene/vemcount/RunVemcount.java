package com.sostrenegrene.vemcount;

import com.sostrenegrene.stores.GetStores;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.threads.ThreadsUtils;

import java.util.Hashtable;

/**
 * Created by soren.pedersen on 05-05-2017.
 */
public class RunVemcount {

    private LogTracer log = new LogFactory().tracer();

    public RunVemcount(Hashtable<String,String> options) {

        GetStores getstores = new GetStores();
        int[] ids = getstores.getStore_ids();
        ids = new int[]{9564,9563,9553,9575,9586};

        for (int i=0; i<ids.length; i++) {
            int id = ids[i];
            Request req = new Request(options,id);

            try {
                //log.trace(req.getResult().toJSONString());
            }
            catch(Exception e) {
                log.warning("result was null");
            }

            new ThreadsUtils().sleep(1);
        }

    }

}
