/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_client;

import bovespa_client.ui.InterfaceClientUI;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author alineborges
 */
public class Client {
    
    public int ID;
    
    public ArrayList<Stock> listeningStocks = new ArrayList<>();
    public ArrayList<Stock> myStocks = new ArrayList<>();
    public ArrayList<Order> orders = new ArrayList<>();
    
    public InterfaceClientUI delegate;
    
    public Client() {
        Random rand = new Random();
        this.ID = rand.nextInt(1000) + 1;
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
    }
    
    
}
