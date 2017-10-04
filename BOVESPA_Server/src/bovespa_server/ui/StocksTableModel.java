/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_server.ui;

import bovespa_server.Stock;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author alinekborges
 */
public class StocksTableModel extends AbstractTableModel {
    
    public Stock[] stocks;
            
    public StocksTableModel(Stock[] stocks) {
        this.stocks = stocks;
    }

    @Override
    public int getRowCount() {
        return stocks.length;
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) { return "STOCK"; }
        else { return "PRICE"; }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Stock stock = stocks[rowIndex];
        if (columnIndex == 0) {
            return stock.name;
        } else {
            return stock.price;
        }
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

}
