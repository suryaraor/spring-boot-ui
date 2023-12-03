package dev.surya.labs.service.impl;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import dev.surya.labs.entity.Constituency;
@Component
public class GoogleSheetUpdater {

    private static Sheets sheetsService;
    
    @Value("${sheet.id}")
    public String sheetID;
    
    @Value("${sheet.name}")
    public String sheetName;
    
    @Value("${credentials}")
    private String credentialsFile;
    
    @Value("${oauth.port}")
    private String oauthPort;
    
    @Autowired
    public SheetsServiceUtil sheetsServiceUtil;

    public void lead(Constituency constituency) {
    	updateParty(constituency, true);
    }
    
    public void won(Constituency constituency) {
    	updateParty(constituency, false);
    }
    
    
    public void updateParty(Constituency constituency) {
    	updateParty(constituency, true);
    }
    public void updateParty(Constituency constituency, boolean lead) {
        try {
            sheetsService = SheetsServiceUtil.getSheetsService(credentialsFile, oauthPort);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return;
        }

        // Specify the range to search (e.g., "Sheet1!A1:Z100")
        String rangeToSearch = sheetName+"!A1:L120";
        
        // Specify the content you want to find
        String targetContent = constituency.getId().toString();

        // Find the cell by content
        CellCoordinate cellCoordinate = null;
        cellCoordinate = findCellByContent(sheetID, rangeToSearch, targetContent);
        

        if (cellCoordinate != null) {
            int updateColumn = 0;
            if(lead) {
            	updateColumn = cellCoordinate.getCol()+10;
            }else {
            	updateColumn = cellCoordinate.getCol()+11;
            }
            updateCellValue(sheetID, cellCoordinate.getSheetName(), cellCoordinate.getRow(), updateColumn, lead? constituency.getLead(): constituency.getWon());
            
            if(lead) {
            	System.out.println("Updated leading " + constituency.getLead() +" with "+constituency.getMargin());
            }else {
            	System.out.println("Updated winning " +constituency.getWon() +" with "+constituency.getMargin());
            }
            
            
        } else {
            System.out.println("Cell not found.");
        }
    }

    private static CellCoordinate findCellByContent(String spreadsheetId, String range, String targetContent) {
        try {
            ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();

            List<List<Object>> values = response.getValues();

            if (values != null) {
                for (int row = 0; row < values.size(); row++) {
                    for (int col = 0; col < values.get(row).size(); col++) {
                        String cellContent = (String) values.get(row).get(col);

                        // Check if the cell content matches the target content
                        if (cellContent != null && cellContent.equals(targetContent)) {
                            return new CellCoordinate(range.split("!")[0], row + 1, col + 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Cell not found
    }

    

    
    private static void updateCellValue(String spreadsheetId, String sheetName, int row, int col, String newValue) {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(newValue)));

        try {
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, sheetName + "!" + getColumnName(col) + row, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getColumnName(int columnNumber) {
        int dividend = columnNumber;
        String columnName = "";
        int modulo;

        while (dividend > 0) {
            modulo = (dividend - 1) % 26;
            columnName = (char) (65 + modulo) + columnName;
            dividend = (dividend - modulo) / 26;
        }

        return columnName;
    }
    
    
}


class CellCoordinate {
    private final String sheetName;
    private final int row;
    private final int col;

    public CellCoordinate(String sheetName, int row, int col) {
        this.sheetName = sheetName;
        this.row = row;
        this.col = col;
    }

	public String getSheetName() {
		return sheetName;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
    
    
}