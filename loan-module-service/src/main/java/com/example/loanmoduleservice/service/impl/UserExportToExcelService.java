package com.example.loanmoduleservice.service.impl;

import com.example.loanmoduleservice.entity.TransactionLog;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserExportToExcelService extends ReportAbstract {

    public void writeTableData(List<TransactionLog> data) {

        // font style content
        CellStyle style = getFontContentExcel();

        // starting write on row
        int startRow = 2;

        // write content
        for (TransactionLog transactionLog : data) {
            Row row = sheet.createRow(startRow++);
            int columnCount = 0;
            createCell(row, columnCount++, transactionLog.getTransactionId(), style);
            createCell(row, columnCount++, String.valueOf(transactionLog.getAmount()), style);
            createCell(row, columnCount++, transactionLog.getTransactionType(), style);
            createCell(row, columnCount++, transactionLog.getPayerbankDetails(), style);
            createCell(row, columnCount++, transactionLog.getReceiverbankDetails(), style);
            createCell(row, columnCount++, String.valueOf(transactionLog.getTimestamp()), style);

        }
    }

    public void exportToExcel(HttpServletResponse response, List<TransactionLog> data) throws IOException {
        newReportExcel();

        // response  writer to excel
        response = initResponseForExportExcel(response, "UserExcel");
        ServletOutputStream outputStream = response.getOutputStream();


        // write sheet, title & header
        String[] headers = new String[]{"Transaction Id", "Amount", "Transaction Type", "Payer bank Details", "Receiver bank Details", "Created Date"};
        writeTableHeaderExcel("Sheet Transaction", "Report Transaction", headers);

        // write content row
        writeTableData(data);

        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
