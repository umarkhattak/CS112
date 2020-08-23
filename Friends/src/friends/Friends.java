package friends;

import java.util.ArrayList;
import structures.Queue;
import structures.Stack;
//import java.util.*;
//imports added
public class Friends {

public static void main (String[] args) {
	
}
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		ArrayList<String> aL1 = new ArrayList<>();

		if (g == null || p1 == null || p2 == null || p1.length() == 0 || p2.length() == 0) {
  		 	return null;
		}

		p1 = p1.toLowerCase();
		p2 = p2.toLowerCase();
		//System.out.print(");
		if (p1.equals(p2)) {
   		aL1.add(g.members[g.map.get(p1)].name);
   		return aL1;
		}

		if (g.map.get(p1) == null || g.map.get(p2) == null) {
   			return null;
		}

		Queue<Integer> q = new Queue<>();
		boolean[] array3 = new boolean[g.members.length];
		int[] array1 = new int[g.members.length];
		int[] array2 = new int[g.members.length];
		 
		for (int i = 0; i < array3.length; i++) {
			array2[i] = -1;
   			array3[i] = false;
   			array1[i] = Integer.MAX_VALUE; 
		}

		int int1 = g.map.get(p1);
		Person person1 = g.members[int1];
		array1[int1] = 0; 
		array3[int1] = true;
		q.enqueue(int1);

		while (!q.isEmpty()) {
			int a = q.dequeue(); 
			Person person2 = g.members[a];

   			for (Friend friendForloop = person2.first; friendForloop != null; friendForloop = friendForloop.next) {
    			int fnum = friendForloop.fnum;

			    if (!array3[fnum]) {
			     array1[fnum] = array1[a] + 1; 
			     array2[fnum] = a;
    			 array3[fnum] = true;
    			 q.enqueue(fnum); 
   				}
   			}
		}

		Stack<String> stack1 = new Stack<>();
		int stackInt = g.map.get(p2);

		if (!array3[stackInt]) {
   			System.out.println("Cannot reach!");
  			return null;
		}

		while (stackInt != -1) {
   			stack1.push(g.members[stackInt].name);
   			stackInt = array2[stackInt];
		}

		while (!stack1.isEmpty()) {
   			aL1.add(stack1.pop());
		}

		return aL1;
	}

	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		if (g == null || school == null || school.length() == 0) {
   			return null;
		}

		school = school.toLowerCase();
		boolean[] array3 = new boolean[g.members.length];
		
		for (int i = 0; i < array3.length; i++) {
  			array3[i] = false;
		}

		for (Person member : g.members) {
			if (!array3[g.map.get(member.name)] && member.school != null && member.school.equals(school)) {

			    Queue<Integer> q = new Queue<>();
    			ArrayList<String> clique = new ArrayList<>();

			    int int1 = g.map.get(member.name);
    			array3[int1] = true;

			    q.enqueue(int1);
    			clique.add(member.name);

    			while (!q.isEmpty()) {
			    	int a = q.dequeue(); 
     				Person person2 = g.members[a];

				    for (Friend friendForloop = person2.first; friendForloop != null; friendForloop = friendForloop.next) {
        				int fnum = friendForloop.fnum;
      					Person personP = g.members[fnum];

        				if (!array3[fnum] && personP.school != null && personP.school.equals(school)) {
        					array3[fnum] = true;
       						q.enqueue(fnum);
       						clique.add(g.members[fnum].name);
      					}
     				}
    			}
    			result.add(clique);
   			}
		}

		return result;
	}

	public static ArrayList<String> connectors(Graph g) {
		boolean[] array3 = new boolean[g.members.length];
		int[] tbhIdk = new int[g.members.length];
		int[] b = new int[g.members.length];
		ArrayList<String> result = new ArrayList<>();

		for (Person member : g.members) {
   			if (!array3[g.map.get(member.name)]) {
    			tbhIdk = new int[g.members.length];
    			DFS(g.map.get(member.name), g.map.get(member.name), g, array3, tbhIdk, b, result);
   			}
		}

		for (int i = 0; i < result.size(); i++) {
  			Friend friendForloop = g.members[g.map.get(result.get(i))].first;
   			int counter = 0;

   			while (friendForloop != null) {
    			friendForloop = friendForloop.next;
    			counter++;
   			}

		   if (counter == 0 || counter == 1) {
    			result.remove(i);
   			}
		}

		for (Person member : g.members) {
   			if ((member.first.next == null && !result.contains(g.members[member.first.fnum].name))) {
    			result.add(g.members[member.first.fnum].name);
   			}
		}
		return result;
	}
	
	private static void DFS(int a, int start, Graph g, boolean[] array3, int[] tbhIdk, int[] b, ArrayList<String> result) {
		Person person2 = g.members[a];
		array3[g.map.get(person2.name)] = true;
		int counter = help2(tbhIdk) + 1;
		if (tbhIdk[a] == 0 && b[a] == 0) {
   			tbhIdk[a] = counter;
   			b[a] = tbhIdk[a];
		}
		for (Friend friendForloop = person2.first; friendForloop != null; friendForloop = friendForloop.next) {
		   if (!array3[friendForloop.fnum]) {

			    DFS(friendForloop.fnum, start, g, array3, tbhIdk, b, result);
    			if (tbhIdk[a] > b[friendForloop.fnum]) {
				    b[a] = Math.min(b[a], b[friendForloop.fnum]);
    			} 
    			else {
     				if (Math.abs(tbhIdk[a] - b[friendForloop.fnum]) < 1 && Math.abs(tbhIdk[a] - tbhIdk[friendForloop.fnum]) <= 1 && b[friendForloop.fnum] == 1 && a == start) {
  				      continue;
     				}
    				if (tbhIdk[a] <= b[friendForloop.fnum] && (a != start || b[friendForloop.fnum] == 1)) { // not the startng point
      					if (!result.contains(g.members[a].name)) {
       						result.add(g.members[a].name);
      					}
     				}	
   				}
   			} 
   			else {
    			b[a] = Math.min(b[a], tbhIdk[friendForloop.fnum]);
   			}
		}
		return;
	}
	
	private static int help2(int[] array) {
		int counter = 0;
		for (int i = 0; i < array.length; i++) {
    		if (array[i] != 0) {
    			counter++;
   			}
		}
		return counter;
	}
}


