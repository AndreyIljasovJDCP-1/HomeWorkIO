package shopping;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ClientLog {

    private final List<String[]> entry;

    public ClientLog() {
        this.entry = new ArrayList<>();
    }

    public void log(int productCode, int productAmount, boolean enabled) {
        if (enabled) {
            String[] choice = new String[2];
            choice[0] = Integer.toString(productCode + 1);
            choice[1] = Integer.toString(productAmount);
            entry.add(choice);
        }
    }

    public void exportAsCSV(String fileName, boolean enabled) {
        if (enabled) {
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName))) {
                csvWriter.writeAll(entry);
                System.out.println("\nЛоги сохранены в файл-> "
                        + new File(fileName).getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
