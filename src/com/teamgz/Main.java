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

import com.teamgz.expression.*;
import com.teamgz.parser.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
public class Main {
    private static String expression = ""; // save the input, when input a command, deal it

    private void Method() {
    	
    }
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
                            if (result != null) {
                            	System.out.println(" " + result);
                            }
                            break;
                        default: // derivative
                            if (expression.length() == 0) {
                                System.out.println(" Error! No Expression exist");
                                break;
                            }
                            String tmp = derivative(expression, cmd.charAt(cmd.length() - 1));
                            if (tmp != null) {
                                System.out.println(tmp);
                            }
                    }
                } else { // is expression
                    System.out.println(" Error! A Bad Command.");
                }
            } else {
                if (ip.isLegalExpression()) { //judge legal or not, i do not write this func, always return true now
                    System.out.println(" " + input);
                    expression = expression(input);
                } else {
                    System.out.println(" Error! A Bad Expression.");
                }
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
                    if (legal1.find()) {
                    	return " Can not divide zero. Error, Please check";
                    }
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
        if (exp_tmp.replaceAll("-?\\d\\.?\\d*", "").equals("")) {
        	return "0";
        }
        if (!exp_tmp.contains(String.valueOf(X))) { // there is not X
            return "0";
        } else {
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

