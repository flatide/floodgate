package com.flatide.floodgate.system;

//import org.json.simple.parser.ContainerFactory;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/*
 * TODO: Concurrently Reloading
 */

public class JSONUtil {
    private static final JSONUtil instance = new JSONUtil();

    HashMap<String, Object> jsonList = new HashMap<>();
    Map<String, Object> data = null;

    private JSONUtil() {
    }

    public static JSONUtil shared() {
        return instance;
    }

    /*public Boolean load(String filename) {
        File file = new File(filename);
        if( file.exists()) {
            JSONParser parser = new JSONParser();
            ContainerFactory orderedKeyFactory = new ContainerFactory()
            {
                public List creatArrayContainer() {
                    return new ArrayList();
                }

                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }
            };

            try {
                Object json = parser.parse(new FileReader(filename), orderedKeyFactory);
                this.jsonList.put(filename, json);
                System.out.println(filename + " is loaded.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }*/

    public Boolean store(String filename) {
        return true;
    }

    public void dispose(String filename) {
        this.jsonList.remove(filename);
        System.out.println(filename + " is disposed.");
    }

    /*public Object get(String filename, String key) {
        Map json = (Map) this.jsonList.get(filename);
        if( json == null ) {
            if( load(filename) ) {
                json = (Map) this.jsonList.get(filename);
                dispose(filename);
            }
        } else {
            System.out.println(filename + " is already loaded.");
        }

        if( json == null ) {
            return null;
        }

        return json.get(key);
    }*/
}
