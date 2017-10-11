/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server.util;

import bovespa.InterfaceClient;
import bovespa.Stock;
import bovespa_server.util.StocksHistory;

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
        
        stocks = new Stock[]{EMBR3};
        
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
