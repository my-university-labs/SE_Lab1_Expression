package com.teamgz.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by me on 16-11-8.
 */
/*
 * input the expression string and format it
 * judge legal or not // not write
 * remove the parentheses
 */
public class InputParser {
    private boolean is_cmd = false;
    private String expression = "";
    private String command = "";
    private boolean legal_cmd = false;
    private boolean legal_exp = true;
    private Map<String, Double> cmds = new HashMap<>();
    private Pattern divideZreo = Pattern.compile("/[ ]*0[^.|]+|/[ ]*0$|/[ ]*0\\.0+$|/[ ]*0\\.0+[^\\d]");
    private Pattern legalCommand = Pattern.compile("^!\\s?(simplify|d/d[a-zA-Z]$|q$|help$)(\\s?[a-zA-Z]\\s?=\\s?\\d\\s?)*"); // judge the command
    private Pattern legalExpression = Pattern.compile("[^/+*a-zA-Z0-9.^-]");
    private Pattern whatCommand = Pattern.compile("(simplify|d/d[a-zA-Z]|q|help)");
    private Pattern cmdsContent = Pattern.compile("[a-zA-Z]\\s?=\\s?\\d");
    private Pattern findUnknown = Pattern.compile("[a-zA-Z]");
    private Pattern findNumber = Pattern.compile("\\d");
    public InputParser(String input) {
        input = input.trim();
        toUseParser(input);
        legalExpression(input);
    }
    public boolean isCommand() {
        return is_cmd;
    }
    public boolean isLegalCommand() {
        return legal_cmd;
    }
    public boolean isLegalExpression() {
        return legal_exp;
    }
    public String getExpressionAfterParser() {
        return expression;
    }
    public String getCommand() {
        return command;
    }
    public Map<String, Double> getCommandsContent() {
        return cmds;
    }
    private void toUseParser(String input) {
        if (input.charAt(0) == '!') { // is command
            is_cmd = true;
            Matcher legal = legalCommand.matcher(input);
            if (legal.find()) { // judge legal command or not
                legal_cmd = legal.group().equals(input);
            }
            if (legal_cmd) { //if legal
                Matcher which_cmd = whatCommand.matcher(input);
                Matcher  contents = cmdsContent.matcher(input);
                if (which_cmd.find()) {
                    command = which_cmd.group(); // find command
                }
                while (contents.find()) { // find the contents in this command
                    String content = contents.group();
                    Matcher unknown = findUnknown.matcher(content);
                    Matcher  number = findNumber.matcher(content);
                    if (unknown.find() && number.find()) { // put all the contents in the map
                        cmds.put(unknown.group(), Double.parseDouble(number.group()));
                    }
                }
            }
        } else { // is expression
            expression = removeParentheses(input);
        }
    }

    private void legalExpression(String input) {
        Matcher legal1 = divideZreo.matcher(input);
        if (legal1.find()) {
            legal_exp = false;
            System.out.print(" Can not divide zero");
            return;
        }
        Matcher legal2 = legalExpression.matcher(input);
        legal_exp = !legal2.find();
    }
    private String removeParentheses(String input) {
        return input;
    } // not write

}


