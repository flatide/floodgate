package com.flatide.floodgate.system;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import javax.servlet.annotation.MultipartConfig;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path="/admin") // This means URL's start with /demo (after Application path)
public class FileUploader {
    @PostMapping(path = "/file")
    public @ResponseBody
    Map push(@RequestParam MultipartFile file ) throws Exception {

        String filename = file.getOriginalFilename();

        byte[] data = file.getBytes();

        Map<String, Object> result = new HashMap<>();
        result.put("filename", filename);
        result.put("length", data.length);

        System.out.println("filename:" + filename);
        System.out.println("length:" + data.length);
        return result;
    }
}