/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import bovespa_server.util.IServerUI;
import bovespa_server.util.AllStocks;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author alineborges
 */
public final class Server extends UnicastRemoteObject implements InterfaceServer {
    
    public AllStocks allstocks;
    public ArrayList<Order> orders = new ArrayList<>();
    
    public IServerUI delegate;
    
    
    /**
     * Constructor that initializes RMI and timer
     * @throws RemoteException 
     */
    public Server() throws RemoteException {
        
        this.allstocks = new AllStocks();
        
        startTimer();
        
        startRMI();
        
    }
    
    /**
     * Starts the timer for updating stocks prices
     */
    private void startTimer() {
        ActionListener action = (ActionEvent e) -> {
            allstocks.onNext();
            delegate.updateStocks();
        };
     
        Timer timer=new Timer(2500,action);//create the timer which calls the actionperformed method for every 1000 millisecond(1 second=1000 millisecond)
        timer.start();//start the task
    }
    
    //********************************
    /**
     * Starts RMI Reference name and bind
     */
    public void startRMI() {
        try {
            Registry referenciaServicoNomes = LocateRegistry.createRegistry(2021);
            referenciaServicoNomes.rebind("RefServidor", this);
            System.out.println("Server running");
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Receives from client the subscription for one stock
     * @param stockName
     * @param clientID
     * @param client
     * @throws RemoteException 
     */
    @Override
    public void subscribe(String stockName, int clientID, InterfaceClient client) throws RemoteException {
         this.allstocks.subscribe(stockName, clientID , client);
    }

    /**
     * Receives a buy order from client
     * @param stockName
     * @param quantity
     * @param price
     * @param clientID
     * @param client
     * @throws RemoteException 
     */
    @Override
    public void buyOrder(String stockName, int quantity, Double price, int clientID, InterfaceClient client) throws RemoteException {
        Order order = this.createOrder(stockName, quantity, price, clientID, client);
        order.type = Order.Type.BUY;
        Stock stock = stockWithName(stockName);
        if (stock != null) {
            stock.addOrder(order);
            orders.add(order);
            this.delegate.updateOrders();
        }
    }

    /**
     * Receives a sell order from client
     * @param stockName
     * @param quantity
     * @param price
     * @param clientID
     * @param client
     * @throws RemoteException 
     */
    @Override
    public void sellOrder(String stockName, int quantity, Double price, int clientID, InterfaceClient client) throws RemoteException {
        Order order = this.createOrder(stockName, quantity, price, clientID, client);
        order.type = Order.Type.SELL;
        Stock stock = stockWithName(stockName);
        if (stock != null) {
            stock.addOrder(order);
            orders.add(order);
            this.delegate.updateOrders();
        }
    }
    
    /**
     * Returns the stock price to client
     * @param stockName
     * @return 
     */
    @Override
    public Double getPrice(String stockName) {
        Stock stock = this.stockWithName(stockName);
        return stock.price;
    }
    
    //********************************
    //Helper Methods
    /**
     * Creates order object with parameters
     * @param stockName
     * @param quantity
     * @param price
     * @param clientID
     * @param client
     * @return 
     */
    public Order createOrder(String stockName, int quantity, Double price, int clientID, InterfaceClient client) {
        Order order = new Order();
        order.stock = stockName;
        order.quantity = quantity;
        order.price = price;
        order.clientID = clientID;
        order.client = client;
        return order;
    }
    
    /**
     * return stock reference with name
     * @param name
     * @return 
     */
    public Stock stockWithName(String name) {
        return allstocks.stockWithName(name);
    }
    
}
