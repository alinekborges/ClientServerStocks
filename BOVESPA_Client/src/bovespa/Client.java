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
    
    public InterfaceServer server;
    
    public ArrayList<Stock> listeningStocks = new ArrayList<>();
    public ArrayList<Stock> myStocks = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    
    public InterfaceClientUI delegate;
    
    public Client() throws RemoteException {
        Random rand = new Random();
        this.ID = rand.nextInt(1000) + 1;
        startRMI();
    }
    
    public void addOrder(Order order) {
        
        //If you already is subscribed to this stock
        //don't do it again!
        Stock oldStock = this.myStockWithName(order.stock);
        if (oldStock != null && oldStock.quantity < order.quantity) {
            JOptionPane.showMessageDialog(new JFrame(), "Can't sell something you don' have!");
            return;
        }
        
        Order oldOrder = this.orderWithParams(order.type, order.stock);
        //Oh! There is already an order with this stock, so just update it
        if (oldOrder != null && oldOrder.price == order.price) {
            oldOrder.quantity += order.quantity;
        } else {
            this.orders.add(order);
        }
       
        this.delegate.updateOrders();
        
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
    
    public void addListeningStock(String stockName) {
        
        //If you already is subscribed to this stock
        //don't do it again!
        if (this.listeningStockWithName(stockName) != null) {
            return;
        }
        
        Stock stock = new Stock();
        stock.name = stockName;
        this.listeningStocks.add(stock);
        this.delegate.updateStocks();
        
        try {
            this.server.subscribe(stockName, ID, this);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //****************************************
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
    
    @Override
    public void notify(String stockName, Double price) throws RemoteException {
        Stock stock = listeningStockWithName(stockName);
        
        System.out.println("Price for" + stockName + ": " + price);
        
        if (stock != null) {
            
            stock.price = price;
            delegate.updateStocks();
        }
    }

    @Override
    public void buyOrderCompleted(String stockName, int quantity, Double price) {
        Order order = this.orderWithParams(Order.Type.BUY, stockName);
        if (order != null) {
            order.status = Order.Status.EXECUTED;
            this.executeOrder(order);
            delegate.updateOrders();
        }
    }

    @Override
    public void sellOrderCompleted(String stockName, int quantity, Double price) {
        Order order = this.orderWithParams(Order.Type.SELL, stockName);
        if (order != null) {
            order.status = Order.Status.EXECUTED;
            this.executeOrder(order);
            delegate.updateOrders();
        }
    }
    
    //******************************************
    // Helper functions
    public Stock createStock(String stockName, int quantity, Double price) {
        Stock stock = new Stock();
        stock.name = stockName;
        stock.quantity = quantity;
        stock.price = price;
        return stock;
    }
    
    public Stock listeningStockWithName(String stockName) {
        
        for (Stock stock : listeningStocks) {
            if (stock.name.equals(stockName)) {
                return stock;
            }
        }
        
        return null;
    }
    
    public Stock myStockWithName(String stockName) {
        
        for (Stock stock : myStocks) {
            if (stock.name.equals(stockName)) {
                return stock;
            }
        }
        
        return null;
    }
    
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
    
    public void executeOrder(Order order) {
        
        Stock stock = this.myStockWithName(order.stock);
        
        //The stock is already in my wallet, so updateValue
        if (stock != null) {
            
            if (order.type == Order.Type.BUY) {
                stock.quantity += order.quantity;
            } else {
                stock.quantity -= order.quantity;
            }
            
            //Update PM
            stock.price = (stock.price + order.price) / 2;
            
        } else {
            //The only way is buying a new stock
            Stock newstock = this.createStock(order.stock, order.quantity, order.price);
            this.myStocks.add(newstock);
            
        }
        
        delegate.updateMyStocks();
    }
    
 
    
}
