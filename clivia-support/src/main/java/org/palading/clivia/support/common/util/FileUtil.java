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

import java.io.*;

/**
 * @author palading_cr
 * @title FileUtil
 * @project clivia /17
 */
public class FileUtil {

    private static String default_key = "clivia";

    public static String readToString(String filePath, String encoding) {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            if (StringUtils.isEmpty(filePath)) {
                return default_key;
            }
            File file = new File(filePath);
            if (file.isDirectory()) {
                File file0 = file.listFiles()[0];
                if (null != file0 && file0.length() > 0) {

                    reader = new BufferedReader(new FileReader(file0));
                    String tempStr;
                    while ((tempStr = reader.readLine()) != null) {
                        sbf.append(tempStr);
                    }
                    reader.close();
                    return sbf.toString();

                    // Long filelength = file0.length();
                    //
                    // byte[] filecontent = new byte[filelength.intValue()];
                    // in = new FileInputStream(file);
                    // in.read(filecontent);
                    // in.close();
                    // return new String(filecontent, encoding);
                }
            }

        } catch (IOException e) {
        } finally {
            try {
                if (null != in) {
                    in.close();
                }

            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(readToString("D://document/gateway", "UTF-8"));
    }
}
