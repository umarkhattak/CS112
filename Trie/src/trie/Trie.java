package trie;

import java.util.ArrayList;

public class Trie {
	
	private Trie() { }

	public static TrieNode buildTrie(String[] allWords) {
		
		TrieNode rt = new TrieNode(null, null, null);
		if(allWords.length == 0)
			return rt;
		
		rt.firstChild = new TrieNode(new Indexes(0,(short)(0), (short)(allWords[0].length() - 1)), null, null);
		TrieNode seenLast = rt.firstChild, pointer = rt.firstChild;
		
		int endIndex = -1;
		int inTo = -1;
		int wordIndex = -1;
		int startIndex = -1;
		
		for(int i = 1; i < allWords.length; i++) {
			String w = allWords[i];
			
			while(pointer != null) {
				wordIndex = pointer.substr.wordIndex;
				endIndex = pointer.substr.endIndex;
				startIndex = pointer.substr.startIndex;
				inTo = helper1(allWords[wordIndex].substring(startIndex, endIndex+1), w.substring(startIndex)); 
				
				if(startIndex > w.length()) {
					seenLast = pointer;
					pointer = pointer.sibling;
					continue;
				}
				
				if(inTo != -1)
					inTo += startIndex;
				
				if(inTo == -1) { 
					seenLast = pointer;
					pointer = pointer.sibling;
				}
				else {
					if (inTo < endIndex){ 
						seenLast = pointer;
						break;
					}
					else if(inTo == endIndex) { 
						seenLast = pointer;
						pointer = pointer.firstChild;
					}
				}
			}
			
			if(pointer == null) {
				Indexes indexes = new Indexes(i, (short)startIndex, (short)(w.length()-1));
				seenLast.sibling = new TrieNode(indexes, null, null);
			} 
			else {
				TrieNode currFirstChild = seenLast.firstChild; 				
				Indexes currIndexes = seenLast.substr; 
				Indexes currWordNewIndexes = new Indexes(currIndexes.wordIndex, (short)(inTo+1), currIndexes.endIndex);
				currIndexes.endIndex = (short)inTo; 
				
				seenLast.firstChild = new TrieNode(currWordNewIndexes, null, null);
				seenLast.firstChild.firstChild = currFirstChild;
				seenLast.firstChild.sibling = new TrieNode(new Indexes((short)i, (short)(inTo+1), (short)(w.length()-1)), null, null);
			}
			
			inTo = startIndex = endIndex = wordIndex = -1;
			pointer = seenLast = rt.firstChild;
		}
		
		return rt;
	}
	
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		if(root == null) 
			return null;
		
		ArrayList<TrieNode> found = new ArrayList<>();
		TrieNode pointer = root;
		
		while(pointer != null) {
			if(pointer.substr == null) 
				pointer = pointer.firstChild;
			
			String s = allWords[pointer.substr.wordIndex];
			String a = s.substring(0, pointer.substr.endIndex+1);
			if(s.startsWith(prefix) || prefix.startsWith(a)) {
				if(pointer.firstChild != null) { 
					found.addAll(completionList(pointer.firstChild, allWords, prefix));
					pointer = pointer.sibling;
				} else { 
					found.add(pointer);
					pointer = pointer.sibling;
				}
			} else {
				pointer = pointer.sibling;
			}
		}
		return found;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
	private static int helper1(String withinTree, String i) {
		int x = 0;
		while(x < withinTree.length() && x < i.length()
				&& withinTree.charAt(x) == i.charAt(x))
			x++;
		
		return (x-1);
	}
 }
