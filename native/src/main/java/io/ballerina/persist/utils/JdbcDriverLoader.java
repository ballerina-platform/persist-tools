package io.ballerina.persist.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

    private final PrintStream errStream = System.err;
    public JdbcDriverLoader(URL[] urls, Path driverPath) throws MalformedURLException {
        super(urls);
        List<Path> pathList = listFiles(driverPath);
        for (Path path : pathList) {
            try {
                addURL(new File(path.toString()).toURI().toURL());
            } catch (MalformedURLException e) {
                throw e;
            }
        }
    }

    private List<Path> listFiles(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk != null ? walk.filter(Files::isRegularFile).collect(Collectors.toList()) : new ArrayList<>();
        } catch (IOException e) {
            errStream.println(e.getMessage());
        }
        return new ArrayList<>();
    }
}


