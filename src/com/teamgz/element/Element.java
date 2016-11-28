package com.teamgz.element;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by me on 16-11-8.
 */
public class Element {
    private double nums = 1;
    private boolean negative = false;
    private Pattern matchNumberForMulti = Pattern.compile("\\d+\\.?\\d*");
    private Pattern matchNumberPowerForMulti = Pattern.compile("(\\d+\\.?\\d*)\\^(\\d+)");
    private Pattern matchNumberForDivide = Pattern.compile("/\\d+\\.?\\d*");
    private Pattern matchNumberPowerForDivide = Pattern.compile("/(\\d+\\.?\\d*)\\^(\\d+)");
    private Pattern matchUnknown = Pattern.compile("[a-zA-Z]+");
    private Pattern matchXForMutil = Pattern.compile("[a-zA-Z]");
    private Pattern matchXForDivide = Pattern.compile("/[a-zA-Z]");
    private Pattern matchXPowerForMutil = Pattern.compile("[a-zA-Z]\\^\\d+");
    private Pattern matchXPowerForDivide = Pattern.compile("/[a-zA-Z]\\^\\d+");
    private Map<Character, Integer> multi_map = new HashMap<>();
    private Map<Character, Integer> divide_map = new HashMap<>();

    public Element(String str, char X) {
        try {
            if (str.charAt(0) == '-') {
                this.negative = true;
            }
            formatElement(str);
            formatMap();
            toDerivative(X);
        } catch (Exception e) {
            System.out.println("Error! line around 236 " + e);
        }
    }
    public String getElement() {

        if (nums == 0) {
            return "0";
        }
        String result = "";
        if (negative) {
            result = "-";
        }

        if (multi_map.size() == 0 && divide_map.size() == 0) { // there is not any known var
            result += nums;
        } else { // there are some vars
            if (nums != 1) { // need add nums
                result += nums;
                if (multi_map.size() != 0) {
                    result += '*';
                } else {
                    result += '/';
                }
            }
            for (Map.Entry<Character, Integer> entry : multi_map.entrySet()) {
                if (entry.getValue() > 0) {
                    result += entry.getKey();
                }
                if (entry.getValue() > 1) {
                    result +=  "^" + entry.getValue();
                }
                if (entry.getValue() > 0) {
                    result += '*';
                }
            }

            if (multi_map.size() != 0 && result.length() != 0) {
                result = result.substring(0, result.length() - 1);
            }
            if(multi_map.size() == 0 && nums != 1) {
                result = result.substring(0, result.length() - 1);
            }
            for (Map.Entry<Character, Integer> entry : divide_map.entrySet()) {
                result += '/';
                if (entry.getValue() > 0) {
                    result += entry.getKey();
                }
                if (entry.getValue() > 1) {
                    result += "^" + entry.getValue();
                }
            }
        }
        return result;
    }
    private void toDerivative(char X) {
        if (X != '#') {
            if (multi_map.containsKey(X)) {
                nums *= multi_map.get(X);
                multi_map.put(X, multi_map.get(X) - 1);
            }
            if (divide_map.containsKey(X)) {
                nums *= divide_map.get(X);
                negative = !negative;
                divide_map.put(X, divide_map.get(X) + 1);
            }
            if (!multi_map.containsKey(X) && !divide_map.containsKey(X)) {
                multi_map.clear();
                divide_map.clear();
                nums = 0;
            }
        }
    }
    private void formatElement(String str) {
        // deal /x^n first
        try {
            Matcher power_for_divide = matchXPowerForDivide.matcher(str);
            while (power_for_divide.find()) {
                String re = putIntoMap(divide_map, power_for_divide.group(), "powerdevide");
                str = str.replaceAll(re, "");
            }
        } catch (Exception e) {
            System.out.println("Error1 " + e);
        }
        // deal x^n
        try {
            Matcher power_for_mutil = matchXPowerForMutil.matcher(str);
            while (power_for_mutil.find()) {
                String re = putIntoMap(multi_map, power_for_mutil.group(), "powermulti");
                str = str.replaceAll(re, "");
            }
        } catch (Exception e) {
            System.out.println("Error2 " + e);
        }
        //deal /number^n
        try {
            Matcher numbers = matchNumberPowerForDivide.matcher(str);
            while (numbers.find()) {
                String s1 = numbers.group(1);
                String s2 = numbers.group(2);
                this.nums /= Math.pow(Double.parseDouble(s1),
                        Integer.parseInt(s2));
                str = str.replaceAll("/" + s1 + "\\" + "^" + s2, "");
            }
        } catch (Exception e) {
            System.out.println("Error3 " + e);
        }
        //deal *number^n
        try {
            Matcher numbers = matchNumberPowerForMulti.matcher(str);
            while (numbers.find()) {
                String s1 = numbers.group(1);
                String s2 = numbers.group(2);
                this.nums *= Math.pow(Double.parseDouble(s1),
                        Integer.parseInt(s2));
                str = str.replaceAll(s1 + "\\" + "^" + s2, "");
            }
        } catch (Exception e) {
            System.out.println("Error4 " + e);
        }
        // deal /number
        try {
            Matcher numbers = matchNumberForDivide.matcher(str);
            while (numbers.find()) {
                String ds = numbers.group();
                this.nums /= Double.parseDouble(ds.substring(1, ds.length()));
                str = str.replaceAll(numbers.group(), "");
            }
        } catch (Exception e) {
            System.out.println("Error5 " + e);
        }
        // deal *number
        try {
            Matcher numbers = matchNumberForMulti.matcher(str);
            while (numbers.find()) {
                this.nums *= Double.parseDouble(numbers.group());
            }
        } catch (Exception e) {
            System.out.println("Error6 " + e);
        }
        // deal /x
        try {
            Matcher divide = matchXForDivide.matcher(str);
            while (divide.find()) {
                String re = putIntoMap(divide_map, divide.group(), "divide");
                str = str.replaceAll(re, "");
            }
        } catch (Exception e) {
            System.out.println("Error7 " + e);
        }
        // deal *x
        try {
            Matcher mutil = matchXForMutil.matcher(str);
            while (mutil.find()) {
                String re = putIntoMap(multi_map, mutil.group(), "mutil");
                str = str.replaceAll(re, "");
            }
        } catch (Exception e) {
            System.out.println("Error8 " + e);
        }
    }
    private String putIntoMap(Map<Character, Integer> map, String p, String type) {
        Matcher find_number = matchNumberForMulti.matcher(p);
        Matcher find_unknown = matchUnknown.matcher(p);
        char X = ' ';
        while (find_unknown.find()) {
            X = find_unknown.group().charAt(0);
        }
        int times = 1;
        if (!type.equals("divide") && !type.equals("mutil")) {
            while (find_number.find()) {
                times = Integer.parseInt(find_number.group());
            }
        } else {
            times = 1;
        }

        if (map.containsKey(X)) {
            map.put(X, map.get(X) + times);
        } else {
            map.put(X, times);
        }
        String result = "";
        switch (type) {
            case "powerdevide":
                result = "/" + X + "\\" + "^" + times;
                break;
            case "powermulti":
                result = X + "\\" + "^" + times;
                break;
            case "divide":
                result = "/" + X;
                break;
            case "multi":
                result += X;
                break;
            default:
                //;
        }
        return result;
    }
    private void formatMap() {
        Map<Character, Integer> tmp = new HashMap<>();
        for (Map.Entry<Character, Integer> entry : multi_map.entrySet()) {
            char key = entry.getKey();
            int value = entry.getValue();
            if (divide_map.containsKey(key)) {
                if (divide_map.get(key) > value) {
                    divide_map.put(key, divide_map.get(key) - value);
                } else if (divide_map.get(key) < value) {
                    tmp.put(key, value - divide_map.get(key));
                    divide_map.remove(key);
                } else {
                    divide_map.remove(key);
                }
            } else {
                tmp.put(key, value);
            }
        }
        multi_map = tmp;
    }
}

