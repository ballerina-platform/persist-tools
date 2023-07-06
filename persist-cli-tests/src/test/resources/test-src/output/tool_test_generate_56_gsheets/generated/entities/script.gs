// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer for model.
// Please verify the generated scripts and execute them against the target DB server.

function createSheets() {
	var activeSpreadsheet = SpreadsheetApp.getActiveSpreadsheet();
	var yourNewSheet = activeSpreadsheet.getSheetByName("User");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("User");
	yourNewSheet.appendRow(["id", "name", "birthDate", "mobileNumber"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Post");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Post");
	yourNewSheet.appendRow(["id", "description", "tags", "category", "timestamp", "userId"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Follow");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Follow");
	yourNewSheet.appendRow(["id", "leaderId", "followerId", "timestamp"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Comment");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Comment");
	yourNewSheet.appendRow(["id", "comment", "timesteamp", "userId", "postId"]);

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


