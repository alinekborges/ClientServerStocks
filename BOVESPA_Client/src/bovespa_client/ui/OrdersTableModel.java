/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_client.ui;

import bovespa_client.Order;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author alinekborges
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
        if (col == 0) { return "TYPE"; }
        if (col == 1) { return "NAME"; }
        if (col == 2) { return "PRICE"; }
        if (col == 3) { return "STATUS"; }
        
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Order order = orders.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return order.type;
            case 1:
                return order.stock;
            case 2:
                return order.price;
            case 3:
                return order.status;
            default:
                return "";
        }
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

}