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

import org.palading.clivia.config.api.CliviaBlacklistConfigService;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author palading_cr
 * @title CliviaBlackListController
 * @project clivia
 */
@RestController
@RequestMapping("/admin/server")
public class CliviaBlackListController {

    // public CliviaResponse getBlackList() {
    //
    // }

    @Autowired
    private CliviaBlacklistConfigService cliviaBlacklistService;

    @RequestMapping(value = "/blacklist/getBlacklist")
    public CliviaResponse getBlackList(@RequestParam("token") String token, @RequestParam("apiBlacklistVos") String json) {
        return cliviaBlacklistService.getBlacklistCache(token, json);
    }

    @RequestMapping(value = "/blacklist/getBlacklistAll", method = RequestMethod.POST)
    public CliviaResponse getBlackListAll(@RequestParam("token") String token) {
        return cliviaBlacklistService.getAllBlacklistCache(token);
    }
}
