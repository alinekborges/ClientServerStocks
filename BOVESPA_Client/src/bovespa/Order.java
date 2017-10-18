/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

/**
 *
 * @author alinekborges
 */
public class Order {
    
    public enum Type {
        BUY, SELL
    }
    
    public enum Status {
        PENDIND, EXECUTED
    }
    
    public Type type; //Order type, if its by or sell
    public String stock; //Stock name, like "EMBR3"
    public Double price; //Stock price
    public int quantity; //Quantity of this stock
    public Status status = Status.PENDIND; //Stock status, pending or executed
    
}
