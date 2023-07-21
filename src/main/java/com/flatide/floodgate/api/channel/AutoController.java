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
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path="/api")
public class AutoController extends ApiBasicController {
    @GetMapping(path="/{api}/{target}")
    public @ResponseBody Map get(
            @PathVariable String api,
            @PathVariable Map<String, String> paths,
            @RequestParam Map<String, String> params) throws Exception {
        ChannelAgent agent = new ChannelAgent();
        super.init(agent);

        agent.addContext(Context.CONTEXT_KEY.REQUEST_PATH_VARIABLES, paths);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);

        return agent.process(null, "/" + api);
    }

    @PostMapping(path="/{api}/{target}")
    public @ResponseBody Map postFlow(
            @RequestBody Map<String, Object> data,
            @PathVariable String api,
            @PathVariable Map<String, String> paths,
            @RequestParam Map<String, String> params) throws Exception {
        ChannelAgent agent = new ChannelAgent();
        super.init(agent);

        FGInputStream current = new FGSharableInputStream(new JSONContainer(data, "HEADER", "ITEMS"));

        agent.addContext(Context.CONTEXT_KEY.REQUEST_PATH_VARIABLES, paths);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_BODY, data);

        return agent.process(current, "/" + api);
    }
}
