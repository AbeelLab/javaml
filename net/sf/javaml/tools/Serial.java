/**
 * %SVN.HEADER%
 */
package net.sf.javaml.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class with utility methods for serialization.
 * 
 * @author Thomas Abeel
 * 
 */
public class Serial {

    public static boolean exists(String fileName) {
        return (new File(fileName)).exists();
    }

    public static boolean store(Object p, String fileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fileName)));
            out.writeObject(p);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object load(File fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(fileName)));
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object load(String fileName) {
        return load(new File(fileName));
    }
}
