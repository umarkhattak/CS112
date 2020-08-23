package poly;

import java.io.IOException;
import java.util.Scanner;

public class Polynomial {

	public static Node read(Scanner sc) 
	throws IOException { //DO NOT CHANGE METHOD IDIOT
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}

	public static Node add(Node poly1, Node poly2) {

		Node total = null;
		Node endingTotal = null;
		Node one = null;
		Node two = null;

		if (poly1 != null)
			one = new Node(poly1.term.coeff, poly1.term.degree, poly1.next);

		if (poly2 != null)
			two = new Node(poly2.term.coeff, poly2.term.degree, poly2.next);

		while (one != null && two != null) {
			if (one.term.degree == two.term.degree) {
				if (one.term.coeff + two.term.coeff != 0) {
					total = new Node(one.term.coeff + two.term.coeff, one.term.degree, total);
				}
				one = one.next;
				two = two.next;
			} else if (one.term.degree < two.term.degree) {
				if (one.term.coeff != 0)
					total = new Node(one.term.coeff, one.term.degree, total);
				one = one.next;
				} else if (one.term.degree > two.term.degree) {
					if (two.term.coeff != 0)
					total = new Node(two.term.coeff, two.term.degree, total);
				two = two.next;
				}
		} 

		while (one != null) {
			if (one.term.coeff != 0)
				total = new Node(one.term.coeff, one.term.degree, total);
			one = one.next;
		} 

		while (two != null) {
			if (two.term.coeff != 0)
				total = new Node(two.term.coeff, two.term.degree, total);
			two = two.next;
		} 

		while (total != null) {
			endingTotal = new Node(total.term.coeff, total.term.degree, endingTotal);
			total = total.next;
		} 
		return endingTotal;
//		return null;
	}

	public static Node multiply(Node poly1, Node poly2) {
		
		Node product = null;
		Node endingProd = null;
		
		if (poly1 == null || poly2 == null)
			return endingProd;
		
		Node varOne = new Node(poly1.term.coeff, poly1.term.degree, poly1.next);
		Node varTwo = new Node(poly2.term.coeff, poly2.term.degree, poly2.next);
//		return product.next;
		for (varOne = poly1; varOne != null; varOne = varOne.next) {
			for (varTwo = poly2; varTwo != null; varTwo = varTwo.next) {
				if (varOne.term.coeff * varTwo.term.coeff != 0)
					if (helper1(product, varOne.term.degree + varTwo.term.degree, varOne.term.coeff * varTwo.term.coeff) == false) {
						product = new Node(varOne.term.coeff * varTwo.term.coeff, varOne.term.degree + varTwo.term.degree, product);
					}
			}
		} 

		endingProd = helper3(product); 
		
		return endingProd;

//		return null;
	}

	public static float evaluate(Node poly, float x) {
		
		float result = 0;
		
		if (poly == null) {
			return result;
		} else {
			
			result = poly.term.coeff;
			
			for (Node curr = poly.next; curr != null; curr = curr.next) {
				result = (float) (result + (curr.term.coeff * Math.pow(x, curr.term.degree)));
			}
			
			return result;
		}

//		return 0;
	}

	public static String toString(Node poly) { //DO NOT CHANGE METHOD IDIOT
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
	
	private static boolean helper1(Node poly, int y, float value) {
		
		boolean fal = false;
		
		while (poly != null) {
			if (poly.term.degree == y) {
				poly.term.coeff = poly.term.coeff + value;
				fal = true;
				break;
			}
			
			poly = poly.next;
		}
		
		return fal;
	}

	private static boolean helper2(Node poly, int y) {
		
		boolean ohyea = false;
		
		for (Node curr = poly; curr != null; curr = curr.next) {
			if (curr.term.degree == y) {
				
				ohyea = true;
				
				return ohyea;
			}
		}
		
		return ohyea;
	}

	private static Node helper3(Node poly) {
		
		Node result = null;
		Node nodeOne = poly;
		Node nodeTemp = poly;

		for (Node cnt = poly; cnt != null; cnt = cnt.next) {
			for (; poly != null; poly = poly.next) {
				if (nodeTemp.term.degree < poly.term.degree && helper2(result, poly.term.degree) == false) {
					
					nodeTemp = poly;
				}
			}
			
			poly = nodeOne;
			result = new Node(nodeTemp.term.coeff, nodeTemp.term.degree, result);
			nodeTemp = new Node(0, -1, null);
		}
		Node ptr = result.next;
		Node prev = result;
		while (ptr != null) {
			if(ptr.term.coeff == 0)
				prev.next = ptr.next;
			else 
				prev = prev.next;
			ptr = ptr.next;
		}
		return result;
	}
}
