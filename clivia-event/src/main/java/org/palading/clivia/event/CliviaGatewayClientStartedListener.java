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
package org.palading.clivia.event;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * print a custom banner. Generally, after printing the banner, it indicates that each cache of the system has been
 * loaded successfully
 * 
 * @author palading_cr
 * @title CliviaGatewayClientBannerListener
 * @project clivia
 */
public class CliviaGatewayClientStartedListener implements CliviaListenerCallable {

    private static final Logger logger = LoggerFactory.getLogger(CliviaGatewayClientStartedListener.class);

    private static final String logo = "\n" +
            "   _____ _      _______      _______          \n" +
            "  / ____| |    |_   _\\ \\    / /_   _|   /\\    \n" +
            " | |    | |      | |  \\ \\  / /  | |    /  \\   \n" +
            " | |    | |      | |   \\ \\/ /   | |   / /\\ \\  \n" +
            " | |____| |____ _| |_   \\  /   _| |_ / ____ \\ \n" +
            "  \\_____|______|_____|   \\/   |_____/_/    \\_\\\n" +
            "                                              \n" +
            "                                              \n";
    private final AtomicBoolean bannerDisplay = new AtomicBoolean(true);

    private StringBuffer stringBuffer;

    /**
     * banner print
     * 
     * @author palading_cr
     *
     */
    @Override
    public Object invoke(Object o, Class aClass) throws Exception {
        if (bannerDisplay.compareAndSet(true, false)) {
            stringBuffer = new StringBuffer();
            stringBuffer.append("\n");
            stringBuffer.append(logo);
            stringBuffer.append("\n");
            stringBuffer.append(":: clivia ::    (v");
            stringBuffer.append(getVersion("0.0.1")).append(")");
            stringBuffer.append("\n");
            logger.info(stringBuffer.toString());
            return !bannerDisplay.get();
        }
        return bannerDisplay.get();
    }

    /**
     * gets the current system version number. If it cannot be obtained, the default value is displayed
     * 
     * @author palading_cr
     *
     */
    private String getVersion(String defaultVersion) {
        String version = determineCliviaVersion();
        return StringUtils.isEmpty(version) ? defaultVersion : version;
    }

    private String getCodeSourceLocationVersion(CodeSource codeSource) {
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection)connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private String determineCliviaVersion() {
        String implementationVersion = getClass().getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        return getCodeSourceLocationVersion(codeSource);
    }

    private String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }

    @Override
    public void invokeScheduler(Object o) throws Exception {

    }

    @Override
    public long getPeriod(long period) {
        return 0;
    }

    @Override
    public int getOrder() {
        return CliviaConstants.clivia_banner_listener_order;
    }

}
