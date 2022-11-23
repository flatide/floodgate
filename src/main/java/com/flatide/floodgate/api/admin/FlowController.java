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

import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.FloodgateConstants;
import com.flatide.floodgate.agent.meta.MetaManager;

import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class FlowController {
    @GetMapping(path="/flow")
    public @ResponseBody List get(
            @RequestParam(required = false) String id,
            @RequestParam(required = false, defaultValue = "1") int from,
            @RequestParam(required = false, defaultValue = "-1") int to
    ) throws Exception {
        try {
            return MetaManager.shared().readList(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW), id);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(path="/flow")
    public @ResponseBody Map post(
            @RequestBody Map<String, Object> data
    ) throws Exception {
        try {
            long cur = System.currentTimeMillis();
            Timestamp current = new Timestamp(cur);

            data.put("CREATE_DATE", current);
            data.put("MODIFY_DATE", current);
            MetaManager.shared().insert(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW), "ID", data, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping(path="/flow")
    public @ResponseBody Map put(
            @RequestBody Map<String, Object> data
    ) throws Exception {
        try {
            Map old = MetaManager.shared().read(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW), (String) data.get("ID"));

            long cur = System.currentTimeMillis();
            Timestamp current = new Timestamp(cur);
            data.put("MODIFY_DATE", current);

            old.put("TABLE_NAME", ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW));

            MetaManager.shared().insert(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_META_HISTORY), "ID", old, true);

            MetaManager.shared().update(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW), "ID", data, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping(path="/flow")
    public @ResponseBody Map delete(
            @RequestParam(required = true) String id
        ) throws Exception {
        try {
            Map old = MetaManager.shared().read(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW), id);

            old.put("TABLE_NAME", ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW));

            MetaManager.shared().insert(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_META_HISTORY), "ID", old, true);

            MetaManager.shared().delete(ConfigurationManager.shared().getString(FloodgateConstants.META_SOURCE_TABLE_FOR_FLOW), id, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
