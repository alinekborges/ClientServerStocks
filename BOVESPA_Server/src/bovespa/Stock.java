/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import static java.lang.Math.abs;
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
                int quantity = Math.min(matchOrder.quantity, order.quantity);
                
                Double endprice = (order.price + matchOrder.price) / 2;
                order.completeOrder(endprice, quantity);
                matchOrder.completeOrder(endprice, quantity);
                this.sellOrders.remove(matchOrder);
            } else {
                addNewBuyOrder(order);
            }
        }
        
        if (order.type == Order.Type.SELL) {
            
            Order matchOrder = this.verifyCanSell(order);
            //There is an order that matches the one I have!!
            if (matchOrder != null) {
                int quantity = Math.min(matchOrder.quantity, order.quantity);
                
                //Efected at average of both prices
                Double price = (order.price + matchOrder.price) / 2;
                order.completeOrder(price, quantity);
                matchOrder.completeOrder(price, quantity);
                this.buyOrders.remove(matchOrder);
            } else {
                addNewSellOrder(order);
            }
        }
        
    }
    
    public void addNewBuyOrder(Order neworder) {
        for (Order order : buyOrders) {
            if (order.clientID == neworder.clientID && order.stock == neworder.stock && order.price == neworder.price) {
                order.quantity += neworder.quantity;
                return;
            }
        }
        
        buyOrders.add(neworder);
    }
    
    public void addNewSellOrder(Order neworder) {
        for (Order order : sellOrders) {
            if (order.clientID == neworder.clientID && order.stock == neworder.stock && order.price == neworder.price) {
                order.quantity += neworder.quantity;
                return;
            }
        }
        
        sellOrders.add(neworder);
    }
    
    public Order verifyCanBuy(Order newOrder) { 
        
        Double buyPrice = newOrder.price;
        
        for (Order order : sellOrders) {
            //Sell price lower than buy price
            //and want I want to buy is lower than order quantity
            if (order.price <= buyPrice) {
                return order;
            }
        }
        
        return null;
    }
    
    public Order verifyCanSell(Order newOrder) {
        
        Double sellPrice = newOrder.price;
        
        for (Order order : buyOrders) {
            //Sell price lower than buy price
            //and want I want to sell less than someone wants to buy
            if (order.price > sellPrice) {
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
