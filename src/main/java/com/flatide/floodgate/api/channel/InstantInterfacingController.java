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

package com.flatide.floodgate.api.channel;

import java.util.*;

import com.flatide.floodgate.agent.ChannelAgent;
import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.flow.stream.FGSharableInputStream;
import com.flatide.floodgate.agent.flow.stream.carrier.container.JSONContainer;

import com.flatide.floodgate.system.FlowGenerator;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*" )
@RestController
@RequestMapping(path="/channel")
public class InstantInterfacingController extends ApiBasicController {
    @GetMapping(path="/instant/{target}")
    public @ResponseBody Map get(
        @PathVariable String api,
        @PathVariable Map<String, String> paths,
        @RequestParam Map<String, String> params) throws Exception {
        super.init();
        ChannelAgent agent = getAgent();

        agent.addContext(Context.CONTEXT_KEY.REQUEST_PATH_VARIABLES, paths);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);

        return agent.process(null, "/" + api);
    }

    @PostMapping(path="/instant/{api1}/{target}")
    public @ResponseBody Map postFlow1(
        @RequestBody Map<String, Object> body,
        @RequestParam String src_trg,
        @PathVariable String api1,
        @PathVariable Map<String, String> paths,
        @RequestParam Map<String, String> params) throws Exception {
        super.init();

        return postProcess(body, src_trg, api1, params, paths);
   }

    @PostMapping(path="/instant/{api1}/{api2}/{target}")
    public @ResponseBody Map postFlow2(
        @RequestBody Map<String, Object> body,
        @RequestParam String src_trg,
        @PathVariable String api1,
        @PathVariable String api2,
        @PathVariable Map<String, String> paths,
        @RequestParam Map<String, String> params) throws Exception {
        super.init();

        return postProcess(body, src_trg, api1 + "/" + api2, params, paths);
   }


    @PostMapping(path="/instant/{api1}/{api2}/{api3}/{target}")
    public @ResponseBody Map postFlow3(
        @RequestBody Map<String, Object> body,
        @RequestParam String src_trg,
        @PathVariable String api1,
        @PathVariable String api2,
        @PathVariable String api3,
        @PathVariable Map<String, String> paths,
        @RequestParam Map<String, String> params) throws Exception {
        super.init();

        return postProcess(body, src_trg, api1 + "/" + api2 + "/" + api3, params, paths);
   }

    private Map postProcess(Map body, String src_trg, String api, Map params, Map paths) throws Exception {
        ChannelAgent agent = getAgent();

        FlowGenerator flowGenerator = new FlowGenerator();
        Map flow = flowGenerator.generate(body, src_trg);
        body.remove("MAPPING_INFO");
        body.remove("EXEC_INFO");

        FGInputStream current = new FGSharableInputStream(new JSONContainer(body, "HEADER", "ITEMS"));

        agent.addContext(Context.CONTEXT_KEY.REQUEST_PATH_VARIABLES, paths);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_BODY, body);

        return agent.process(current, "/" + api, flow);
    }
}



