package com.ruffneck.calculator.rpn;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 逆波兰表达式
 *
 * @author runing
 */
public class RPN {


    /**
     * 是否为算术表达式
     *
     * @param str
     * @return
     */
    public static boolean isExpression(String str) {
        int flag = 0;
        for (int i = 0; i < str.length() - 1; i++) {
            char ch = str.charAt(i);
            char chb = str.charAt(i + 1);
            //首尾不是数字
            if (((!isNum(ch) && i == 0) && ch != '(')
                    || (!isNum(chb) && (i == str.length() - 2) && chb != ')')) {
//                System.out.println("首尾不是数字---->" + ch + chb);
                return false;
            }
            //小数点前后不是数字
            if ((ch == '.' && !isNum(chb)) || (!isNum(ch) && chb == '.')) {
//                System.out.println("小数点前后不是数字--->" + ch + chb);
                return false;
            }
            //运算符后不是数字
            if (isOperator(ch) && !isNum(chb) && chb != '(') {
//                System.out.println("运算符后不是数字--->" + ch + chb);
                return false;
            }
            //数字后不是运算符
            if (isNum(ch) && !isOperator(chb) && chb != '.' && chb != ')'
                    && !isNum(chb)) {
//                System.out.println("数字后不是运算符--->" + ch + chb);
                return false;
            }
            if (ch == '(') {
                flag++;
            }
            if (chb == ')') {
                flag--;
            }
        }
        //括号不匹配
        if (flag != 0) {
//            System.out.println("括号不匹配--->");
            return false;
        }
        return true;
    }


    /**
     * 字符是否为数字
     *
     * @param ch
     * @return
     */
    public static boolean isNum(char ch) {
        if (ch <= '9' && ch >= '0') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串是否为Double类型
     *
     * @param s
     * @return
     */
    private static boolean isDouble(String s) {
        try {
            Double.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 字符是否为运输符
     *
     * @param ch
     * @return
     */
    private static boolean isOperator(char ch) {
        if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=') {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 字符串是否为运算符
     *
     * @param s
     * @return
     */
    private static boolean isStrOperator(String s) {
        if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")
                || s.equals("(") || s.equals(")")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 比较运算符优先级
     *
     * @param o1
     * @param o2
     * @return
     */
    private static boolean heightOperator(String o1, String o2) {
        if ((o1.equals("*") || o1.equals("/"))
                && (o2.equals("+") || o2.equals("-")) || o2.equals("(")) {
            return true;
        } else if ((o1.equals("+") || o1.equals("-"))
                && (o2.equals("*") || o2.equals("/"))) {
            return false;
        } else if ((o1.equals("*") || o1.equals("/"))
                && ((o2.equals("*") || o2.equals("/")))) {
            return true;
        } else if ((o1.equals("+") || o1.equals("-"))
                && (o2.equals("+") || o2.equals("-"))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 两数进行运算
     *
     * @param oper
     * @param num1
     * @param num2
     * @return
     */
    private static double getCountResult(String oper, double num1, double num2) {
        if (oper.equals("+")) {
            return num1 + num2;
        } else if (oper.equals("-")) {
            return num1 - num2;
        } else if (oper.equals("*")) {
            return num1 * num2;
        } else if (oper.equals("/")) {
            return num1 / num2;
        } else {
            return 0;
        }
    }

    /**
     * 中缀表达式转换后缀
     *
     * @param list
     * @return
     */
    private static List<String> nifix_to_post(List<String> list) {
        Stack<String> stack = new Stack<>();
        List<String> plist = new ArrayList<>();
        for (String str : list) {
            //如果是数字就加进集合中
            if (isDouble(str)) {
                plist.add(str);
            }
            //如果栈是空的则运算符直接入栈
            if (isStrOperator(str) && stack.isEmpty()) {
                stack.push(str);
            } else if (isStrOperator(str) && !stack.isEmpty()) {
                //读取栈中的最后一个元素(也可以用stack.lastElement().)
                String last = stack.peek();
                //栈顶元素与运算符作比较.如果是运算符是'('或者  str(运算符)比栈顶元素(last)大,则把运算符加进栈中
                if (heightOperator(str, last) || str.equals("(")) {
                    stack.push(str);
                }
                //如果str(运算符)比栈顶元素(last)小,而且不为')',则循环把栈中的元素取出来,直到到'('为止.
                else if (!heightOperator(str, last) && !str.equals(")")) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        plist.add(stack.pop());
                    }
                    //取出后再把运算符加进栈中
                    stack.push(str);
                }
                //如果str是')',则也依次取出,直到出现'('为止.
                else if (str.equals(")")) {
                    while (!stack.isEmpty()) {
                        String pop = stack.pop();
                        //如果栈顶元素不是'('就加进list中,如果是'('则跳出循环.
                        if (!pop.equals("(")) {
                            plist.add(pop);
                        }else break;
                    }
                }
            }
//            System.out.println("stack = " + stack + ",list = " + plist);
        }
        while (!stack.isEmpty()) {
            plist.add(stack.pop());
        }
//		for (String pl : plist) {
//			System.out.println(pl);
//		}
        return plist;
    }

    /**
     * 计算后缀表达式
     *
     * @param list
     * @return
     */
    private static double get_postfis_result(List<String> list) {
        //用于进行计算的栈.
        Stack<String> stack = new Stack<>();
        for (String str : list) {
            if (isDouble(str)) {
                stack.push(str);
            } else if (isStrOperator(str)) {
                double n2 = Double.valueOf(stack.pop());
                double n1 = Double.valueOf(stack.pop());
                stack.push("" + getCountResult(str, n1, n2));
            }
//            System.out.println("stack = " + stack);
        }
        return Double.valueOf(stack.pop());
    }

    /**
     * 分解表达式
     *
     * @param str
     * @return
     */
    private static List<String> resolveString(String str) {
        List<String> list = new ArrayList<String>();
        String temp = "";
        for (int i = 0; i < str.length(); i++) {
            final char ch = str.charAt(i);
            if (isNum(ch) || ch == '.') {
                char c = str.charAt(i);
                temp += c;
            } else if (isOperator(ch) || ch == ')') {
                if (!temp.equals("")) {
                    list.add(temp);
                }
                list.add("" + ch);
                temp = "";
            } else if (ch == '(') {
                list.add("" + ch);
            }
            if (i == str.length() - 1) {
                list.add(temp);
            }
        }
        return list;
    }

    /**
     * 计算一个表达式的值
     * @param expression
     * @return
     */
    public static double calculate(String expression){

        List<String> list = resolveString(expression);
//        System.out.println(list);
        list = nifix_to_post(list);
//        System.out.println(list);
        return get_postfis_result(list);
    }


}
