package com.woogleFX.file.font;

import java.util.ArrayList;

public class FontDataParser {

    private enum ParseMode {
        FULL_ARGUMENT,
        LIST,
        STRING,
    }


    private enum QuoteStatus {
        NONE,
        WITHIN_SINGLE_QUOTES,
        WITHIN_DOUBLE_QUOTES
    }


    public static ArrayList<FontCommand> parse(String text) {

        ArrayList<FontCommand> commands = new ArrayList<>();


        ArrayList<FontData> currentCommandBuilder = new ArrayList<>();

        // Start parsing from the beginning of the text.
        for (currentIndex = 0; currentIndex < text.length(); currentIndex++) {

            // Parse the text for the next FontData.
            FontData fontData = recursiveParse(text, ParseMode.FULL_ARGUMENT);

            // Add the received font data to the currentCommandBuilder.
            if (fontData != null) currentCommandBuilder.add(fontData);

            // If the current command ended:
            if (commandEndFlag) {

                // Reset the command end flag.
                commandEndFlag = false;

                // Build a command from the currentCommandBuilder and add it to the command list.
                FontData commandIDData = currentCommandBuilder.remove(0);
                FontKeyword commandIDKeyword = (FontKeyword)commandIDData;
                String commandID = commandIDKeyword.getKeyword();

                FontData[] commandArguments = currentCommandBuilder.toArray(new FontData[0]);

                FontCommand command = new FontCommand(commandID, commandArguments);
                commands.add(command);

                // Clear the currentCommandBuilder.
                currentCommandBuilder.clear();

            }

        }

        return commands;

    }


    /** Represents the index at which the input text is being parsed. */
    private static int currentIndex;


    /** Set to true when a parse reaches the end of a command. */
    private static boolean commandEndFlag = false;


    /** Represents the current quote status - whether the text is within single quotes, double quotes, or neither. */
    private static QuoteStatus currentQuoteStatus;


    /** Parses the input text for one FontData.
     * Data could be a string, integer, list, or arbitrary keyword. */
    private static FontData recursiveParse(String text, ParseMode parseMode) {

        ArrayList<FontData> listDataBuilder = new ArrayList<>();

        StringBuilder dataBuilder = new StringBuilder();

        // Iterate from the given index to the end of the text, or at least until the next ")".
        for (; currentIndex < text.length(); currentIndex++) {

            // Get the character at this index.
            char c = text.charAt(currentIndex);

            // If a string is being parsed, ignore the regular syntax entirely.
            if (parseMode == ParseMode.STRING) switch (c) {

                case '"' -> {

                    // If the string was already within double quotes, terminate the string.
                    if (currentQuoteStatus == QuoteStatus.WITHIN_DOUBLE_QUOTES) {
                        currentQuoteStatus = QuoteStatus.NONE;
                        return new FontString(dataBuilder.toString());
                    }

                    // Otherwise, add the double quote to the string.
                    dataBuilder.append('"');

                }

                case '\'' -> {

                    // If the string was already within single quotes, terminate the string.
                    if (currentQuoteStatus == QuoteStatus.WITHIN_SINGLE_QUOTES) {
                        currentQuoteStatus = QuoteStatus.NONE;
                        return new FontString(dataBuilder.toString());
                    }

                    // Otherwise, add the single quote to the string.
                    dataBuilder.append('\'');

                }

                default -> dataBuilder.append(c);

            }

            // Otherwise, parse the character appropriately.
            else switch (c) {

                case '(' -> {
                    // Begin a new list.

                    // Call another parse to get the full string.
                    currentIndex ++;
                    FontData listData = recursiveParse(text, ParseMode.LIST);

                    // Note that currentIndex is now at the very end of the list declaration.
                    // Therefore, this parse doesn't need to worry about it at all.

                    if (parseMode == ParseMode.FULL_ARGUMENT) {
                        // Return the list data.
                        return listData;
                    } else if (parseMode == ParseMode.LIST) {
                        // Add the list data to this parse's own list.
                        listDataBuilder.add(listData);
                    }

                }

                case ')' -> {
                    // End a list.

                    // If this parse was still reading data (ex. a number at the end of the list),
                    // parse that data and add it to the list.
                    if (!dataBuilder.isEmpty()) {
                        String data = dataBuilder.toString();
                        FontData fontData = parseSingleStringForData(data);
                        listDataBuilder.add(fontData);
                    }

                    // Then, this parse can simply return the list it was building.
                    FontData[] fontDataArray = listDataBuilder.toArray(new FontData[0]);
                    return new FontList(fontDataArray);

                }

                case '\'', '"' -> {
                    // Begin a single string.

                    // Set the current quote status.
                    if (c == '\'') currentQuoteStatus = QuoteStatus.WITHIN_SINGLE_QUOTES;
                    else currentQuoteStatus = QuoteStatus.WITHIN_DOUBLE_QUOTES;

                    // Call another parse to get the full string.
                    currentIndex ++;
                    FontData stringData = recursiveParse(text, ParseMode.STRING);

                    if (parseMode == ParseMode.FULL_ARGUMENT) {
                        // Return the string data.
                        return stringData;
                    } else if (parseMode == ParseMode.LIST) {
                        // Add the string data to this parse's own list.
                        listDataBuilder.add(stringData);
                    }

                }

                case ',' -> {
                    // End an entry in a list.

                    if (dataBuilder.isEmpty()) break;

                    // Get the data from and clear the StringBuilder.
                    String data = dataBuilder.toString();
                    dataBuilder = new StringBuilder();

                    // Parse the data and add it to the list.
                    FontData fontData = parseSingleStringForData(data);
                    listDataBuilder.add(fontData);

                }

                case ';' -> {
                    // End the entire command.

                    // Set the command end flag so that the main parse knows the command is over.
                    commandEndFlag = true;

                    // Then, this parse can simply return whatever it was building.
                    if (parseMode == ParseMode.LIST) {
                        FontData[] fontDataArray = listDataBuilder.toArray(new FontData[0]);
                        return new FontList(fontDataArray);
                    } else {
                        String data = dataBuilder.toString();
                        return parseSingleStringForData(data);
                    }

                }

                case '\n', ' ' -> {
                    // End an argument.

                    // If the data builder has no data, don't do anything.
                    if (dataBuilder.isEmpty()) break;

                    // Get the data from the data builder.
                    String data = dataBuilder.toString();

                    // Return the data.
                    return parseSingleStringForData(data);

                }

                default -> {

                    // Check for ZERO WIDTH NO-BREAK SPACE and carriage return and exlude them.
                    if (c == 0xFEFF || c == 0xD) break;

                    dataBuilder.append(c);
                }

            }

        }

        // If a parse got to the end of the file, all the commands have already been processed.
        // Therefore, this parse can just end.
        return null;

    }


    private static FontData parseSingleStringForData(String string) {

        // If the string is a valid integer, return a FontInteger.
        // This can be checked by just trying to parse the string and seeing what happens.
        try {
            int integerData = Integer.parseInt(string);
            // If this line is reached, then the string must be a valid integer.
            return new FontInteger(integerData);
        } catch (NumberFormatException ignored) {
            // The string is NOT a valid integer.

            try {
                double doubleData = Double.parseDouble(string);
                return new FontDouble(doubleData);
            } catch (NumberFormatException alsoIgnored) {
                // The only other thing it could be is a keyword, so a FontKeyword is returned instead.
                return new FontKeyword(string);
            }
        }

    }

}
