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
