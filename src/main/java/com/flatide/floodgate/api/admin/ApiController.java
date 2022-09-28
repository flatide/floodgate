package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.agent.Config;
import com.flatide.floodgate.agent.meta.MetaManager;
import com.flatide.floodgate.agent.meta.MetaTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class ApiController {
    @Autowired
    private Config config;

    @GetMapping(path="/api")
    public @ResponseBody Map get(
            @RequestParam(required = false) String id,
            @RequestParam(required = false, defaultValue = "1") int from,
            @RequestParam(required = false, defaultValue = "-1") int to
    ) {
        try {
            if( id == null) {
                MetaTable metaTable = MetaManager.shared().getTable((String) config.get("meta.source.tableForAPI"));
                if( metaTable == null ) {
                    return null;
                }
                if( from == 1 && to == -1 )
                    return metaTable.getRows();
                else {
                    Map<Object, Object> temp = new LinkedHashMap<>();
                    LinkedHashMap<String, ? extends Map> result = (LinkedHashMap<String, ? extends Map>) metaTable.getRows();
                    to = result.size();
                    int i = 1;
                    for(Map.Entry entry : result.entrySet()) {
                        if( i > to )
                            break;

                        if( i >= from ) {
                            temp.put(entry.getKey(), entry.getValue());
                        }

                        i++;
                    }

                    return temp;
                }
            } else {
                return MetaManager.shared().read((String) config.get("meta.source.tableForAPI"), id);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(path="/api")
    public @ResponseBody Map post(
            @RequestBody Map<String, Object> data
    ) {
        try {
            String key = (String) data.get("ID");
            MetaManager.shared().insert((String) config.get("meta.source.tableForAPI"), key, data, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(path="/api")
    public @ResponseBody Map put(
            @RequestBody Map<String, Object> data,
            @RequestParam String table,
            @RequestParam String key
    ) {
        try {
            MetaManager.shared().update(table, key, data, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}