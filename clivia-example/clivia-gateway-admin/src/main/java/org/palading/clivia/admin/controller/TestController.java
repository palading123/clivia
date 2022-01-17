/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.admin.controller;

import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.DesUtil;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

/**
 * @author palading_cr
 * @title TestController
 * @project clivia
 */
@RestController
@RequestMapping("/admin/server")
public class TestController {

    public static void main(String[] args) {
        House house = new House("武汉", "光谷未来城");
        Person person = new Person();
        person.setName("小陈");
        person.setValue("25岁");
        person.setHouse(house);
        String s = JsonUtil.toJson(person);
        System.out.println(s);
        System.out.println(DesUtil.encrypt(s, "23423sczxc"));;
    }

    public CliviaResponse test(HttpServletRequest request) {
        // List<Person> list = new ArrayList<>();
        // Person person = new Person();
        // person.setName("你好");
        // Map<String, Object> map = new HashMap<>();
        // map.put("key1", "key2");
        // map.put("key2", " key3");
        // person.setMap(map);
        // list.add(person);
        CliviaResponse cliviaResponse = new CliviaResponse();
        cliviaResponse.setResCode(200);
        cliviaResponse.setResMsg("success");
        cliviaResponse.setResData("nihao");
        // Enumeration<String> headerNames = request.getHeaderNames();
        // while (headerNames.hasMoreElements()) {
        // String name = headerNames.nextElement();
        // // 根据名称获取请求头的值
        // String value = request.getHeader(name);
        // System.out.println(name + "---" + value);
        // }
        return cliviaResponse;
    }

    // @RequestMapping(value = "/test", method = RequestMethod.POST)
    public CliviaResponse testJson(HttpServletRequest request) {
        // List<Person> list = new ArrayList<>();
        // Person person = new Person();
        // person.setName("你好");
        // Map<String, Object> map = new HashMap<>();
        // map.put("key1", "key2");
        // map.put("key2", " key3");
        // person.setMap(map);
        // list.add(person);
        CliviaResponse cliviaResponse = new CliviaResponse();
        cliviaResponse.setResCode(200);
        cliviaResponse.setResMsg("success");
        cliviaResponse.setResData("nihao");
        // Enumeration<String> headerNames = request.getHeaderNames();
        // while (headerNames.hasMoreElements()) {
        // String name = headerNames.nextElement();
        // // 根据名称获取请求头的值
        // String value = request.getHeader(name);
        // System.out.println(name + "---" + value);
        // }
        return cliviaResponse;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public CliviaResponse buildCliviaResponse(@RequestBody Person person) {
        CliviaResponse cliviaResponse = CliviaResponse.success(person);
        return cliviaResponse;
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String get(@RequestParam(value = "test") String test) {

        return test;
    }

    @RequestMapping(value = "/postForm", method = RequestMethod.POST)
    public Map get(@RequestParam Map<String,Object> param) {
        return param;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public CliviaResponse post(@RequestBody Person person) {
        return CliviaResponse.success(person);
    }

    static class Person implements Serializable {
        private String name;
        private String value;
        private House house;

        private String a;
        private String c;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public House getHouse() {
            return house;
        }

        public void setHouse(House house) {
            this.house = house;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    static class House implements Serializable {
        private String houseAddr;
        private String houseName;

        public House(String houseAddr, String houseName) {
            this.houseAddr = houseAddr;
            this.houseName = houseName;
        }

        public House() {}

        public String getHouseAddr() {
            return houseAddr;
        }

        public void setHouseAddr(String houseAddr) {
            this.houseAddr = houseAddr;
        }

        public String getHouseName() {
            return houseName;
        }

        public void setHouseName(String houseName) {
            this.houseName = houseName;
        }
    }
}
