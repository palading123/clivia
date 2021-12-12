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
package org.palading.clivia.support.common.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author palading_cr
 * @title MacheineUtil
 * @project clivia
 */
public class MacheineUtil {

    private static String processNo;
    private static String IP;
    private static String hostName;

    static {
        processNo = getProcessNo();
    }

    private MacheineUtil() {
        // Non
    }

    public static String getProcessNo() {
        if (StringUtils.isEmpty(processNo)) {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            processNo = name.split("@")[0];
        }
        return processNo;
    }

    private static InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            hostName = "unknown host!";
        }
        return null;

    }

    public static String getHostIp() {
        if (StringUtils.isEmpty(IP)) {
            InetAddress netAddress = getInetAddress();
            if (null == netAddress) {
                IP = "N/A";
            } else {
                IP = netAddress.getHostAddress(); // get the ip address
            }
        }
        return IP;
    }

    public static String getHostName() {
        if (StringUtils.isEmpty(hostName)) {
            InetAddress netAddress = getInetAddress();
            if (null == netAddress) {
                hostName = "N/A";
            } else {
                hostName = netAddress.getHostName(); // get the host address
            }
        }
        return hostName;
    }

    public static String getHostDesc() {
        return getHostName() + "/" + getHostIp();
    }

}
