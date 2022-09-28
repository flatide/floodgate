package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.agent.meta.MetaManager;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class StoreMetaController {

    @GetMapping(path="/storemeta")
    public @ResponseBody String get(
            @RequestParam String table
    ) {
        try {
                MetaManager.shared().store(table, true);

            return "Ok.";
        } catch(Exception e) {
            return e.getMessage() == null? e.getClass().getName() : e.getMessage();
        }
    }
}