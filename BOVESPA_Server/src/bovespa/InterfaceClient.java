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
public interface InterfaceClient extends Remote {
    
    public void notify(String stockName, Double price) throws RemoteException;
    public void buyOrderCompleted(String stockName, int quantity, Double price) throws RemoteException;
    public void sellOrderCompleted(String stockName, int quantity, Double price) throws RemoteException;
    
}

