package org.xonix.zlo.search.test.storage;

import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.DAO;
import org.xonix.zlo.search.IndexingSource;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 15:19:58
 */
public class ZloStorage implements Serializable, IndexingSource {
    public static final String STORAGE_DIR = Config.getProp("test.storage");
    public static final int FROM = Integer.parseInt(Config.getProp("test.storage.from"));
    public static final int TO = Integer.parseInt(Config.getProp("test.storage.to"));
    public static final String SERIALIZE_FILE_NAME = "serialized.bin";

    private Map<Integer, ZloMessage> storedMsgs = new HashMap<Integer, ZloMessage>(TO - FROM);
    private File serialized = new File(STORAGE_DIR + "/" + SERIALIZE_FILE_NAME);

    private class MsgsHolder implements Serializable {
        public Map<Integer, ZloMessage> getMsgs() {
            return storedMsgs;
        }
    }

    public ZloStorage() {
        this(false);
    }

    public ZloStorage(boolean recreate) {
        File dir = new File(STORAGE_DIR);

        if (!serialized.exists() || recreate) {
            if (!dir.exists()) {
                System.out.println("Making storage dir...");
                if (!dir.mkdirs())
                    throw new RuntimeException("Can't create " + STORAGE_DIR);
            }
            // recreate - need to del old:
            if (serialized.exists()) {
                System.out.println("Deleting serialized file...");
                if(!serialized.delete())
                    throw new RuntimeException("Can't delete " + serialized.getAbsolutePath());
            }

            retrieveAndSerialize();
        }
        if (!recreate)
            loadData();
    }

    private void retrieveAndSerialize() {
        System.out.println("Retrieving...");
        try {
            for (ZloMessage m : DAO.Site.SOURCE.getMessages(FROM, TO)) {
                if (m != null)
                    storedMsgs.put(m.getNum(), m);
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serialized));
            oos.writeObject(new MsgsHolder());
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
            storedMsgs = ((MsgsHolder) ois.readObject()).getMsgs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ZloMessage getMessageByNumber(int num) throws DAO.Exception {
        for (int key : storedMsgs.keySet()) {
            if (key == num)
                return storedMsgs.get(key);
        }
        return null;
    }

    public List<ZloMessage> getMessages(int from, int to) throws DAO.Exception {
        List<ZloMessage> msgs = new ArrayList<ZloMessage>(to - from);
        for (int key : storedMsgs.keySet()) {
            if (key >= from && key <= to) {
                msgs.add(storedMsgs.get(key));
            }
        }
        return msgs;
    }
}
