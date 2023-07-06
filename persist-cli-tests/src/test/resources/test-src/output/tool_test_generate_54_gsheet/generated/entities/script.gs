// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer for model.
// Please verify the generated scripts and execute them against the target DB server.

function createSheets() {
	var activeSpreadsheet = SpreadsheetApp.getActiveSpreadsheet();
	var yourNewSheet = activeSpreadsheet.getSheetByName("Employee");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Employee");
	yourNewSheet.appendRow(["empNo", "firstName", "lastName", "birthDate", "gender", "hireDate", "departmentDeptNo", "workspaceWorkspaceId"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Workspace");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Workspace");
	yourNewSheet.appendRow(["workspaceId", "workspaceType", "locationBuildingCode"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Building");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Building");
	yourNewSheet.appendRow(["buildingCode", "city", "state", "country", "postalCode", "'type"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Department");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Department");
	yourNewSheet.appendRow(["deptNo", "deptName"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("OrderItem");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("OrderItem");
	yourNewSheet.appendRow(["orderId", "itemId", "quantity", "notes"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Sheet1");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
}


function insertRecord(metadataKey, metadataValue, array, spreadsheetId, range, sheetId) {
  var request = {
    majorDimension: "ROWS",
    dataFilters: [{
      developerMetadataLookup: {
        metadataKey: metadataKey,
        metadataValue: metadataValue
      }
    }]
  };
  var response = Sheets.Spreadsheets.Values.batchGetByDataFilter(
    request, spreadsheetId)
  if (response.hasOwnProperty("valueRanges")) {
    if (response.valueRanges.length > 0) {
      throw new Error('ErrorCode:409, Duplicate Record');
    }
  }
  var values = [
    array
  ];
  var resource = {
    values: values
  };
  var response = Sheets.Spreadsheets.Values.append(resource, spreadsheetId, range, {
    valueInputOption: 'USER_ENTERED'
  });

  let row = response.updates.updatedRange.split("!")[1].split(":")[0];
  let rowId = row.replace(/\D/g,'');
  var requests = [{
    createDeveloperMetadata: {
      developerMetadata: {
        metadataKey: metadataKey,
        metadataValue: metadataValue,
        visibility: "DOCUMENT",
        location: {
          dimensionRange: {
            sheetId: sheetId,
            dimension: 'ROWS',
            startIndex: parseInt(rowId)-1,
            endIndex: parseInt(rowId)
          }
        }
      }
    }
  }];

  var response2 = Sheets.Spreadsheets.batchUpdate({
      requests: requests
    }, spreadsheetId);
}


