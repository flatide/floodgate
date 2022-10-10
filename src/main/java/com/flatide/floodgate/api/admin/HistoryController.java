/*
 * MIT License
 *
 * Copyright (c) 2022 FLATIDE LC.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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