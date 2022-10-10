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

import com.flatide.floodgate.agent.ChannelAgent;
import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.flow.stream.FGSharableInputCurrent;
import com.flatide.floodgate.agent.flow.stream.carrier.pipe.BytePipe;
import com.flatide.floodgate.agent.flow.stream.carrier.pipe.JSONPipe;
import com.flatide.floodgate.agent.flow.stream.carrier.pipe.ListPipe;
import com.flatide.floodgate.agent.flow.stream.carrier.container.JSONContainer;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins =  "*")
@RestController
@RequestMapping(path="/test")
public class TestController {
    @PostMapping(path="/{guest}/{api}/{resource}")
    public @ResponseBody Map postFlow(@RequestBody Map<String, Object> data, @PathVariable String guest, @PathVariable String api, @PathVariable String resource, @RequestParam Map<String, String> params) throws Exception {
        FGInputStream stream = new FGSharableInputCurrent( new JSONContainer(data, "HEADER", "ITEMS"));

        ChannelAgent agent = new ChannelAgent();
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_BODY, data);
        return agent.process(stream, resource);
    }

    @GetMapping(path="/{guest}/{api}/{resource}")
    public @ResponseBody Map getFlow(@RequestBody Map<String, Object> data, @PathVariable String guest, @PathVariable String api, @PathVariable String resource, @RequestParam Map<String, String> params) throws Exception {
        FGInputStream stream = new FGSharableInputCurrent( new JSONContainer(data, "HEADER", "ITEMS"));

        ChannelAgent agent = new ChannelAgent();
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);
        agent.addContext(Context.CONTEXT_KEY.REQUEST_BODY, data);
        return agent.process(stream, resource);
    }

    /*@GetMapping(path="/remote")
    public @ResponseBody List<Map> get() throws Exception {
        GhostTable table = Ghost.shared().getTable("HISTORY");
        //List item = new ArrayList<String>();
        Map<String, Object> item = new HashMap<>();

        BasicConnector db = new DBConnector();

        List<Map> result;
        try {
            db.connect(new HashMap(), new HashMap());

            Map<String, String> rule = new LinkedHashMap<>();
            //      source  target
            rule.put("NAME", "CODE");
            rule.put("CREATE_DATE", "createData");
            rule.put("IF_SEQ", "SQ");

            result = db.read(rule);

            item.put("CODE", new Date().toString());
            item.put("createData", "OK");
            item.put("SQ", result.toString());

            System.out.println(result.toString());
        } catch( Exception e) {
            item.put("CODE", new Date().toString());
            item.put("createData", "ERROR");
            item.put("SQ", e.toString());

            e.printStackTrace();
            return null;
        } finally {
            table.add("test", item);
            table.store();

            db.close();
        }

        return result;
    }*/


    @PostMapping(path="/insertjson")
    public @ResponseBody Map insertjson(@RequestBody Map data, @RequestParam String id) throws Exception {
        FGInputStream stream = new FGSharableInputCurrent( new ListPipe( (Map) data.get("HEADER"), (List) data.get("ITEMS") ));

        ChannelAgent agent = new ChannelAgent();
        return agent.process(stream, id);
    }

    @PostMapping(path="/stream/{id}")
    public @ResponseBody Map stream(@RequestBody String data, @PathVariable String id, @RequestParam Map params) throws Exception {
        BufferedInputStream inputStream = new BufferedInputStream( new ByteArrayInputStream(data.getBytes()));
        FGInputStream stream = new FGSharableInputCurrent( new JSONPipe(inputStream, data.length(), "", "ITEMS", 1 ));

        ChannelAgent agent = new ChannelAgent();
        agent.addContext(Context.CONTEXT_KEY.REQUEST_PARAMS, params);
        return agent.process(stream, id);
    }

    @PostMapping(path="/upload")
    public @ResponseBody Map upload(@RequestBody String data, @RequestParam String id) throws Exception {
        //Current stream = new InputCurrent( new ListStream((Map) data.get("HEADER"), (List) data.get("ITEMS") ));
        BufferedInputStream inputStream = new BufferedInputStream( new ByteArrayInputStream(data.getBytes()));
        FGInputStream stream = new FGSharableInputCurrent( new BytePipe(inputStream, data.length(), 100 ));

        ChannelAgent agent = new ChannelAgent();
        return agent.process(stream, id);
    }

    @GetMapping(path="/test")
    public @ResponseBody Map test(@RequestParam( defaultValue = "0") int delay) throws Exception {
        System.out.println("Start");
        System.out.println("chnnel/test");
        Thread.sleep(delay);
        System.out.println("Return");
        return new HashMap();
    }

    @PostMapping(path="/testpost")
    public @ResponseBody Map testpost(@RequestBody String data, @RequestParam( defaultValue = "0") int delay) throws Exception {
        System.out.println("chnnel/testpost");
        doHttp("http://localhost:8080/channel/remote", delay);
        Map<String, Object> result = new HashMap<>();
        result.put("result", data);
        return result;
    }

    private String doHttp(String url, int delay) throws Exception {
        url += "?delay=" + delay;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int code = connection.getResponseCode();
        if( code < 200 || code > 299 ) {
            System.out.println("Status " + code);
        }

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream) );

        StringBuilder builder = new StringBuilder();
        while( true ) {
            String line = reader.readLine();
            if( line == null ) break;
            builder.append(line);
        }

        return builder.toString();
    }



    public String encrypt(String strToEncrypt, String secret)
    {
        String key = "boooooooooom!!!!";
        String salt = "ssshhhhhhhhhhh!!!!";
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt, String secret) {
        String key = "boooooooooom!!!!";
        String salt = "ssshhhhhhhhhhh!!!!";

        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }


}