/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import bovespa_client.ui.InterfaceClientUI;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author alineborges
 */
public final class Client extends UnicastRemoteObject implements InterfaceClient {
    
    public int ID;
    
    //connection to call server methods
    public InterfaceServer server;
    
    public ArrayList<Stock> listeningStocks = new ArrayList<>();
    public ArrayList<Stock> myStocks = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    
    //To separate UI logic from Client logic
    public InterfaceClientUI delegate;
    
    /**
     * Construct Client object with random ID and starts RMI
     * @throws RemoteException 
     */
    public Client() throws RemoteException {
        Random rand = new Random();
        this.ID = rand.nextInt(1000) + 1;
        startRMI();
    }
    
    /**
     * Add a new order and send it to the server
     * @param order 
     */
    public void addOrder(Order order) {
        
        Order oldOrder = this.orderWithParams(order.type, order.stock);
        //Oh! There is already an order with this stock, so just update it
        if (oldOrder != null && oldOrder.price == order.price) {
            oldOrder.quantity += order.quantity;
        } else {
            this.orders.add(order);
        }
       
        this.delegate.updateOrders();
        
        //Sends buy or send order to the server
        try {
            if (order.type == Order.Type.BUY) {
                this.server.buyOrder(order.stock, order.quantity, order.price, ID, this);
            } else {
                this.server.sellOrder(order.stock, order.quantity, order.price, ID, this);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Subscribes to server to get updates for a certain stock
     * @param stockName 
     */
    public void addListeningStock(String stockName) {
        
        //If you already is subscribed to this stock
        //don't do it again!
        if (this.listeningStockWithName(stockName) != null) {
            return;
        }
        
        Stock stock = new Stock();
        stock.name = stockName;
        this.listeningStocks.add(stock);
        
        
        try {
            this.server.subscribe(stockName, ID, this);
            stock.price = this.server.getPrice(stockName);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.delegate.updateStocks();
        
    }
    //****************************************
    /**
     * Starts RMI conection
     */
    public void startRMI() {
        try {
            Registry refSN = LocateRegistry.getRegistry("localhost", 2021);
            server = (InterfaceServer) refSN.lookup("RefServidor");
            System.out.println("Client running");
            

        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(new JFrame(), "Error connecting to server");
        }
    }
    
    /**
     * Remote method
     * Gets notification of price change from server
     * @param stockName
     * @param price
     * @throws RemoteException 
     */
    @Override
    public void notify(String stockName, Double price) throws RemoteException {
        Stock stock = listeningStockWithName(stockName);
        
        if (stock != null) {
            
            stock.price = price;
            delegate.updateStocks();
        }
    }

    /**
     * Remote method
     * Received when the buy order is executed
     * @param stockName
     * @param quantity
     * @param price 
     */
    @Override
    public void buyOrderCompleted(String stockName, int quantity, Double price) {
        Order order = this.orderWithParams(Order.Type.BUY, stockName);
        System.out.println("BUY ORDER QUANTITY: " + quantity + " PRICE " + price);
        if (order != null) {
            this.executeOrder(order, quantity, price);
            delegate.updateOrders();
        }
    }

    /**
     * Remote method
     * Received when my sell order is executed
     * @param stockName
     * @param quantity
     * @param price 
     */
    @Override
    public void sellOrderCompleted(String stockName, int quantity, Double price) {
        Order order = this.orderWithParams(Order.Type.SELL, stockName);
        System.out.println("BUY ORDER QUANTITY: " + quantity + " PRICE " + price);
        if (order != null) {
            this.executeOrder(order, quantity, price);
            delegate.updateOrders();
        }
    }
    
    //******************************************
    // Helper functions
    /**
     * Creates stocks based on name, quantity and price
     * @param stockName
     * @param quantity
     * @param price
     * @return 
     */
    private Stock createStock(String stockName, int quantity, Double price) {
        Stock stock = new Stock();
        stock.name = stockName;
        stock.quantity = quantity;
        stock.price = price;
        return stock;
    }
    
    /**
     * Runs the list and find the stock being observed with that name
     * @param stockName
     * @return 
     */
    public Stock listeningStockWithName(String stockName) {
        
        for (Stock stock : listeningStocks) {
            if (stock.name.equals(stockName)) {
                return stock;
            }
        }
        
        return null;
    }
    
    /**
     * Finds the stock reference with given name
     * @param stockName
     * @return 
     */
    public Stock myStockWithName(String stockName) {
        
        for (Stock stock : myStocks) {
            if (stock.name.equals(stockName)) {
                return stock;
            }
        }
        
        return null;
    }
    
    /**
     * Find order reference with stock name
     * @param type
     * @param stockName
     * @return 
     */
    public Order orderWithParams(Order.Type type, String stockName) {
        for (Order order : orders) {
            //TODO: Parcial order
            if (order.type == type
                    && order.stock.equals(stockName)) {
                return order;
            }
        }
        
        return null;
    }
    
    /**
     * When some order is received, it needs to be executed in client
     * @param order
     * @param quantity
     * @param price 
     */
    public void executeOrder(Order order, int quantity, Double price) {
        
        if (order.quantity == quantity) {
                order.status = Order.Status.EXECUTED;
        } else {
                order.quantity -= quantity;
        }
        
        Stock stock = this.myStockWithName(order.stock);
        
        //The only way is buying a new stock
        if (order.type == Order.Type.BUY) {
            Stock newstock = this.createStock(order.stock, quantity, price);
            this.myStocks.add(newstock);
        }
        
        
        
        delegate.updateMyStocks();
    }
    
 
    
}
