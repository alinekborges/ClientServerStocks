/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alinekborges
 */
public class Stock {

    public Map<String, InterfaceClient> subscribedClients;

    public String name;
    public Double price;
    private Double[] history;
    private int currentPosition = 0;
    
    
    public Stock() {
        this.subscribedClients = new HashMap<>();
        
    }
    
    public void setHistory(Double[] history) {
        this.history = history;
        this.price = history[0];
    }
    
    public void nextPrice() {
        if (currentPosition >= history.length) {
            currentPosition = -1;
        }
        
        currentPosition++;
        
        price = history[currentPosition];
        
        notifyClients();
    }
    
    public void addSubscriber(int ID, InterfaceClient client) {
        String stringID = String.valueOf(ID);
        
        if (subscribedClients.get(stringID) == null) {
            subscribedClients.put(stringID, client);
        }
    }
    
    public void notifyClients() {
        
        subscribedClients.forEach((ID, client) ->  {
            try {
                client.notify(this.name, this.price);
            } catch (RemoteException ex) {
                Logger.getLogger(Stock.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }
}
