/**
 * ********************************************************************************************************
 * 作者：高文成 1140310303
 * 作者：张东昌 1140310304
 * ****************************************************************************************************
 * 程序结构：
 * class Main        -> 程序入口，实现对所有的功能的管理
 * class InputParser -> 对输入字符串进行解析：判断是命令还是表达式，并且对其进行合法性判断
 * class Expression  -> 对表达式整体进行规格化：通过对表达式中‘+’进行拆分为多个元素，这些元素在Element类中进行处理
 * 				最后将处理后的元素合并为最终结果
 * class Element     -> 对输入的元素进行规格化：将元素形如4*7*x*y*x*x^3/x/z整理为28*x^4*y/z，其中对于求导命令也在这一步进行。
 * ******************************************************************************************************
 * 输入要求：
 * 输入：String类型
 * Expression：不要出现空格，不能出现括号，可以计算小数，以下运算符为合法：+，-，*，/，^，.。
 * 			注意可以出现形如2^2，x^2，注意幂次需要为正整数，尽量输入合法运算表达式。
 * 命令：仅仅允许以下格式：
 * 			!q  退出
 * 			!help  获得帮助
 * 			!d/dvar  针对var求导其中var为单个变量范围为[a-zA-Z]
 * 			!simplify [var = number...]  化简，[]内为可选内容，多个赋值表达式之间以空格分开可以多个空格，变量不存在报错
 *
 * ****************************************************************************************************************
 */

package com.teamgz;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
public class Main {
    private static String expression =""; // save the input, when input a command, deal it
    public static void main(String[] args) {
        System.out.println(" Welcome!");
        printHelpDoc();
        label:
        while (true) { // start
            System.out.print(">");
            Scanner in = new Scanner(System.in); // get input
            String input = in.nextLine().trim(); // trim the string
            if (input.length() == 0) {
                System.out.println(" Please input something!");
                continue;
            }
            InputParser ip = new InputParser(input);

            if (ip.isCommand()) { // is command
                if (ip.isLegalCommand()) {
                    String cmd = ip.getCommand();
                    switch (cmd) {
                        case "q": // quit
                            break label;
                        case "help": // help
                            printHelpDoc();
                            break;
                        case "simplify": //simplify
                            if (expression.length() == 0) {
                                System.out.println(" Error! No Expression exist");
                                break;
                            }
                            String result = simplify(expression, ip.getCommandsContent());
                            if (result != null) System.out.println(" " + result);
                            break;
                        default: // derivative
                            if (expression.length() == 0) {
                                System.out.println(" Error! No Expression exist");
                                break;
                            }
                            String tmp = derivative(expression, cmd.charAt(cmd.length() - 1));
                            if (tmp != null)
                                System.out.println(tmp);
                    }
                } else { // is expression
                    System.out.println(" Error! A Bad Command.");
                }
            } else {
                if (ip.isLegalExpression()) { //judge legal or not, i do not write this func, always return true now
                    System.out.println(" " + input);
                    expression = expression(input);
                } else
                    System.out.println(" Error! A Bad Expression.");
            }

        }
        System.out.println("Bye-bye! See You Then");
    }

    public static String expression(String exp) {
        InputParser ip = new InputParser(exp);
        return ip.getExpressionAfterParser();
    }
    public static String simplify(String exp_tmp, Map<String, Double> contents) {
        if (contents.size() != 0) { // use var to simplify
        	exp_tmp = (new Expression(exp_tmp, '#')).getExpression();
            for (Map.Entry<String, Double> entry : contents.entrySet()) {
                if (!exp_tmp.contains(entry.getKey())) {
                    System.out.println(" Error! There Is Not A Variable " + entry.getKey());
                    return null;
                } else {
                    String value = "" + entry.getValue();
                    exp_tmp = exp_tmp.replaceAll(entry.getKey(), value);
                    Pattern divideZreo = Pattern.compile("/[ ]*0[^.|]+|/[ ]*0$|/[ ]*0\\.0$|/[ ]*0\\.0[^\\d]");
                    Matcher legal1 = divideZreo.matcher(exp_tmp);
                    if (legal1.find()) return " Can not divide zero. Error, Please check";
                }
            }

            return (new Expression(exp_tmp, '#')).getExpression();
        } else { // just simplify with no var
            return (new Expression(exp_tmp, '#')).getExpression();
        }
    }
    public static String derivative(String exp_tmp, char X) {
        exp_tmp = simplify(exp_tmp, new HashMap<>()); // simplify first
        exp_tmp = exp_tmp.replaceAll("\\+-", "-"); // "+-" will cause false
        if(exp_tmp.replaceAll("-?\\d\\.?\\d*", "").equals("")) return "0";
        if (!exp_tmp.contains(String.valueOf(X))){ // there is not X
            return "0";
        }
        else {
            exp_tmp = (new Expression(exp_tmp, X)).getExpression();
        }
        return exp_tmp;
    }
    private static void printHelpDoc() {
        System.out.println("--------------------------------------------");
        System.out.println("USAGE:");
        System.out.println("usage1:   expression");
        System.out.println("usage2:   !cmd [var=value] [var1=value1] ...");
        System.out.println("--------------------------------------------");
        System.out.println("CMD:");
        System.out.println("   q        -> quit the program");
        System.out.println("   help     -> to print this menu again");
        System.out.println("   simplify -> to simplify the expression");
        System.out.println("   d/dvar   -> to get derivative (var is the var in the expression)");
        System.out.println("--------------------------------------------");
    }
}

