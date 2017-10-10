/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server;

import bovespa_server.util.IServerUI;
import bovespa_server.util.AllStocks;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author alineborges
 */
public class Server {
    
    public AllStocks allstocks;
    public ArrayList<Order> orders = new ArrayList<>();
    
    public IServerUI delegate;
    
    public Server() {
        
        this.allstocks = new AllStocks();
        
        startTimer();
        
    }
    
    private void startTimer() {
        ActionListener action = (ActionEvent e) -> {
            allstocks.onNext();
            delegate.updateStocks();
        };
     
        Timer timer=new Timer(2500,action);//create the timer which calls the actionperformed method for every 1000 millisecond(1 second=1000 millisecond)
        timer.start();//start the task
    }
    
}
