import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;

public class MyTable extends AbstractTableModel{

    //STRING IS SERIALIZABLE BY DEFAULT
    private  ArrayList<String[]> ROWS = new ArrayList<>();
    private final String[] COLS = {"USERNAME", "SCORE", "TIME"};

    @Override
    //Assign "COLS" as names of table
    public String getColumnName(int column){return COLS[column];}

    @Override
    public int getRowCount() {return ROWS.size();}

    @Override
    public int getColumnCount() {return COLS.length;}

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {return ROWS.get(rowIndex)[columnIndex];}

    public void add(String[] info){
        //Adds a new row
        ROWS.add(info);
    }
}
