/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alineborges
 */
public class Order {
    
    public enum Type {
        BUY, SELL
    }
    
    public enum Status {
        PENDIND, EXECUTED
    }
    
    public Type type;
    public String stock;
    public Double price;
    public int quantity;
    public Status status = Status.PENDIND;
    public int clientID;
    public InterfaceClient client;
    
    public void completeOrder(Double price)  {
        
        try {
            if (type == Type.SELL) {
                client.sellOrderCompleted(stock, quantity, price);
            } else {
                client.buyOrderCompleted(stock, quantity, price);
            }
            
            this.status = Status.EXECUTED;
            
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
