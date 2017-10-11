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
    public ArrayList<Order> buyOrders = new ArrayList<>();
    public ArrayList<Order> sellOrders = new ArrayList<>();

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
        currentPosition++;
        
        if (currentPosition >= history.length) {
            currentPosition = 0;
        }

        price = history[currentPosition];
        
        notifyClients();
    }
    
    public void addSubscriber(int ID, InterfaceClient client) {
        String stringID = String.valueOf(ID);
        
        if (subscribedClients.get(stringID) == null) {
            subscribedClients.put(stringID, client);
        }
    }
    
    public void addOrder(Order order) {
        
        if (order.type == Order.Type.BUY) {
            
            Order matchOrder = this.verifyCanBuy(order);
            //TODO: Parcial orders
            if (matchOrder != null) {
                //Efected at average of both prices
                Double price = order.price + matchOrder.price / 2;
                order.completeOrder(price);
                matchOrder.completeOrder(price);
                this.sellOrders.remove(matchOrder);
            } else {
                this.buyOrders.add(order);
            }
        }
        
        if (order.type == Order.Type.SELL) {
            
            Order matchOrder = this.verifyCanSell(order);
            //TODO: Parcial orders
            if (matchOrder != null) {
                //Efected at average of both prices
                Double price = order.price + matchOrder.price / 2;
                order.completeOrder(price);
                matchOrder.completeOrder(price);
                this.buyOrders.remove(matchOrder);
            } else {
                this.sellOrders.add(order);
            }
        }
        
    }
    
    public Order verifyCanBuy(Order newOrder) { 
        
        Double buyPrice = newOrder.price;
        
        for (Order order : sellOrders) {
            //TODO: Parcial orders
            if (order.price <= buyPrice && order.quantity == newOrder.quantity) {
                //Sell price lower than buy price
                return order;
            }
        }
        
        return null;
    }
    
    public Order verifyCanSell(Order newOrder) {
        
        Double sellPrice = newOrder.price;
        
        for (Order order : buyOrders) {
            //TODO: Parcial orders
            if (order.price > sellPrice && order.quantity == newOrder.quantity) {
                //Sell price lower than buy price
                return order;
            }
        }
        
        return null;
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
