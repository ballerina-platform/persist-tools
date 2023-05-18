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
