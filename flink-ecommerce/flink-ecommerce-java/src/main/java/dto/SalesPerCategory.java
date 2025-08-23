package dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
public class SalesPerCategory {
    private Date transactionDate;
    private String category;
    private double totalSales;
    private int totalTransactions;

    public String getKey() {
        String formattedDate = String.format("%tY-%<tm-%<td", transactionDate);
        return formattedDate + "-" + category;
    }
}
