//Import other java data structures to be used for the Trie.
import java.util.*;

public class Trie{

	private Node root;

	public Trie(){
		root = new Node();
	}

	/* Main idea: Break the word into characters, check if it exists.
	If it doesn't --> create a new node, passCount++ either way.
	Once you reach the final character, increase endCount by 1. */
	public void insert(String word){
		if(word == null || word.length() == 0){
			return; //We check for this because if word is empty or null, it's not a real word.
		}
		word = word.toLowerCase().trim();
		root.passCount++; //this will be the # of 'words' added
		Node curr = root;
		for(char c: word.toCharArray()){
			Node child = curr.children.get(c); //<-- Try to get the node associated with the character
			if(child == null){
				child = new Node(); //there was no child previously so we need to make a new node for the child
				curr.children.put(c, child); //puts the newly created child into the map
			}
			curr = child;
			curr.passCount++; //add to total passes
		}
		curr.endCount++;
	}

	/* Break down word into characters, go through trie and check if each character exists.
	If any one char doesn't exist --> return false.
	Reached the end: check for endCount > 0 then return true if valid. */
	public boolean contains(String word){
		if(word == null || word.length() == 0){ //if word isn't valid, return false.
			return false;
		}
		word = word.toLowerCase().trim();
		Node curr = root;
		for(char c: word.toCharArray()){
			Node child = curr.children.get(c);
			if(child == null){
				return false;
			}
			curr = child;
		}
		return curr.isEndOfWord(); //call to isEndOfWord() that checks if endCount > 0.
	}

	public char mostLikelyNextChar(String prefix){
		/* Go to the last prefix char and visit its children.
		Return the child w/ highest passCount after comparison. */
   		if(prefix == null){
       		return '_';
   		}
   		prefix = prefix.toLowerCase().trim();
   		Node curr = root;
   		for(char c: prefix.toCharArray()){
   	  		curr = curr.children.get(c);
       		if(curr == null){ //If prefix isn't valid, return _
           		return '_';
			}
   		}
   		int greatestCount = Integer.MIN_VALUE;
   		char returnChar = '\0';
   		//checks passCount for greatest count + char stored in above vars.
   		for(Map.Entry<Character, Node> entry : curr.children.entrySet()){
			if(entry.getValue().passCount > greatestCount){
				greatestCount = (int) entry.getValue().passCount;
				returnChar = entry.getKey();
			}
			else if(entry.getValue().passCount == greatestCount){ //match key w/ its passCount
				if(entry.getKey() < returnChar){
					returnChar = entry.getKey();
				}
			}
		}
   		return returnChar;
    }

	//Fix mostLikelyNextWord() method and make sure it works properly (works now!)
    public String mostLikelyNextWord(String prefix){
		if(prefix == null){
			return "";
		}
		//Break down the prefix into chars and get its children.
		prefix = prefix.toLowerCase().trim();
		Node curr = root;
		for(char c: prefix.toCharArray()){
			curr = curr.children.get(c);
			if(curr == null){
				return "";
			}
		}
		/* StringBuilder builds word as we visit child chars.
		Recursively call collectWordCounts that takes SB and frequency list to add words
		until endCount > 0. */
		StringBuilder word = new StringBuilder(prefix);
		Map<String, Integer> wordFrequencies = new HashMap<>();
		collectWordCounts(curr, word, wordFrequencies);
		int maxCount = Integer.MIN_VALUE;
		String maxWord = "";
		//Find the word with the greatest endCount and store it in var.
		//Tiebreaker cases: Return word w/ shortest length or alphabetical order.
		for(Map.Entry<String, Integer> entry : wordFrequencies.entrySet()){
			if(entry.getValue() > maxCount){
				maxCount = entry.getValue();
				maxWord = entry.getKey();
			}
			else if(entry.getValue() == maxCount){ //check length then return shortest
				if(entry.getKey().length() < maxWord.length()){
					maxWord = entry.getKey();
				}
				else if(entry.getKey().length() == maxWord.length() && entry.getKey().compareTo(maxWord) < 0){
					//compareTo used for alphabetical tiebreaker.
					maxWord = entry.getKey();
				}
			}
		}
		return maxWord;
	}

	private void collectWordCounts(Node curr, StringBuilder word, Map<String, Integer> wordFreqMap){
		if(curr.endCount > 0){ //Word is complete when endCount > 0, so add it to frequency list.
			wordFreqMap.put(word.toString(), (int) curr.endCount);
		}
		for(Map.Entry<Character, Node> entry : curr.children.entrySet()){
			/* Build a complete word w/ recursive call while above condition is false. */
			word.append(entry.getKey());
			collectWordCounts(entry.getValue(), word, wordFreqMap);
			word.deleteCharAt(word.length() - 1);
		}
	}

	public void printWordFrequencies(){
		//Use StringBuilder to build words and call method to print words.
		StringBuilder sb = new StringBuilder();
		printWordRecursively(root, sb);
	}

	private void printWordRecursively(Node curr, StringBuilder word){
		/* Add each char to SB until endCount > 0 --> end of word reached, so print it. */
		if(curr.endCount > 0){
			System.out.println(word.toString()+": "+curr.endCount);
		}
		for(Map.Entry<Character, Node> entry : curr.children.entrySet()){
			word.append(entry.getKey());
			printWordRecursively(entry.getValue(), word);
			word.deleteCharAt(word.length() - 1);
		}
	}

	/* Top 5 methods use a similar logic to mostLikelyNextChar() and mostLikelyNextWord(),
	but in addition we take passCount / endCount values, put them in a list with corresponding characters
	and gather top 5 using comparisons. */

