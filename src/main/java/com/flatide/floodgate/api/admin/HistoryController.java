package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.agent.logging.LoggingManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class HistoryController {
    @GetMapping(path="/history")
    public @ResponseBody
    List get() throws Exception {

        return new ArrayList<String>();
    }

    /*
    @PostMapping(path="/history")
    public @ResponseBody
    Map insert(@RequestBody JSONObject data) throws Exception {
        BasicConnector db = new DBConnector("jdbc:oracle:thin:@localhost:32771/xe", "blindcat", "yawnfish", "12monkeys" );

        HashMap<String, String> result = new HashMap<>();
        try {
            db.connect();

            ArrayList<Map> items = (ArrayList<Map>)data.get("ITEMS");

            Map<String, String> ruleMap = new HashMap<>();
            //      source  target
            ruleMap.put("CODE", "NAME");
            ruleMap.put("+sysdate", "IF_DATE");
            ruleMap.put(">Seq", "VALUE");

            MappingRule mappingRule = new MappingRule();
            mappingRule.addRule(ruleMap);

            db.create(mappingRule, items);

            result.put("result", "ok");
        } catch( Exception e) {
            result.put("result", "error");
            result.put("cause", e.toString());
        } finally {
            db.close();

        }

        result.put("sent", db.getSent());

        return result;
    }
     */
}