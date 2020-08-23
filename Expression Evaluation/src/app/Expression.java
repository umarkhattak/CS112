package app;

import java.io.*;
import java.util.*;
import java.util.regex.*; //do i really have to use this tho

import structures.Stack; //way too much work dont even think about using this

public class Expression {

	public static String delims = " \t*+-/()[]";

	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		expr = expr + " ";
		StringTokenizer str = new StringTokenizer(expr, delims);
		int ref = 0;
		while (str.hasMoreTokens()) {
			String temp = str.nextToken();
			if (!booYa(temp)) {
				if (arrays.contains(new Array(temp)) == false) {
					if (expr.charAt(expr.indexOf(temp, ref) + temp.length()) == '[') { // contains object
						arrays.add(new Array(temp));
						ref = expr.indexOf('[', ref) - 1;
					} else if (vars.contains(new Variable(temp)) == false) {
						vars.add(new Variable(temp));
					}
				}
			}
		}
	}

	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		expr=expressionFixer(expr.replaceAll("\\s+",""));
		System.out.println(expr);
		float output = 0;

		if (expr.contains("[")) {

			int indexStart = 0;
			int indexEnd = 0;
			int counter = 0;
			
			for (int a = 0; a < expr.length(); a++) {

				if (expr.charAt(a) == '[') {
					if (counter == 0) {
						indexStart = a;
					}
					counter++;
				}
				
				if (expr.charAt(a) == ']') {
					indexEnd = a;
					counter--;
					
					if (counter == 0) {
						System.out.println(expr.substring(indexStart + 1, indexEnd));
						String arrayName = exprName(expr, indexStart - 1);
						expr = expressionFixer(expr.substring(0, indexStart - arrayName.length()) + arrVal(arrayName, evaluate(expr.substring(indexStart + 1, indexEnd), vars, arrays), arrays) + expr.substring(indexEnd + 1, expr.length()));
						a = 0;
					}
				}
			}
		}
		
		if (expr.contains("(")) {
			int indexStart = 0;
			int indexEnd = 0;
			int counter2 = 0;
			
			for (int a = 0; a < expr.length(); a++) {

				if (expr.charAt(a) == '(') {
					if (counter2 == 0) {
						indexStart = a;
					}
					counter2++;
				}
				
				if (expr.charAt(a) == ')') {
					indexEnd = a;
					counter2--;
					
					if (counter2 == 0) {
						System.out.println(expr.substring(indexStart + 1, indexEnd));
						expr = expressionFixer(expr.substring(0, indexStart) + evaluate(expr.substring(indexStart + 1, indexEnd), vars, arrays) + expr.substring(indexEnd + 1, expr.length()));
						a = 0;
					}
				}
			}
		}

		if (doesContainLetter(expr)) {
			System.out.print(expr);
			String nameOfExpression = "";
			
			for (int i = 0; i < expr.length(); i++) {
				char cr = expr.charAt(i);
				
				if (Character.isLetter(cr)) {
					nameOfExpression += cr;
				}

				if (nameOfExpression != "" && (opr(cr))) {
					expr = expr.substring(0, i - nameOfExpression.length()) + varVal(nameOfExpression, vars) + expr.substring(i, expr.length());
					nameOfExpression = "";
				}

				if (nameOfExpression != "" && i + 1 == expr.length()) {
					expr = expr.substring(0, i - nameOfExpression.length() + 1) + varVal(nameOfExpression, vars) + expr.substring(i, i);
					nameOfExpression = "";
					break;
				}
			}
			
			System.out.println(expr);
		}
		
		if (expr.contains("/") || expr.contains("*")) {
			System.out.println(expr);
			
			for (int i = 0; i < expr.length(); i++) {
				char crc = expr.charAt(i);
				String str1 = "";
				String str2 = "";
				
				if (crc == '/' || crc == '*') {

					int x = i - 1;
					char z = expr.charAt(x);
					boolean negative = false;
					int y = i - 1;
					int count = 0;
					
					while (y >= 0) {
						if (expr.charAt(y) == '-' && count == 0 && y == 0) {
							negative = true;
							break;
						}

						if (opr(expr.charAt(y))) {
							count++;
						}
						y--;
					}

					while ((z != '+' && z != '-' && z != '*' && z != '/') && x >= 0) {

						if (negative) {
							str1 = z + str1;

						} 
						else {
							str1 = z + str1;
						}
						x--;
						
						try {
							z = expr.charAt(x);

						} 
						catch (StringIndexOutOfBoundsException error) {
						}
					}

					int idk = i + 1;
					char e = expr.charAt(idk);

					if (expr.charAt(idk) == '-') {
						idk++;
						str2 += '-';
						e = expr.charAt(idk);
					}

					while ((e != '/' && e != '*' && e != '+' && e != '-') && idk < expr.length()) {
						str2 += e;
						idk++;
						
						try {
							e = expr.charAt(idk);
						} 
						catch (StringIndexOutOfBoundsException error) {
						}
					}

					float float1 = Float.parseFloat(str1);
					float float2 = Float.parseFloat(str2);
					float answer = 0;
					
					if (crc == '/') {
						answer = float1 / float2;
					}
					
					if (crc == '*') {
						answer = float1 * float2;	
					}
					expr = expressionFixer(expr.substring(0, x + 1) +String.format("%.16f",answer) + expr.substring(idk, expr.length()));
					System.out.println(expr + ":  " + float1 + "  */ " + float2);
					str1="";
					str2="";
					i=0;
				}
			}
		}
		
		if (expr.contains("+") || expr.contains("-")) {
			int i = 0;
			String s = "";
			boolean subract = false;
			//int count = 0;
			
			while (i < expr.length()) {
				char c = expr.charAt(i);
				i++;
				
				if (c != '+' && c != '-') {
					s += c;
				}
				
				try {
					
					if(expr.charAt(i-1)=='E') {
						i++;
						//String power = "";
						
						while(i<expr.length()&&expr.charAt(i)!='+'&&expr.charAt(i)!='-') {
							//power+=expr.charAt(i);
							i++;
						}
						//float answer=Float.parseFloat(s)*(float)Math.pow(10,Float.parseFloat(power));
					}
				}
				catch(StringIndexOutOfBoundsException error) {	
				}
				
				if (c == '+' || c == '-' || i == expr.length()) {
					
					if (expr.charAt(0) == '-') {
					}
					
					if (subract) {
						
						try {
							output -= Float.parseFloat(s);
							s = "";
						} 
						catch (NumberFormatException error) {
						}
						s = "";
					} 
					else {
						try {
							output += Float.parseFloat(s);
							s = "";
						} 
						catch (NumberFormatException error) {
						}
					}
				}
				
				if (c == '+') {
					subract = false;
				}

				if (c == '-') {
					subract = true;
				}
			}
			System.out.println(output + " = " + expr);
			
			return output;
		} 
		else
			return Float.parseFloat(expr);
	}

	//Helper Methods
	private static String expressionFixer(String expr) {
		if(expr.contains("E0.0")) {
			expr = expr.replace("E0.0", "");
		}
		if(expr.contains("--")) {
			expr = expr.replace("--", "+");
		}
		if(expr.contains("-+")) {
			expr = expr.replace("-+", "-");
		}
		if(expr.contains("++")) {
			expr = expr.replace("++", "+");
		}
		if(expr.contains("+-")) {
			expr = expr.replace("+-", "-");
		}
		return expr;
	}

	private static void addingVar(String varName, ArrayList<Variable> vars) { //not used anymore

		for (Variable variable : vars) {
			if (variable.name.equals(varName)) {
				return;
			}
		}
		vars.add(new Variable(varName));
	}
	
	private static void arrayAddition(String varName, ArrayList<Array> arrays) { //not used anymore

		for (Array array : arrays) {
			if (array.name.equals(varName)) {
				return;
			}
		}
		arrays.add(new Array(varName));
	}
	
	private static String exprName(String expr, int indexStart) {
		int is = indexStart;
		String varName = "";
		while (is >= 0 && Character.isLetter(expr.charAt(is))) {
			varName = expr.charAt(is) + varName;
			is--;
		}

		return varName;
	}
	
	private static boolean doesContainLetter(String string) {
		for (int x = 0; x < string.length(); x++) {
			if (Character.isLetter(string.charAt(x))) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean opr(char x) {
		if (x == '*' || x == '/' || x == '+' || x == '-') {
			return true;
		}
		return false;
	}

	private static float varVal(String varName, ArrayList<Variable> vars) {
		for (Variable v : vars) {
			if (v.name.equals(varName)) {
				return v.value;
			}
		}

		return 0;
	}

	private static float arrVal(String varName, float index, ArrayList<Array> arrays) {
		for (Array a : arrays) {
			if (a.name.equals(varName)) {
				return a.values[(int) index];
			}
		}

		return 0;
	}
	
	private static boolean booYa(String x) {
		try {
			Float.parseFloat(x);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}