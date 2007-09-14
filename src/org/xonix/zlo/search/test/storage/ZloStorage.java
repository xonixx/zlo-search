package org.xonix.zlo.search.test.storage;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.DAO;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 15:19:58
 */
public class ZloStorage implements Serializable {
    public static final String STORAGE_DIR = Config.getProp("test.storage");
    public static final int FROM = Integer.parseInt(Config.getProp("test.storage.from"));
    public static final int TO = Integer.parseInt(Config.getProp("test.storage.to"));
    public static final String SERIALIZE_FILE_NAME = "serialized.bin";

    private List<ZloMessage> storedMsgs = new ArrayList<ZloMessage>();
    private File serialized = new File(STORAGE_DIR + "/" + SERIALIZE_FILE_NAME);;

    public ZloStorage() {
        this(false);
    }

    public ZloStorage(boolean recreate) {
        File dir = new File(STORAGE_DIR);

        if (!dir.exists() || dir.isDirectory() && dir.list().length == 0 || recreate) {
            if (!dir.exists())
                System.out.println("Making storage dir...");
                if (!dir.mkdirs())
                    throw new RuntimeException("Can't create " + STORAGE_DIR);

            if (serialized.exists())
                System.out.println("Deleting serialized file...");
                if(!serialized.delete())
                    throw new RuntimeException("Can't delete " + serialized.getAbsolutePath());

            retrieveAndSerialize();
        }
        if (!recreate)
            loadData();
    }

    private void retrieveAndSerialize() {
        try {
            storedMsgs = DAO.Site.SOURCE.getMessages(FROM, TO);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serialized));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DAO.Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serialized));
            ZloStorage storage = (ZloStorage) ois.readObject();
            storedMsgs = storage.getStoredMsgs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public List<ZloMessage> getStoredMsgs() {
        return storedMsgs;
    }
}
