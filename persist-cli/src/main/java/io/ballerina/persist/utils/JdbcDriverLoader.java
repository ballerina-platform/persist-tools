/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.persist.utils;

import io.ballerina.persist.objects.BalException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to implement jdbc driver loader.
 *
 * @since 0.1.0
 */
public class JdbcDriverLoader extends URLClassLoader {

    public JdbcDriverLoader(URL[] urls, Path driverPath) throws MalformedURLException, BalException {
        super(urls);
        List<Path> pathList = listFiles(driverPath);
        for (Path path : pathList) {
            addURL(new File(path.toString()).toURI().toURL());
        }
    }
    private List<Path> listFiles(Path path) throws BalException {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk != null ? walk.filter(Files::isRegularFile).collect(Collectors.toList()) : new ArrayList<>();
        } catch (IOException e) {
            throw new BalException("Error occurred while loading JDBC driver : " + e.getMessage());
        }
    }
}


