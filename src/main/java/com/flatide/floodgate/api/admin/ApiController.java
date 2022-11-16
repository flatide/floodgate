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

import com.flatide.floodgate.agent.Config;
import com.flatide.floodgate.agent.meta.MetaManager;
import com.flatide.floodgate.agent.meta.MetaTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/admin")
public class ApiController {
    @Autowired
    private Config config;

    @GetMapping(path="/api")
    public @ResponseBody List get(
            @RequestParam(required = false) String id,
            @RequestParam(required = false, defaultValue = "1") int from,
            @RequestParam(required = false, defaultValue = "-1") int to
    ) {
        try {
            return MetaManager.shared().readList((String) config.get("meta.source.tableforAPI"), id);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping(path="/api")
    public @ResponseBody Map post(
            @RequestBody Map<String, Object> data
    ) throws Exception {
        try {
            MetaManager.shared().insert((String) config.get("meta.source.tableForAPI"), "ID", data, true);

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
            @RequestParam(required = true ) String id,
            @RequestBody Map<String, Object> data
    ) throws Exception {
        try {
            MetaManager.shared().update((String) config.get("meta.source.tableforAPI"), "ID", data, true);

            Map<String, Object> result = new HashMap<>();
            result.put("result", "Ok");
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
