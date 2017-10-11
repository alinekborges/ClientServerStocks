/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server.ui;

import bovespa.Order;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author alineborges
 */
public class OrdersTableModel extends AbstractTableModel {
    
    public ArrayList<Order> orders;
            
    public OrdersTableModel(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public int getRowCount() {
        return orders.size();
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) { return "CLIENT"; }
        if (col == 1) { return "TYPE"; }
        if (col == 2) { return "NAME"; }
        if (col == 3) { return "PRICE"; }
        if (col == 4) { return "QUANTITY"; }
        if (col == 5) { return "STATUS"; }
        
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Order order = orders.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return order.clientID;
            case 1:
                return order.type;
            case 2:
                return order.stock;
            case 3:
                return order.price;
            case 4:
                return order.quantity;
            case 5:
                return order.status;
            default:
                return "";
        }
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

}