package info.xonix.zlo.search.rmi;

import info.xonix.zlo.search.config.Config;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Author: Vovan
 * Date: 21.04.2008
 * Time: 21:41:36
 */
public class Server extends UnicastRemoteObject implements ServerInterface {
    public Server() throws RemoteException {
        super();
    }

    public void main() {
        try{
            System.setProperty("java.rmi.server.ignoreStubClasses", "true");
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(Config.getProp("rmi.port")));

            registry.rebind("SearchServer", this);

            synchronized (this) {
              try {
                // prevent main thread from exiting...
                this.wait();
              } catch (InterruptedException e) {;}
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        new Server().main();
    }
}
