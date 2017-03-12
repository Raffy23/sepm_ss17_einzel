package sepm.ss17.e1526280.dao.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is able to Store Images in a Directory as Blob Storage for the H2 Database
 *
 * @author Raphael Ludwig
 * @version 08.03.17
 */
public class ImageDAO {

    /** Logger for logging ... duh **/
    private static final Logger LOG = LoggerFactory.getLogger(ImageDAO.class);

    /** The Path where the Data should be stored **/
    private final String targetPath;

    /**
     * @param targetPath a valid path where the Images should be stored
     */
    public ImageDAO(String targetPath) {
        this.targetPath = targetPath;
    }

    /**
     * Stores a File in the Storage
     * @param path the path of the File which should be stored in the Storage
     * @return the File form the Storage
     */
    public File persistImage(String path) {
        LOG.debug("Persist Image: " + path);

        final Path source = Paths.get(path);
        final Path target = Paths.get(targetPath+"/"+generateNewBlobFileName(path));

        //Copy the Image to the Storage and set the Name so there are no problems with
        //overwriting another one
        try {
            Files.copy(source,target);
        } catch (IOException e) {
            LOG.error("Unable to store Image in Database: " + e.getMessage());
            throw new RuntimeException("Unable to store Image in Database!",e);
        }

        return target.toAbsolutePath().toFile();
    }

    /**
     * Deletes a Image from the Image Storage
     * @param name the name of the Image
     */
    public void deleteImage(String name) {
        LOG.debug("delete " + name);

        try {
            Files.deleteIfExists(Paths.get(targetPath+"/"+name));
        } catch (IOException e) {
            LOG.error("Unable to delete Image in Database: " + e.getMessage());
            throw new RuntimeException("Unable to delete stored Image!",e);
        }
    }

    /**
     * This Function does return the File Object of the Image in the Storage
     * @param name the name of the Image in the Storage
     * @return the file object of the Image in the Storage
     */
    public File getFilePath(String name) {
        return Paths.get(targetPath+"/"+name).toAbsolutePath().toFile();
    }

    /**
     * This Function does generate a unique name for every file
     * @param file the path of the file
     * @return a unique name of the file (a 64 Char String)
     */
    private static String generateNewBlobFileName(String file) {
        LOG.debug("Generate new Name for " + file);
        //We do generate a unique name by Hashing the Filename with SHA-256
        //Generate the stuff we need:
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 is not supported!");
        }

        try {
            // Hash filename so that there are no name collisions
            md.update((String.valueOf(System.currentTimeMillis())+file).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding (UTF-8) is not supported ... wtf?");
        }

        //Get the Hash and convert it to a String so wew can use it
        byte[] digest = md.digest();
        return String.format("%064x", new java.math.BigInteger(1, digest));
    }


}
