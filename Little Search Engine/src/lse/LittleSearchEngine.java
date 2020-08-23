package lse;

import java.io.*;
import java.util.*;

public class LittleSearchEngine {

	HashMap<String, ArrayList<Occurrence>> keywordsIndex;


	HashSet<String> noiseWords;

	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {

		HashMap<String, Occurrence> hashMap = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		
		while (sc.hasNext()) {
			String str = getKeyword(sc.next());
			
			if (str != null) {
				
				if (hashMap.containsKey(str)) {
					Occurrence temp = hashMap.get(str);
					Occurrence occ = new Occurrence(docFile, temp.frequency + 1);
					hashMap.replace(str, occ);
				} else {
					hashMap.put(str, new Occurrence(docFile, 1));
				}
			}
		}
		
		sc.close();
		System.out.println(hashMap);	
		System.out.println(hashMap.size());	
		
		return hashMap;
	}

	public void mergeKeywords(HashMap<String, Occurrence> kws) {

		for (Map.Entry<String, Occurrence> entry : kws.entrySet()) {
			
			if (keywordsIndex.containsKey(entry.getKey())) {
				
				ArrayList<Occurrence> arrayList = keywordsIndex.get(entry.getKey());
				arrayList.add(entry.getValue());
				insertLastOccurrence(arrayList);
			} else {
				ArrayList<Occurrence> arrayList = new ArrayList<Occurrence>();
				arrayList.add(entry.getValue());
				keywordsIndex.put(entry.getKey(), arrayList);
			}
		}
		
	}

	public String getKeyword(String word) {

		word = helper3(word);
		if (word != null && helper2(word)) {
			word = word.toLowerCase();
			
			for (String temp : noiseWords) {
				
				if (temp.equalsIgnoreCase(word)) {
					return null;
				}
			}
			
			return word;
		} else {
			
			return null;
		}
	}

	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {

		ArrayList<Integer> arrayListInt = new ArrayList<Integer>();
		int insert = helper1(occs, occs.get(occs.size() - 1).frequency, 0, occs.size() - 2, arrayListInt);
		Occurrence occ = occs.get(occs.size() - 1);
		occs.remove(occs.size() - 1);
		occs.add(insert, occ);
		return arrayListInt;
	} //done

	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException { //DO NOT TOUCH THIS METHOD IDIOT
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	} //DO NOT TOUCH

	public ArrayList<String> top5search(String kw1, String kw2) {

		ArrayList<Occurrence> arrayListKw1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> arrayListKw2 = new ArrayList<Occurrence>();

		if (keywordsIndex.get(kw1) != null)
			arrayListKw1 = keywordsIndex.get(kw1);

		if (keywordsIndex.get(kw2) != null)
			arrayListKw2 = keywordsIndex.get(kw2);

		ArrayList<String> strResult = new ArrayList<String>();
		ArrayList<Integer> arrayListInt = new ArrayList<Integer>();
		ArrayList<Occurrence> occOrder = new ArrayList<Occurrence>();
		ArrayList<Occurrence> occCombined = new ArrayList<Occurrence>();
		
		if (arrayListKw1.isEmpty() && arrayListKw2.isEmpty()) {
			
			return null;
			
		} else if (!(arrayListKw1.isEmpty()) && !(arrayListKw2.isEmpty())) { 
			boolean first = false;
			boolean second = false;

			for (Occurrence firstOcc : arrayListKw1) {
				second = false;
				
				for (Occurrence secondOcc : arrayListKw2) {
					
					if (firstOcc.document.equals(secondOcc.document)) {
						second = true;
						
						if (firstOcc.frequency > secondOcc.frequency) {
							occCombined.add(firstOcc);
							arrayListInt.add(1);
						} else {
							occCombined.add(secondOcc);
							arrayListInt.add(2);
						}
					}
				}
				
				if (!second) {
					occCombined.add(firstOcc);
					arrayListInt.add(1);
				}
			}

			for (Occurrence secondOcc : arrayListKw2) {
				first = false;
				
				for (Occurrence firstOcc : arrayListKw2) {
					
					if (secondOcc.document.equals(firstOcc.document)) {
						first = true;
						
						break;
					}
				}
				
				if (!first) {
					occCombined.add(secondOcc);
					arrayListInt.add(2);
				}
			}
		} else if (arrayListKw1.isEmpty()) { 
			
			for (Occurrence firstOcc : arrayListKw2) {
				occCombined.add(firstOcc);
				arrayListInt.add(2);
			}
		} else {
			
			for (Occurrence secondOcc : arrayListKw1) {
				occCombined.add(secondOcc);
				arrayListInt.add(2);
			}
		}
		
		while (occOrder.size() != occCombined.size()) {
			int list = 0;
			int addNum = -1;
			
			for (int i = 0; i < occCombined.size(); i++) {
				Occurrence occNotOrdered = occCombined.get(i);
				
				if (!(occOrder.contains(occNotOrdered))) {
					
					if (occNotOrdered.frequency > list) {
						addNum = i;
						list = occNotOrdered.frequency;
					} else if (occNotOrdered.frequency == list) {
						
						if (arrayListInt.get(i) < arrayListInt.get(addNum)) {
							addNum = i;
						}
					}
				}
			}
			occOrder.add(occCombined.get(addNum));
		}

		while (!(occOrder.isEmpty()) && strResult.size() < 5) {
			Occurrence occr = occOrder.remove(0);
			strResult.add(occr.document);
		}
		
		return strResult;
	}
	
	private int helper1(ArrayList<Occurrence> occ, int value, int beginning, int fin, ArrayList<Integer> arrayListNum) {
		if (beginning > fin) {
			return beginning; 
		}

		int middle = (beginning + fin) / 2;
		arrayListNum.add(middle);

		if (occ.get(middle).frequency == value)
			return middle + 1;
		
		else if (occ.get(middle).frequency > value)
			return helper1(occ, value, middle + 1, fin, arrayListNum);
		
		else if (occ.get(middle).frequency < value)
			return helper1(occ, value, beginning, middle - 1, arrayListNum);

		return middle;
	} //done
	
	private boolean helper2(String str) {
		
		char[] charArr = str.toCharArray();
		
		for (char crc : charArr) {
			
			if (Character.isAlphabetic(crc) == false)
				
				return false;
		}
		
		return true;
	}

	private String helper3(String str) {
		
		if (str.length() > 0) {
			
			if (str.charAt(str.length() - 1) == '.' || str.charAt(str.length() - 1) == ','
					|| str.charAt(str.length() - 1) == '?' || str.charAt(str.length() - 1) == ':'
					|| str.charAt(str.length() - 1) == ';' || str.charAt(str.length() - 1) == '!')
				return helper3(str.substring(0, (str.length() - 1)));
			
			else
				
				return str;
		}
		
		return null;
	} //done i think 
}