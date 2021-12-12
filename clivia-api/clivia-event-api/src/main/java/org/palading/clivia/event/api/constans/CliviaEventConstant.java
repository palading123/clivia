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
package org.palading.clivia.event.api.constans;

/**
 * @author palading_cr
 * @title CliviaEventConstant
 * @project clivia
 */
public class CliviaEventConstant {

    // register listener order
    public static final int clivia_register_listener_order = 0;

    // cache listener order
    public static final int clivia_cache_listener_order = 1;

    // server listener order
    public static final int clivia_server_listener_order = 2;

    public static final int clivia_banner_listener_order = 3;

    // register listener invokeScheduler method default period
    public static final long clivia_register_default_period = 10;

    // cache listener invokeScheduler method default period
    public static final long clivia_cache_default_period = 30;

    // server listener invokeScheduler method default period
    public static final long clivia_server_default_period = 30;

}
