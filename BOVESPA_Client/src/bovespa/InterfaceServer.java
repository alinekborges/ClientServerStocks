/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author alineborges
 */
public interface InterfaceServer extends Remote {
    
    public void subscribe(String stockName, int clientID, InterfaceClient client) throws RemoteException;
    public void buyOrder(String stockName, int quantity, Double price, int clientID, InterfaceClient client) throws RemoteException;
    public void sellOrder(String stockName, int quantity, Double price, int clientID, InterfaceClient client) throws RemoteException;
}
