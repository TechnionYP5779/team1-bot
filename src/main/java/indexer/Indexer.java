package indexer;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Yoav Zuriel on 11/7/2018.
 */
public class Indexer {
	private HashMap<String, Integer> docIds;
	private ArrayList<String> docIdsInverted;
	private Integer currentID = Integer.valueOf(0);
	private List<HashMap<String, List<Integer>>> manyPostingLists;

	Indexer() {
		this.docIds = new HashMap<>();
		this.docIdsInverted = new ArrayList<>();
		this.manyPostingLists = new ArrayList<>();
		for (int i = 0; i < 27; ++i)
			this.manyPostingLists.add(new HashMap<>());
	}

	private void putToList(String word, Integer posting) {
		HashMap<String, List<Integer>> currentTable = this.manyPostingLists
				.get(Math.abs(word.hashCode()) % manyPostingLists.size());
		if (!currentTable.containsKey(word))
			currentTable.put(word, new ArrayList<>());
		List<Integer> currentList = currentTable.get(word);
		if (currentList.isEmpty() || !currentList.get(currentList.size() - 1).equals(posting))
			currentList.add(posting);
	}

	List<Integer> getPostingList(String word) {
		List<Integer> result = new ArrayList<>();
		for (HashMap<String, List<Integer>> hashMap : manyPostingLists)
			if (hashMap.containsKey(word))
				result.addAll(hashMap.get(word));
		return result.stream().sorted().collect(Collectors.toList());
	}

	void InvertedIndex(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
			String line;
			while ((line = br.readLine()) != null)
				if (line.startsWith("<DOC>"))
					currentID = Integer.valueOf(currentID.intValue() + 1);
				else if (!line.startsWith("<DOCNO>")) {
					if (!line.startsWith("<"))
						buildPostingListForOneLine(line, currentID);
				} else {
					String currentName = line.substring(8, line.length() - 9);
					docIds.put(currentName, currentID);
					docIdsInverted.add(currentID.intValue() - 1, currentName);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void buildPostingListForOneLine(String text, Integer docId) {
		if (!"".equals(text))
			for (String word : text.split(" "))
				putToList(word, docId);
	}

	void serializeIndex(String path) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path));) {
			writer.write("The Index:\n");
			for (HashMap<String, List<Integer>> postingList : manyPostingLists)
				for (Map.Entry<String, List<Integer>> e : postingList.entrySet())
					writer.write(e.getKey() + ":" + printPostingListsIndexed(e.getValue()));
			writer.write("The Doc Ids:\n");
			for (String docName : docIdsInverted)
				writer.write(docName + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String printPostingListsIndexed(List<Integer> l) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Integer posting : l)
			stringBuilder.append(posting).append(" ");
		String result = stringBuilder.toString();
		return result.substring(0, result.length() - 1) + "\n";
	}

	Integer getDocIdByName(String name) {
		return docIds.get(name);
	}

	private List<Integer> opPerform(String op, String term1, String term2) {
		switch (op) {
		case "AND":
			return performAND(queryParsing(term1), queryParsing(term2));
		case "OR":
			return performOR(queryParsing(term1), queryParsing(term2));
		case "NOT":
			return performNOT(queryParsing(term1), queryParsing(term2));
		}
		return new ArrayList<>();
	}

	List<Integer> queryParsing(String query) {
		Pattern pattern = Pattern.compile("\\( (?<WORD>[^\\(\\) ]*?) \\)");
		Matcher matcher = pattern.matcher(query);
		if (matcher.matches())
			return getPostingList(matcher.group(1));
		pattern = Pattern.compile("\\( ([^\\(\\)]*?) (AND|OR|NOT) ([^\\(\\)]*?) \\)");
		matcher = pattern.matcher(query);
		if (matcher.matches())
			return opPerform(matcher.group(2), "( " + matcher.group(1) + " )", "( " + matcher.group(3) + " )");
		pattern = Pattern.compile("\\( (?<LEFT>\\(.*\\)) (AND|OR|NOT) (?<RIGHT>\\(.*\\)) \\)");
		matcher = pattern.matcher(query);
		if (matcher.matches())
			return opPerform(matcher.group(2), matcher.group(1), matcher.group(3));
		pattern = Pattern.compile("\\( (?<LEFT>\\(.*\\)) (AND|OR|NOT) ([^\\(\\)]*?) \\)");
		matcher = pattern.matcher(query);
		if (matcher.matches())
			return opPerform(matcher.group(2), matcher.group(1), "( " + matcher.group(3) + " )");
		pattern = Pattern.compile("\\( ([^\\(\\)]*?) (AND|OR|NOT) (?<RIGHT>\\(.*\\)) \\)");
		matcher = pattern.matcher(query);
		return !matcher.matches() ? new ArrayList<>()
				: opPerform(matcher.group(2), "( " + matcher.group(1) + " )", matcher.group(3));
	}

	static List<Integer> performAND(List<Integer> postingList1, List<Integer> postingList2) {
		return postingList1.stream().filter(postingList2::contains).collect(Collectors.toList());
	}

	static List<Integer> performOR(List<Integer> postingList1, List<Integer> postingList2) {
		List<Integer> notIn2 = performNOT(postingList1, postingList2);
		notIn2.addAll(postingList2);
		return notIn2.stream().sorted().collect(Collectors.toList());
	}

	static List<Integer> performNOT(List<Integer> postingList1, List<Integer> postingList2) {
		return postingList1.stream().filter(x -> !postingList2.contains(x)).collect(Collectors.toList());
	}

	private String printPostingList(List<Integer> l) {
		if (l.isEmpty())
			return "\n";
		StringBuilder stringBuilder = new StringBuilder();
		for (Integer posting : l)
			stringBuilder.append(docIdsInverted.get(posting.intValue() - 1)).append(" ");
		String result = stringBuilder.toString();
		return result.substring(0, result.length() - 1) + "\n";
	}

	String BooleanRetrieval(String query) {
		return printPostingList(queryParsing(query));
	}
	
	public static String indexCourses(String query) {
		Indexer indexer = new Indexer();
		String baseDir = "src/main/java/indexer/", path = baseDir + "Descriptions.txt";
		indexer.InvertedIndex(path);
		return indexer.BooleanRetrieval(query);
	}

}