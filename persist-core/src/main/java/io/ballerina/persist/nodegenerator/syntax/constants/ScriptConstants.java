package io.ballerina.persist.nodegenerator.syntax.constants;

/**
 * Class encapsulating constants for script generation.
 *
 * @since 0.1.0
 */

public class ScriptConstants {
    private ScriptConstants() {}

    public static final String FUNCTION_CLOSE = "}";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String VAR = "var ";
    public static final String FUNCTION_HEADER = "function createSheets() {";

    public static final String GET_BY_SHEET_NAME = "yourNewSheet = activeSpreadsheet.getSheetByName(\"{0}\");";
    public static final String CHECK_IF_SHEET_EXIST = "\tif (yourNewSheet != null) {";
    public static final String DELETE_SHEET = "activeSpreadsheet.deleteSheet(yourNewSheet);";
    public static final String INSERT_SHEET = "yourNewSheet = activeSpreadsheet.insertSheet();";
    public static final String SET_SHEET_NAME = "yourNewSheet.setName(\"{0}\");";
    public static final String COMMA_SPACE = ", ";
    public static final String APPEND_ROW_TO_SHEET = "yourNewSheet.appendRow([{0}]);";
    public static final String GET_ACTIVE_SPREADSHEET = "var activeSpreadsheet " +
            "= SpreadsheetApp.getActiveSpreadsheet();";
}
