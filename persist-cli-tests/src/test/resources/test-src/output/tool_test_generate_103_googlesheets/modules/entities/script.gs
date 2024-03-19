// AUTO-GENERATED FILE.

// This file is an auto-generated file by Ballerina persistence layer for model.
// Please verify the generated scripts and execute them against the target DB server.

function createSheets() {
	var activeSpreadsheet = SpreadsheetApp.getActiveSpreadsheet();
	var yourNewSheet = activeSpreadsheet.getSheetByName("Appointment");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Appointment");
	yourNewSheet.appendRow(["id", "reason", "appointmentTime", "status", "patientId", "doctorId"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Patient");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Patient");
	yourNewSheet.appendRow(["id", "name", "age", "address", "phoneNumber", "gender"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Doctor");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
	yourNewSheet = activeSpreadsheet.insertSheet();
	yourNewSheet.setName("Doctor");
	yourNewSheet.appendRow(["id", "name", "specialty", "phoneNumber", "salary"]);

	yourNewSheet = activeSpreadsheet.getSheetByName("Sheet1");
	if (yourNewSheet != null) {
		activeSpreadsheet.deleteSheet(yourNewSheet);
	}
}
