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
        //TODO: Actually send order
        this.orders.add(order);
        this.delegate.updateOrders();
    }
    
    public void addListeningStock(String stockName) {
        //TODO: Subscribe Stock 
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
    
    public Stock stockWithName(String stockName) {
        
        for (Stock stock : listeningStocks) {
            if (stock.name.equals(stockName)) {
                return stock;
            }
        }
        
        return null;
    }

    //****************************************
    public void startRMI() {
        try {
            Registry refSN = LocateRegistry.getRegistry("localhost", 2021);
            server = (InterfaceServer) refSN.lookup("RefServidor");
            System.out.println("Client running");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void notify(String stockName, Double price) throws RemoteException {
        Stock stock = stockWithName(stockName);
        
        System.out.println("Price for" + stockName + ": " + price);
        
        if (stock != null) {
            
            stock.price = price;
            delegate.updateStocks();
        }
    }
    
    
    
}
