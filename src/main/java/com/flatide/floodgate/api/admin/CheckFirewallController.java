package com.flatide.foodgate.api.admin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flatide.floodgate.CAEnv;

@CrossOrigin(origins = "*" )
@RestController
@RequestMapping(path="/admin")
public class CheckFirewallController {

    @GetMapping(path="/checkfirewall")
    public List<String> get(
        @RequestParam(required = false, value = "address", defaultValue = "") String address
        ) throws Exception {
        List<String> responseList = new ArrayList<>();

        String myIp = CAEnv.getInstance().getAddress();
        List<String> addressList = new ArrayList<>();

        if( address.isEmpty() == false ) {
            String[] addr = address.split(",");
            for( String ad : addr ) {
                addressList.add(ad);
            }
        }

        for (String url : addressList) {
            try {
                checkFirewall(url);
                responseList.add(url + " : Ok");
            } catch (Exception e) {
                responseList.add( url + " : Fail" );
            }
        }

        return responseList;
    }

    public boolean checkFirewall(String url) throws Exception {
        Socket socket = null;
        ArrayList<String> ip = new ArrayList<>();
        ArrayList<String> port = new ArrayList<>();
        String curIP = "";
        String curPort = "";
        try {
            if( url.contains("HOST") && url.contains("PORT") ) {    // Oracle tnsnames
                Pattern pattern = Pattern.compile("HOST\\s*=\\s*[\\w-]+\\.[\\w-]+\\.[\\w-]+\\.[\\w-]+");
                Matcher matcher = pattern.matcher(url);
                while( matcher.find() ) {
                    String f = matcher.group();
                    String[] temp = f.split("=");
                    ip.add( temp[1].trim() );
                }

                if( ip.size() == 0 ) {
                    throw new Exception("Cannot find proper ADDRESS from " + url);
                }

                pattern = Pattern.compile("PORT\\s*=\\s*[0-9]+");
                matcher = pattern.matcher(url);
                while( matcher.find() ) {
                    String f = matcher.group();
                    String[] temp = f.split("=");
                    port.add( temp[1].trim() );
                }
                if( port.size() != ip.size() ) {
                    throw new Exception("PORT not properly coupled with ADDRESS in " + url);
                }
            } else {
                Pattern pattern = Pattern.compile("[\\w-]+\\.[\\w-]+\\.[\\w-]+\\.[\\w-]+:[0-9]+");

                Matcher matcher = pattern.matcher(url);
                while( matcher.find() ) {
                    String f = matcher.group();
                    String[] temp = f.split(":");
                    ip.add( temp[0]);
                    port.add( temp[1]);
                }
                if( ip.size() == 0 ) {
                    throw new Exception("Cannot find proper IP:PORT from " + url);
                }
            }

            for( int i = 0; i < ip.size(); i++ ) {
                curIP = ip.get(i);
                curPort = port.get(i);

                socket = new Socket();
                socket.connect(new InetSocketAddress(curIP, Integer.parseInt(curPort)), 500);
                socket.close();
                socket = null;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception( curIP + ":" + curPort + " : " + e.getMessage());
        } finally {
            try {
                if( socket != null ) socket.close();
            } catch(IOException e) {
            }
        }
    }

}



