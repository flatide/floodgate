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

import com.flatide.floodgate.ConfigBase;
import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.Floodgate;
import com.flatide.floodgate.agent.handler.FloodgateHandlerManager;
import com.flatide.floodgate.system.security.FloodgateSecurity;
import com.flatide.floodgate.system.DBLogHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class FloodgateApplication {
    @Autowired
    private ConfigBase config;

    @PostConstruct
    public void init() {
        try {
            ConfigurationManager.shared().setConfig(config);

            String db_url = ConfigurationManager.shared().getString("datasource.security.url");
            String db_userid = ConfigurationManager.shared().getString("datasource.security.user");
            String db_passwd = ConfigurationManager.shared().getString("datasource.security.password");
            String key = ConfigurationManager.shared().getString("security.key");
            String key_alias = ConfigurationManager.shared().getString("security.alias");

            //SecurityProvider provider = new SecurityProvider();
            //FloodgateSecurity.shared().setSecurityProvider(provider);

            Floodgate.init();

            DBLogHandler handler = new DBLogHandler();
            FloodgateHandlerManager.shared().addHandler("DB Log Handler", handler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(FloodgateApplication.class, args);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
