package shopping;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientLog implements Serializable {

    private static final long serialVersionUID = 13092022L;

    private final List<String[]> entry;
    public ClientLog() {
        this.entry = new ArrayList<>();
    }
    public void log(int productCode, int productAmount) {
        String[] choice = new String[2];
        choice[0] = productCode + "";
        choice[1] = productAmount + "";
        entry.add(choice);
    }
    public void exportAsCSV(File txtFile) {

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(txtFile, true))) {
            csvWriter.writeAll(entry);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
