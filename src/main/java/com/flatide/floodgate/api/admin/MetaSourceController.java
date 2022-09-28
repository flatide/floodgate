package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.agent.meta.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class MetaSourceController {

    @GetMapping(path="/metasource")
    public @ResponseBody Map get(
            @RequestParam(required = false) String source,
            @RequestParam(required = false, defaultValue = "false") Boolean reset
    ) throws Exception {
        try {
            if( source == null || source.isEmpty() ) {
                return MetaManager.shared().getInfo();
            } else {
                MetaManager.shared().changeSource(source, reset);

                return MetaManager.shared().getInfo();
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}