	public ArrayList top5Letters(String prefix){
		Node node = root;
		for(char c : prefix.toCharArray()){
			node = node.children.get(c);
			if(node == null){
				return new ArrayList<>();
			}
		}

		List<Map.Entry<Character, Node>> letters = new ArrayList<>();
		for(Map.Entry<Character, Node> entry : node.children.entrySet()){
			letters.add(entry);
		}

		for(int i = 0; i<letters.size(); i++){
			int currMaxIndex = i;
			for(int j = i + 1; j<letters.size(); j++){
				if(letters.get(j).getValue().passCount > letters.get(currMaxIndex).getValue().passCount){
					currMaxIndex = j;
				}
			}
			Map.Entry<Character, Node> temp = letters.get(i);
			letters.set(i, letters.get(currMaxIndex));
			letters.set(currMaxIndex, temp);
		}

		ArrayList<Character> topLetters = new ArrayList<>();
		int topListSize = 0;
		if(letters.size() > 5){
			topListSize = 5;
		} else{
			topListSize = letters.size();
		}

		for(int i = 0; i<topListSize; i++){
			topLetters.add(letters.get(i).getKey());
		}
		return topLetters;
	}

	public ArrayList top5Words(String prefix){
		Node node = root;
		for(char c: prefix.toCharArray()){
			node = node.children.get(c);
			if(node == null){
				return new ArrayList<>();
			}
		}

		Map<String, Integer> wordsWithCounts = new HashMap<>();
		StringBuilder word = new StringBuilder(prefix);
		collectWordCounts(node, word, wordsWithCounts);

		List<Map.Entry<String, Integer>> wordList = new ArrayList<>(wordsWithCounts.entrySet());
		for(int i = 0; i<wordList.size(); i++){
			int currMaxIndex = i;
			for(int j = i + 1; j<wordList.size(); j++){
				if(wordList.get(j).getValue() > wordList.get(currMaxIndex).getValue()){
					currMaxIndex = j;
				}
			}
			Map.Entry<String, Integer> temp = wordList.get(i);
			wordList.set(i, wordList.get(currMaxIndex));
			wordList.set(currMaxIndex, temp);
		}

		ArrayList<String> topWords = new ArrayList<>();
		int topListSize = 0;
		if(wordList.size() > 5){
			topListSize = 5;
		} else{
			topListSize = wordList.size();
		}

		for(int i = 0; i<topListSize; i++){
			topWords.add(wordList.get(i).getKey());
		}
		return topWords;
	}

	//Node - Inner Class
	class Node{
		//long is an integer that allows you to store values of large capacity
		long passCount; // total # of times a node is traversed
		long endCount; // # of times end of word
		Map<Character, Node> children; //In this map, a letter is the key and the value is basically storing the passCount and endCount of that letter.

		Node(){
			passCount = 0;
			endCount = 0;
			children = new HashMap<Character, Node>();
		}

		boolean isEndOfWord(){
		//if endCount is non-zero, return true, else return false
			return endCount > 0;
		}

		@Override
		public String toString(){
			return "(pass = "+passCount+", end = "+endCount+")";
		}

	}

	public static void main(String[] args) {
   		Trie trie = new Trie();

   		// Original data
   		String data = "cat cats dog dogs door";
   		System.out.println("Inserted words:");
  		System.out.println(data + "\n");

       // Insert words
       for (String word : data.split(" ")) {
           trie.insert(word);
       }

       // Simple contains checks
       System.out.println("contains(\"cat\")  --> " + trie.contains("cat"));
       System.out.println("contains(\"cats\") --> " + trie.contains("cats"));
       System.out.println("contains(\"dog\")  --> " + trie.contains("dog"));
       System.out.println("contains(\"dogs\") --> " + trie.contains("dogs"));
       System.out.println("contains(\"door\") --> " + trie.contains("door"));

       System.out.println();

       // Partial / false cases
       System.out.println("contains(\"ca\")   --> " + trie.contains("ca"));
       System.out.println("contains(\"do\")   --> " + trie.contains("do"));
       System.out.println("contains(\"doo\")  --> " + trie.contains("doo"));
       System.out.println("contains(\"catz\") --> " + trie.contains("catz"));

       System.out.println();

       //mostLikelyNextChar check
       Trie trie2 = new Trie();
       String data2 = """
           apple banana apple apple
           and and and any any
           cat dog dog any any
           apple any banana any
           """;

// Load words
for (String w : data2.split("\\s+")){ // split on white space
   trie2.insert(w.toLowerCase());
}

System.out.println("mostLikelyNextChar(\"a\") --> " + trie2.mostLikelyNextChar("a"));
System.out.println("mostLikelyNextChar(\"ap\") --> " + trie2.mostLikelyNextChar("ap"));
System.out.println("mostLikelyNextChar(\"do\") --> " + trie2.mostLikelyNextChar("do"));
        System.out.println("mostLikelyNextChar(\"x\") --> " + trie2.mostLikelyNextChar("x"));
        System.out.println();

         System.out.println("mostLikelyNextWord(\"a\") --> " + trie2.mostLikelyNextWord("a"));
 System.out.println("mostLikelyNextWord(\"ap\") --> " + trie2.mostLikelyNextWord("ap"));
 System.out.println("mostLikelyNextWord(\"b\") --> " + trie2.mostLikelyNextWord("b"));
 System.out.println("mostLikelyNextWord(\"z\") --> " + trie2.mostLikelyNextWord("z"));

 System.out.println("\n---- printWordFrequencies ----");
         trie2.printWordFrequencies();
         System.out.println("\nNOTE:  Alphabetical sorting also acceptable\n\n" );



}

}