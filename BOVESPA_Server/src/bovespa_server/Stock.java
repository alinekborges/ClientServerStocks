/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server;

/**
 *
 * @author alinekborges
 */
public class Stock {
    
    public String name;
    public Double price;
    private Double[] history;
    public int currentPosition = 0;
    
    public Stock() {
        
    }
    
    public void setHistory(Double[] history) {
        this.history = history;
        this.price = history[0];
    }
    
    public void nextPrice() {
        if (currentPosition >= history.length) {
            currentPosition = -1;
        }
        
        currentPosition++;
        
        price = history[currentPosition];
    }
    
}
