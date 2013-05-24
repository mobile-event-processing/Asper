/*
 * *************************************************************************************
 *  Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 *  http://esper.codehaus.org                                                          *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {

    public static String findClasspathFile(String filename) {
        URL url = FileUtil.class.getClassLoader().getResource(filename);
        if (url != null) {
            return url.getFile();
        }
        return null;
    }

    public static void findDeleteClasspathFile(String filename) {
        URL url = FileUtil.class.getClassLoader().getResource(filename);
        if (url != null) {
            File file = new File(url.getFile());
            file.delete();
        }
    }

    public static String[] readClasspathTextFile(String filename) {
        String filenameCp = findClasspathFile(filename);
        if (filenameCp == null) {
            throw new RuntimeException("Failed to find file '" + filename + "' in classpath");
        }
        List<String> lines = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(filenameCp);
            Scanner scanner = new Scanner(fis);
            try {
              while (scanner.hasNextLine()){
                lines.add(scanner.nextLine());
              }
            }
            finally{
                scanner.close();
                fis.close();
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to read file '" + filename + "': " + ex.getMessage(), ex);
        }
        return lines.toArray(new String[lines.size()]);
    }
}
