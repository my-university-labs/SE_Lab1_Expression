package com.teamgz.expression;

import com.teamgz.element.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by me on 16-11-8.
 */
/*
 * a class deal exp string
 * input exp and a char X
 * when just simplify X should be '#'
 * when cmd is !d/dvar X should be var
 */
public class Expression {
    private char X = '#';
    private Pattern matchNumber = Pattern.compile("^-?\\d+\\.?\\d*");
    private String[] elements;
    private Map<String, Double> map = new HashMap<>();
    public Expression(String exp, char x) {
        exp = exp.replaceAll("-", "+-");
        if (exp.charAt(0) == '+') {
            exp = exp.substring(1, exp.length());
        }
        elements = exp.split("\\+");
        this.X = x;
        formatExp();
    }
    public String getExpression() { // return the expression after simplify
        String expression = "";
        if (map.containsKey("numbers") && map.get("numbers") != 0) {
            expression += map.get("numbers");
        }
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (!entry.getKey().equals("numbers") && entry.getValue() != 0) {
                if (!expression.equals("") && entry.getKey().charAt(0) != '-') {
                    expression += '+';
                }
                if (entry.getValue() != 1 && entry.getKey().charAt(0) != '/') {
                    expression += entry.getValue() + "*" + entry.getKey();
                } else if (entry.getValue() != 1) {
                    expression += entry.getValue() + entry.getKey();
                } else if (entry.getValue() == 1 && entry.getKey().charAt(0) == '/') {
                    expression += "1" + entry.getKey();
                } else {
                    expression += entry.getKey();
                }
            }
        }
        String tmp = expression.replaceAll("\\+-", "-");
        if (tmp.trim().equals("")) {
            tmp = "0";
        }
        return tmp;
    }
    private void formatExp() { // format the input
        for (String ele : elements) { // for every "-x*y/z^n"
            String s = (new Element(ele, X)).getElement();
            if (s.equals("0")) {
                continue;
            }
            double value = 1;
            try { //find number

                Matcher number = matchNumber.matcher(s);
                if (number.find()) {
                    value = Double.parseDouble(number.group());

                    s = s.replaceAll(number.group(), "");
                } else if (s.length() > 0 && s.charAt(0) == '-') {
                    value = -1;
                    s = s.substring(1, s.length());
                }
            } catch (Exception e) {
                System.out.println("FindNumberError " + e);
            }
            if (s.length() != 0) { // have unknown
                if (s.charAt(0) == '*') {
                    s = s.substring(1, s.length());
                }
                if (map.containsKey(s)) {
                    map.put(s, map.get(s) + value);
                } else {
                    map.put(s, value);
                }
            } else { // just number
                if (map.containsKey("numbers")) {

                    map.put("numbers", map.get("numbers") + value);
                } else {
                    map.put("numbers", value);
                }
            }
        }
    }

}
