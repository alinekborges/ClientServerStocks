/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server;

/**
 *
 * @author alineborges
 */
public class Order {
    
    public enum Type {
        BUY, SELL
    }
    
    public enum Status {
        PENDIND, EXECUTED
    }
    
    public Type type;
    public String stock;
    public Double price;
    public Status status = Status.PENDIND;
    
    
}
