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

package com.flatide.floodgate;

import org.springframework.web.bind.annotation.*;

import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.Flow;
import com.flatide.floodgate.agent.flow.FlowMockup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashmap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/admin")
public class QueryContoller {
    @PostMapping(path = "/query")
    public @ResponseBody Map query(
        @RequestBody Map body,
        @RequestParam String src_trg) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();

        Map execInfo = (Map) body.get("EXEC_INFO");

        String db_type = (String) execInfo.get("DB_TYPE");
        String table = (String) execInfo.get("TABLE");

        if (db_type == null || db_type.isEmpty()) {
            result.put("success", false);
            result.put("error", "DB_TYPE is required.");
            return result;
        }

        if (table == null || table.isEmpty()) {
            result.put("success", false);
            result.put("error", "TABLE is required.");
            return result;
        }

        List<Map> mappingInfoList = (List) body.get("MAPPING_INFO");

        if ("SRC".equals(src_trg)) {
            String query = "";

            Set<String> sourceSet = new LinkedHashSet<>();
            try {
                for (Map row : mappingInfoList) {
                    String targetColumn = (String) row.get("COL_ID");
                    String sourceColukn = (String) row.get("MAP_SRC_COL_ID");
                    String logic = (String) row.get("MAP_SRC_COL_LOGIC");

                    if (targetColumn == null || targetColumn.isEmpty()) {
                        continue;
                    }

                    if (sourceColumn == null || sourceColumn.isEmpty()) {
                        if (logi null || logic.isEmpty()) {
                            sourceColumn = targetColumn;
                        }
                    }

                    if (sourceColumn !=  null && !sourceColumn.isEmpty()) {
                        sourceSet.add(sourceColumn);
                    }

                    if (logic != null && !logic.isEmpty()) {
                        Pattern pattern = Pattern.compile("\\$.+?\\$");
                        Matcher matcher = pattern.matcher(logic);

                        while (matcher.find()) {
                            String col = logic.substring(matcher.start() + 1, matcher.end() - 1);
                            sourceSet.add(col);
                        }
                    }
                }

                int i = 0;
                for (String col : sourceSet) {
                    if (i>0) {
                        query += ", ";
                    }
                    query += col;
                    i++;
                }

                query = "SELECT " + query + " FROM " + table;

                String conditino = (String) execInfo.get("EXTRACT_CONDITINO");
                if (condition != null && !condition.isEmpty()) {
                    query += " WHERE " + condition;
                }
                result.put("success", true);
                result.put("QUERY", query);
            } catch (Exception e) {
                result.put("success", false);
                result.put("reason", e.getClass().getName() + " : " + e.getMessage());
            }
            return result;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, Object> module = new HashMap<>();
        Map<String, Object> insert = new HashMap<>();
        Map<String, Object> connect = new HashMap<>();

        data.put("ENTRY", "INSERT");

        insert.put("ACTION", "CREATE");

        connect.put("CONNECTOR", "JDBC");
        connect.put("DBTYPE", db_type);
        insert.put("CONNECDT", connect);

        insert.put("TEMPLATE", "QUERY_GENERATE");
        insert.put("TARGET", table);
        insert.put("RULE", "MAPPING");

        module.put("INSERT", insert);

        data.put("MODULE", module);

        Map<String, Object> rule = new HashMap<>();

        Map<String, Object> mapping = new LinkedHashMap<>();

        try {
            // Item needed to process template
            Map<String, Object> item = new LInkedHashMap<>();
            for (Map row : mappingInfoList) {
                String targetColumn = (String) row.get("COL_ID");
                String sourceColumn = (String) row.get("MAP_SRC_COL_ID");
                String logic = (String) row.get("MAP_SRC_COL_LOGIC");

                if (targetColumn == null || targetColumn.isEmpty()) {
                    continue;
                }

                if (logic != null && !logic.isEmpty()) {
                    if (sourceColumn == null || sourceColumn.isEmtpy()) {
                    } else {
                        item.put(sourceColumn, "");
                    }

                    Pattern pattern = Pattern.compile("\\$.+?\\$");
                    Matcher matcher = pattern.matcher(logic);

                    while (matcher.find()) {
                        String col = logic.substring(matcher.start() + 1, matcher.end() - 1);
                        item.put(col, "");
                    }

                    // sysdate, 'API', >IF_DATE, {REFERENCE}
                    char first = logic.charAt(0);
                    switch (first) {
                        case '{':
                            break;
                        case '>':
                            break;
                        case '#':
                            break;
                        default:
                            logic = "+" + logic; // sysdate, 'API'
                            break;
                    }
                    sourceColumn = logic;
                } else {
                    if (sourceColumn == null || sourceColumn.isEmpty()) {
                        sourceColumn = targetColumn;
                    }
                    item.put(sourceColumn, "");
                }

                mapping.put(targetColumn, sourceColumn);
            }
            rule.put("MAPPING", mapping);
            data.put("RULE", rule);

            long cur = System.currentTimeMillis();
            Timestamp current = new Timestamp(cur);

            data.put("CREATE_DATE", current);
            data.put("MODIFY_DATE", current);

            Context context = new Context();

            List<Map<String, Object>> itemList = new ArrayList<>();
            itemList.add(item);

            context.add("ITEM", itemList);
            Flow flow = new FlowMockup("QUERY GENERATE", data, context, null);
            flow.process();

            String query = context.getString("QUERY");

            result.put("success", true);
            result.put("QUERY", query);
        } catch (Exception e) {
            result.put("success", false);
            result.put("reason", e.getClass().getName() + " : " + e.getMessage());
        }

        return result;
    }
}
