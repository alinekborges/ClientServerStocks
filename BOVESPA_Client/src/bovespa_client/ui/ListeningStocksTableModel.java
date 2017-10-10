/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bovespa_client.ui;

import bovespa_client.Stock;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author alinekborges
 */
public class ListeningStocksTableModel extends AbstractTableModel {
    
    public ArrayList<Stock> stocks;
            
    public ListeningStocksTableModel(ArrayList<Stock> stocks) {
        this.stocks = stocks;
    }

    @Override
    public int getRowCount() {
        return stocks.size();
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) { return "STOCK"; }
        else { return "PRICE"; }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

}
