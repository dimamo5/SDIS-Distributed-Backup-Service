package database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by diogo on 12/03/2016.
 */
public class File {
    private String filename;
    private String filehash;
    private int numChuncks;
    private static final int MAX_NO_CHUNKS = 999999;  // PODE TER ATÉ 1.000.000 CHUNKS

    public File(String filename,String filehash, int numchuncks) {
        this.filehash=filehash;
        this.filename = filename;
        this.numChuncks=numchuncks;
    }

    //=============METHODS===========

    public String getFilename() {
        return filename;
    }

    public String getFilehash() {
        return filehash;
    }


    public int getNumChuncks() {
        return numChuncks;
    }

}
