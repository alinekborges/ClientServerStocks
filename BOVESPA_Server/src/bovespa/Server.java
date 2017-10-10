/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa;

import bovespa_server.util.IServerUI;
import bovespa_server.util.AllStocks;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author alineborges
 */
public final class Server extends UnicastRemoteObject implements InterfaceServer {
    
    public AllStocks allstocks;
    public ArrayList<Order> orders = new ArrayList<>();
    
    public IServerUI delegate;
    
    public Server() throws RemoteException {
        
        
        this.allstocks = new AllStocks();
        
        startTimer();
        
        startRMI();
        
    }
    
    private void startTimer() {
        ActionListener action = (ActionEvent e) -> {
            allstocks.onNext();
            delegate.updateStocks();
        };
     
        Timer timer=new Timer(2500,action);//create the timer which calls the actionperformed method for every 1000 millisecond(1 second=1000 millisecond)
        timer.start();//start the task
    }
    
    //********************************
    public void startRMI() {
        try {
            Registry referenciaServicoNomes = LocateRegistry.createRegistry(2021);
            referenciaServicoNomes.rebind("RefServidor", this);
            System.out.println("Server running");
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void subscribe(String stockName, int clientID, InterfaceClient client) throws RemoteException {
         this.allstocks.subscribe(stockName, clientID , client);
    }
    
}
