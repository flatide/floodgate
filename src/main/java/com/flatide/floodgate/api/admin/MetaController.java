package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.agent.meta.MetaManager;
import com.flatide.floodgate.agent.meta.MetaTable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class MetaController {
    @GetMapping(path="/meta")
    public @ResponseBody Map get(
            @RequestParam String table,
            @RequestParam(required = false) String id
    ) {
        try {
            if( id == null) {
                MetaTable metaTable = MetaManager.shared().getTable(table);
                if( metaTable == null ) {
                    return null;
                }
                return metaTable.getRows();
            } else {
                return MetaManager.shared().read(table, id);
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(path="/meta")
    public @ResponseBody Map post(
            @RequestBody Map<String, Object> data,
            @RequestParam String table,
            @RequestParam String key
    ) {
        try {
            MetaManager.shared().insert(table, key, data, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(path="/meta")
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