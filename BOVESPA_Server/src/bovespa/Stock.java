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
    //keeps an array of buy orders and sell orders
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
    
    /**
     * Goes to next price in list, to update stock price and notify clients subscribed to this stock
     */
    public void nextPrice() {
        currentPosition++;
        
        if (currentPosition >= history.length) {
            currentPosition = 0;
        }

        price = history[currentPosition];
        
        notifyClients();
    }
    
    /**
     * Adds a client subscriber to this stock that wants to receive prices updates
     * @param ID
     * @param client 
     */
    public void addSubscriber(int ID, InterfaceClient client) {
        String stringID = String.valueOf(ID);
        
        if (subscribedClients.get(stringID) == null) {
            subscribedClients.put(stringID, client);
        }
    }
    
    /**
     * Add a new order related to this stock
     * All other orders will be verified to check if this order can be executed or will just go to the list
     * If order can be executed, it will be partially or completely executed and call client with the order status, price and quantity sold/bought
     * @param order 
     */
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
                
                if (order.quantity == matchOrder.quantity) {
                    this.sellOrders.remove(matchOrder);
                }
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
                if (order.quantity == matchOrder.quantity) {
                    this.sellOrders.remove(matchOrder);
                }
            } else {
                addNewSellOrder(order);
            }
        }
        
    }
    
    /**
     * Add new Buy order to array (if it is from the same client and same price, just update quantity of curernt order)
     * @param neworder 
     */
    public void addNewBuyOrder(Order neworder) {
        for (Order order : buyOrders) {
            if (order.clientID == neworder.clientID && order.stock == neworder.stock && order.price == neworder.price) {
                order.quantity += neworder.quantity;
                return;
            }
        }
        
        buyOrders.add(neworder);
    }
    
    /**
     * Add new SELL order to array (if it is from the same client and same price, just update quantity of curernt order)
     * @param neworder 
     */
    public void addNewSellOrder(Order neworder) {
        for (Order order : sellOrders) {
            if (order.clientID == neworder.clientID && order.stock == neworder.stock && order.price == neworder.price) {
                order.quantity += neworder.quantity;
                return;
            }
        }
        
        sellOrders.add(neworder);
    }
    
    /**
     * Verify if BUY order has a corresponding sell order that can be combined together
     * @param newOrder
     * @return 
     */
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
    
    /**
     * * Verify if SELL order has a corresponding sell order that can be combined together
     * @param newOrder
     * @return 
     */
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
    
    /**
     * Notify all subscribed clients of a price update
     */
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
