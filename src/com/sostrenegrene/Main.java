package com.sostrenegrene;

import com.sostrenegrene.database.DB_Writer;
import com.sostrenegrene.kundetelling_no.RunKundetelling;
import com.sostrenegrene.vemcount.RunVemcount;
import dk.mudlogic.tools.database.MSSql;
import dk.mudlogic.tools.log.LogFactory;
import dk.mudlogic.tools.log.LogTracer;

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

        //Clear table if selected
        if (Main.OPTIONS.get("clear").equals("1")) { new DB_Writer(Main.SQL).clearDB(); }

        new RunKundetelling(Main.OPTIONS);
        new RunVemcount(Main.OPTIONS);
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
        Main.OPTIONS.put("days","31");
        //Main.OPTIONS.put("from","-6");
        //Main.OPTIONS.put("to","-5");
        Main.OPTIONS.put("clear","1");
        Main.OPTIONS.put("sleep_timer","1");

        vemcountOptions();
        kundetellingOptions();

    }

    /** Standard options for vemcount.com
     *
     */
    private static void vemcountOptions() {
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

    /** Standard options for kundetelling.no
     *
     */
    private static void kundetellingOptions() {
        //API url
        Main.OPTIONS.put("kundetelling_api_url","http://www.kundetelling.no/ws/publicservice.asmx/ExportAll");
        //API key
        Main.OPTIONS.put("kundetelling_api_username","kundeteller@sostrenegrene.com");
        //API search type
        Main.OPTIONS.put("kundetelling_api_password","8RUEnL");

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
            if (s.contains("-shops"))       { Main.OPTIONS.put("shops",s.substring(s.indexOf(":")+1)); }

        }


    }


}