/**
 * input the expression string and format it
 * judge legal or not // not write
 * remove the parentheses
 */
class InputParser {
    private boolean is_cmd = false;
    private String expression = "";
    private String command = "";
    private boolean legal_cmd = false;
    private boolean legal_exp = true;
    private Map<String, Double> cmds = new HashMap<>();
    private Pattern divideZreo = Pattern.compile("/[ ]*0[^.|]+|/[ ]*0$|/[ ]*0\\.0$|/[ ]*0\\.0[^\\d]");
    private Pattern legalCommand = Pattern.compile("^!\\s?(simplify|d/d[a-zA-Z]|q|help)+(\\s?[a-zA-Z]\\s?=\\s?\\d\\s?)*"); // judge the command
    private Pattern legalExpression = Pattern.compile("[^/+*a-zA-Z0-9.^-]");
    private Pattern whatCommand = Pattern.compile("(simplify|d/d[a-zA-Z]|q|help)");
    private Pattern cmdsContent = Pattern.compile("[a-zA-Z]\\s?=\\s?\\d");
    private Pattern findUnknown = Pattern.compile("[a-zA-Z]");
    private Pattern findNumber = Pattern.compile("\\d");
    InputParser(String input) {
        input = input.trim();
        toUseParser(input);
        legalExpression(input);
    }
    boolean isCommand() { return is_cmd; }
    boolean isLegalCommand() { return legal_cmd; }
    boolean isLegalExpression() { return legal_exp; }
    String getExpressionAfterParser() { return expression; }
    String getCommand() { return command; }
    Map<String, Double> getCommandsContent() { return cmds; }
    private void toUseParser(String input) {
        if (input.charAt(0) == '!') { // is command
            is_cmd = true;
            Matcher  legal = legalCommand.matcher(input);
            if (legal.find()) { // judge legal command or not
                legal_cmd = legal.group().equals(input);
            }
            if (legal_cmd) { //if legal
                Matcher which_cmd = whatCommand.matcher(input);
                Matcher  contents = cmdsContent.matcher(input);
                if (which_cmd.find()) command = which_cmd.group(); // find command
                while (contents.find()) { // find the contents in this command
                    String content = contents.group();
                    Matcher unknown = findUnknown.matcher(content);
                    Matcher  number = findNumber.matcher(content);
                    if (unknown.find() && number.find()) // put all the contents in the map
                        cmds.put(unknown.group(), Double.parseDouble(number.group()));
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
    private String removeParentheses(String input) {return input;} // not write

}

/**
 * a class deal exp string
 * input exp and a char X
 * when just simplify X should be '#'
 * when cmd is !d/dvar X should be var
 */
class Expression {
    private char X = '#';
    private Pattern matchNumber = Pattern.compile("^-?\\d+\\.?\\d*");
    private String[] elements;
    private Map<String, Double> map = new HashMap<>();
    Expression(String exp, char X) {
        exp = exp.replaceAll("-", "+-");
        if (exp.charAt(0) == '+')
            exp = exp.substring(1, exp.length());
        elements = exp.split("\\+");
        this.X = X;
        formatExp();
    }
    String getExpression() { // return the expression after simplify
        String expression = "";
        if (map.containsKey("numbers") && map.get("numbers") != 0)
            expression += map.get("numbers");
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (!entry.getKey().equals("numbers") && entry.getValue() != 0) {
                if (!expression.equals("") && entry.getKey().charAt(0) != '-')
                    expression += '+';
                if (entry.getValue() != 1 && entry.getKey().charAt(0) != '/')
                    expression += entry.getValue() + "*" + entry.getKey();
                else if (entry.getValue() != 1)
                    expression += entry.getValue() + entry.getKey();
                else if (entry.getValue() == 1 && entry.getKey().charAt(0) == '/')
                    expression += "1" + entry.getKey();
                else
                    expression += entry.getKey();
            }
        }
        String tmp = expression.replaceAll("\\+-", "-");
        if (tmp.trim().equals("")) tmp = "0";
        return tmp;
    }
    private void formatExp() { // format the input
        for (String ele : elements) { // for every "-x*y/z^n"
            String s = (new Element(ele, X)).getElement();

            if (s.equals("0")) continue;
            double value = 1;
            try { //find number

                Matcher number = matchNumber.matcher(s);
                if (number.find()) {
                    value = Double.parseDouble(number.group());

                    s = s.replaceAll(number.group(), "");
                }
                else if (s.length() > 0 && s.charAt(0) == '-') {
                    value = -1;
                    s = s.substring(1, s.length());
                }
            } catch(Exception e) {
                System.out.println("FindNumberError " + e);
            }
            if (s.length() != 0) { // have unknown
                if (s.charAt(0) == '*')
                    s = s.substring(1, s.length());
                if (map.containsKey(s)) {
                    map.put(s, map.get(s) + value);
                }
                else {
                    map.put(s, value);
                }
            }
            else { // just number
                if (map.containsKey("numbers")) {

                    map.put("numbers", map.get("numbers") + value);
                } else {
                    map.put("numbers", value);
                }
            }
        }
    }

}
class Element {
    private double nums = 1;
    private boolean negative = false;
    private Pattern matchNumberForMulti = Pattern.compile("\\d+\\.?\\d*");
    private Pattern matchNumberPowerForMulti = Pattern.compile("(\\d+\\.?\\d*)\\^(\\d+)");
    private Pattern matchNumberForDivide = Pattern.compile("/\\d+\\.?\\d*");
    private Pattern matchNumberPowerForDivide = Pattern.compile("/(\\d+\\.?\\d*)\\^(\\d+)");
    private Pattern matchUnknown = Pattern.compile("[a-zA-Z]+");
    private Pattern matchXForMutil = Pattern.compile("[a-zA-Z]");
    private Pattern matchXForDivide = Pattern.compile("/[a-zA-Z]");
    private Pattern matchXPowerForMutil = Pattern.compile("[a-zA-Z]+\\^\\d+");
    private Pattern matchXPowerForDivide = Pattern.compile("/[a-zA-Z]+\\^\\d+");
    private Map<Character, Integer> multi_map = new HashMap<>();
    private Map<Character, Integer> divide_map = new HashMap<>();

    Element(String str, char X) {
        try {
            if (str.charAt(0) == '-')
                this.negative = true;
            formatElement(str);
            formatMap();
            toDerivative(X);
        } catch (Exception e) {
            System.out.println("Error! line around 236 " + e);
        }
    }
    String getElement() {

        if (nums == 0) return "0";
        String result = "";
        if (negative) result = "-";

        if (multi_map.size() == 0 && divide_map.size() == 0) { // there is not any known var
            result += nums;
        } else { // there are some vars
            if (nums != 1) { // need add nums
                result += nums;
                if (multi_map.size() != 0)
                    result += '*';
                else
                    result += '/';
            }
            for (Map.Entry<Character, Integer> entry : multi_map.entrySet()) {
                if (entry.getValue() > 0)
                    result += entry.getKey();
                if (entry.getValue() > 1)
                    result +=  "^" + entry.getValue();
                if (entry.getValue() > 0)
                    result += '*';
            }
            if (multi_map.size() != 0 && result.length() != 0)
                result = result.substring(0, result.length() - 1);
            for (Map.Entry<Character, Integer> entry : divide_map.entrySet()) {
                if (multi_map.size() != 0 || result.equals("") || result.equals("-"))
                    result += '/';
                if (entry.getValue() > 0)
                    result += entry.getKey();
                if (entry.getValue() > 1)
                    result += "^" + entry.getValue();
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
            if (!multi_map.containsKey(X) &&
                    !divide_map.containsKey(X)) {
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
        } catch(Exception e) {
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
            System.out.println("Error6 " + e );
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
        if (!type.equals("divide") && !type.equals("mutil"))
            while (find_number.find()) {
                times = Integer.parseInt(find_number.group());
        } else
            times = 1;

        if (map.containsKey(X))
            map.put(X, map.get(X) + times);
        else
            map.put(X, times);
        String result = "";
        switch (type) {
            case "powerdevide":
                result = "/" + X + "\\" +"^" + times;
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
