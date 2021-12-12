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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * zip tool
 * 
 * @author palading_cr
 * @title StrZipUtil
 * @project clivia
 */
public class StrZipUtil {

    /**
     * zip
     *
     * @author palading_cr
     */
    public static String zip(String data) throws IOException {
        if (StringUtils.isNotEmpty(data)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(data.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        }
        return null;
    }

    /**
     * unzip
     *
     * @author palading_cr
     */
    public static String unzip(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        GZIPInputStream gzipStream = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int b = 0;
        while ((b = gzipStream.read(buffer)) >= 0) {
            out.write(buffer, 0, b);
        }
        return out.toString("UTF-8");
    }
}
