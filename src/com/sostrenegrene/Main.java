package com.sostrenegrene;

import com.sostrenegrene.stores.GetStores;
import com.sostrenegrene.vemcount.Request;
import dk.mudlogic.tools.database.MSSql;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;
import dk.mudlogic.tools.threads.ThreadsUtils;

import java.sql.SQLException;
import java.util.Hashtable;

public class Main {

    public static Hashtable<String,String> OPTIONS = new Hashtable<>();
    public static MSSql SQL;

    public static LogTracer log = new LogFactory().tracer();

    public static void main(String[] args) {

        Main.log.setTracerTitle(Main.class);

        //Load standard options
        setStandardOptions();

        //Manage input options
        options_handler(args);

        //Open db connection
        db_Connect();

        run();
    }

    private static void run() {
        GetStores getstores = new GetStores();
        int[] ids = getstores.getStore_ids();
        ids = new int[]{9564};

        for (int i=0; i<ids.length; i++) {
            int id = ids[i];
            Request req = new Request(Main.OPTIONS,id);

            try {
                //log.trace(req.getResult().toJSONString());
            }
            catch(Exception e) {
                log.warning("result was null");
            }

            new ThreadsUtils().sleep(1);
        }

    }

    /** Connect to database, for saving data
     *
     */
    private static void db_Connect() {
        //Setup database connection
        //MSSql sql = new MSSql(db_host,username,password,database);
        Main.SQL = new MSSql(Main.OPTIONS.get("db_host"),Main.OPTIONS.get("username"),Main.OPTIONS.get("password"),Main.OPTIONS.get("database"));

        //Connect to database
        try {
            Main.SQL.connect();
            //s_sql.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Set standard options
     *
     */
    private static void setStandardOptions() {

        //If no input options is supplied
        Main.OPTIONS.put("username","cloudmon");
        Main.OPTIONS.put("password","Grenes1234");
        Main.OPTIONS.put("db_host","localhost\\SQLEXPRESS");
        Main.OPTIONS.put("database","Toolbox");
        Main.OPTIONS.put("days","5");
        //Main.OPTIONS.put("from","-6");
        //Main.OPTIONS.put("to","-5");
        Main.OPTIONS.put("clear","1");
        Main.OPTIONS.put("sleep_timer","1");

        //API url
        Main.OPTIONS.put("vemcount_api_url","https://login.vemcount.com/api/fetch_data/");
        //API key
        Main.OPTIONS.put("vemcount_api_key","E1Re6cqAy8Imw7UfAmxLieAXUKU2UfOg");
        //API search type
        Main.OPTIONS.put("vemcount_api_search_type","custom_shop_id");
        //API data interval
        Main.OPTIONS.put("vemcount_api_data_interval","15min");
        //API group data by
        Main.OPTIONS.put("vemcount_api_group_by","mac_id");
        //API data limit
        Main.OPTIONS.put("vemcount_api_data_limit","10000");

    }

    /** Manage input options
     *
     * @param args String[]
     */
    private static void options_handler(String[] args) {

        for(String s : args) {

            if (s.contains("-username"))    { Main.OPTIONS.put("username",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-password"))    { Main.OPTIONS.put("password",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-dbhost"))      { Main.OPTIONS.put("db_host",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-database"))    { Main.OPTIONS.put("database",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-from"))        { Main.OPTIONS.put("from",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-to"))          { Main.OPTIONS.put("to",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-days"))        { Main.OPTIONS.put("days",s.substring(s.indexOf(":")+1)); }
            if (s.contains("-clear"))       { Main.OPTIONS.put("clear",s.substring(s.indexOf(":")+1)); }

        }


    }


}
