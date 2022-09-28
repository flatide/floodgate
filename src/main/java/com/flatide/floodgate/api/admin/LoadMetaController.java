package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.agent.meta.MetaManager;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class LoadMetaController {
    @GetMapping(path="/loadmeta")
    public @ResponseBody String get(
            @RequestParam String table,
            @RequestParam(required = false) String key
    ) {
        try {
            if( key == null || key.isEmpty() ) {
                MetaManager.shared().load(table);
            } else {
                MetaManager.shared().read(table, key, true);
            }
            return "Ok.";
        } catch(Exception e) {
            return e.getMessage() == null? e.getClass().getName() : e.getMessage();
        }
    }
}