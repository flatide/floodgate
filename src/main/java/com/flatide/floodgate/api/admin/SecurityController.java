package com.flatide.floodgate.api.admin;

import com.flatide.floodgate.Security;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path="/admin")
public class SecurityController {
    @GetMapping(path="/security")
    public @ResponseBody String get(
        @RequestParam(required = false) String password,
        @RequestParam(required = false, defaultValue = "true") String plain
    ) throws Exception {
        String result = "";
        try {
            if( plain.equals("true") ) {
                result = Security.getInstance().encryptAES256(password);
                result = "ENC:" + result;
            } else {
                result = Security.getInstance().decryptAES256(password.substring(4));
            }
            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
