package dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SalesPerMonth {
    private int year;
    private int month;
    private double totalSales;
    private int totalTransactions;

    public String getKey() {
        return year + "-" + month;
    }
}
