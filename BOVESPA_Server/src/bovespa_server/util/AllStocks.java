/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server.util;

import bovespa.InterfaceClient;
import bovespa.Stock;

/**
 *
 * @author alinekborges
 */
public class AllStocks {
    
    public Stock[] stocks;
    
    /**
     * Initialize all stocks data using history from StocksHistory class
     */
    public AllStocks() {
        
        Stock EMBR3 = new Stock();
        EMBR3.name = "EMBR3";
        EMBR3.setHistory(StocksHistory.historyFrom(EMBR3.name));
        
        Stock PETR4 = new Stock();
        PETR4.name = "PETR4";
        PETR4.setHistory(StocksHistory.historyFrom(PETR4.name));
        
        Stock CSNA3 = new Stock();
        CSNA3.name = "CSNA3";
        CSNA3.setHistory(StocksHistory.historyFrom(CSNA3.name));
        
        Stock PTBL3 = new Stock();
        PTBL3.name = "PTBL3";
        PTBL3.setHistory(StocksHistory.historyFrom(PTBL3.name));
        
        stocks = new Stock[]{EMBR3, PETR4, CSNA3, PTBL3};
        
        
    }
    
    public void onNext() {
        
        for (Stock stock : stocks) {
            stock.nextPrice();
        }
        
    }
    
    public Stock stockWithName(String stockName) {
        for (Stock stock : stocks) {
            if (stock.name.equals(stockName)) {
                return stock;
            }
        }
        
        return null;
    }
    
    public void subscribe(String stockName, int clientID, InterfaceClient client) {
        
        Stock stock = stockWithName(stockName);
        
        if (stock != null) {
            stock.addSubscriber(clientID, client);
        }
        
    }
    
}
