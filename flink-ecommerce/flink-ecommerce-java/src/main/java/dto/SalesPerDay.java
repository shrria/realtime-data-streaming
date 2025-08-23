package dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
public class SalesPerDay {
    private Date transactionDate;
    private double totalSales;
    private int totalTransactions;

    public String getKey() {
        return String.format("%tY-%<tm-%<td", transactionDate);
    }
}
