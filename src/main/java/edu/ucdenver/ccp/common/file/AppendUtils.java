package edu.ucdenver.ccp.common.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;


import org.apache.commons.io.IOUtils;

/**
 * Liberated from a StackOverflow.com post:
 * http://stackoverflow.com/questions/2966333/append-data-into-a-file-using-apache-commons-i-o
 * 
 * @author "Ryan"
 *
 */
public final class AppendUtils {

    public static void appendToFile(final InputStream in, final File f) throws IOException {
        OutputStream stream = null;
        try {
            stream = outStream(f);
            IOUtils.copy(in, stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static void appendToFile(final String in, final File f) throws IOException {
        InputStream stream = null;
        try {
            stream = IOUtils.toInputStream(in);
            appendToFile(stream, f);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private static OutputStream outStream(final File f) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(f, true));
    }

    private AppendUtils() {}

}
