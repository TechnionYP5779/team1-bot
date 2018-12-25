package indexer;

import java.io.*;
import java.util.*;

/**
 * Created by Yoav Zuriel on 11/7/2018.
 */
public class Indexer {
	private HashMap<Integer, Bag> documents;
	private Bag corpus;
	private HashSet<String> stopWords = new HashSet<>();

	Indexer() {
		this.stopWords.addAll(Arrays.asList("a", "about", "above", "according", "across", "after", "afterwards",
				"again", "against", "albeit", "all", "almost", "alone", "along", "already", "also", "although",
				"always", "among", "amongst", "am", "an", "and", "another", "any", "anybody", "anyhow", "anyone",
				"anything", "anyway", "anywhere", "apart", "are", "around", "as", "at", "av", "be", "became", "because",
				"become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside",
				"besides", "between", "beyond", "both", "but", "by", "can", "cannot", "canst", "certain", "cf",
				"choose", "contrariwise", "cos", "could", "cu", "day", "do", "does", "doesn't", "doing", "dost", "doth",
				"double", "down", "dual", "during", "each", "either", "else", "elsewhere", "enough", "et", "etc",
				"even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "except", "excepted",
				"excepting", "exception", "exclude", "excluding", "exclusive", "far", "farther", "farthest", "few",
				"ff", "first", "for", "formerly", "forth", "forward", "from", "front", "further", "furthermore",
				"furthest", "get", "go", "had", "halves", "hardly", "has", "hast", "hath", "have", "he", "hence",
				"henceforth", "her", "here", "hereabouts", "hereafter", "hereby", "herein", "hereto", "hereupon",
				"hers", "herself", "him", "himself", "hindmost", "his", "hither", "hitherto", "how", "however",
				"howsoever", "i", "ie", "if", "in", "inasmuch", "inc", "include", "included", "including", "indeed",
				"indoors", "inside", "insomuch", "instead", "into", "inward", "inwards", "is", "it", "its", "itself",
				"just", "kind", "kg", "km", "last", "latter", "latterly", "less", "lest", "let", "like", "little",
				"ltd", "many", "may", "maybe", "me", "meantime", "meanwhile", "might", "moreover", "most", "mostly",
				"more", "mr", "mrs", "ms", "much", "must", "my", "myself", "namely", "need", "neither", "never",
				"nevertheless", "next", "no", "nobody", "none", "nonetheless", "noone", "nope", "nor", "not", "nothing",
				"notwithstanding", "now", "nowadays", "nowhere", "of", "off", "often", "ok", "on", "once", "one",
				"only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out",
				"outside", "over", "own", "per", "perhaps", "plenty", "provide", "quite", "rather", "really", "round",
				"said", "sake", "same", "sang", "save", "saw", "see", "seeing", "seem", "seemed", "seeming", "seems",
				"seen", "seldom", "selves", "sent", "several", "shalt", "she", "should", "shown", "sideways", "since",
				"slept", "slew", "slung", "slunk", "smote", "so", "some", "somebody", "somehow", "someone", "something",
				"sometime", "sometimes", "somewhat", "somewhere", "spake", "spat", "spoke", "spoken", "sprang",
				"sprung", "stave", "staves", "still", "such", "supposing", "than", "that", "the", "thee", "their",
				"them", "themselves", "then", "thence", "thenceforth", "there", "thereabout", "thereabouts",
				"thereafter", "thereby", "therefore", "therein", "thereof", "thereon", "thereto", "thereupon", "these",
				"they", "this", "those", "thou", "though", "thrice", "through", "throughout", "thru", "thus", "thy",
				"thyself", "till", "to", "together", "too", "toward", "towards", "ugh", "unable", "under", "underneath",
				"unless", "unlike", "until", "up", "upon", "upward", "upwards", "us", "use", "used", "using", "very",
				"via", "vs", "want", "was", "we", "week", "well", "were", "what", "whatever", "whatsoever", "when",
				"whence", "whenever", "whensoever", "where", "whereabouts", "whereafter", "whereas", "whereat",
				"whereby", "wherefore", "wherefrom", "wherein", "whereinto", "whereof", "whereon", "wheresoever",
				"whereto", "whereunto", "whereupon", "wherever", "wherewith", "whether", "whew", "which", "whichever",
				"whichsoever", "while", "whilst", "whither", "who", "whoa", "whoever", "whole", "whom", "whomever",
				"whomsoever", "whose", "whosoever", "why", "will", "wilt", "with", "within", "without", "worse",
				"worst", "would", "wow", "ye", "yet", "year", "yippee", "you", "your", "yours", "yourself",
				"yourselves"));
		this.documents = new HashMap<>();
		this.corpus = new Bag();
	}

	/*
	 * private void putToList(String word, Integer posting) { HashMap<String,
	 * List<Integer>> currentTable = this.manyPostingLists
	 * .get(Math.abs(word.hashCode()) % manyPostingLists.size()); if
	 * (!currentTable.containsKey(word)) currentTable.put(word, new ArrayList<>());
	 * List<Integer> currentList = currentTable.get(word); if (currentList.isEmpty()
	 * || !currentList.get(currentList.size() - 1).equals(posting))
	 * currentList.add(posting); }
	 * 
	 * List<Integer> getPostingList(String word) { List<Integer> result = new
	 * ArrayList<>(); for (HashMap<String, List<Integer>> hashMap :
	 * manyPostingLists) if (hashMap.containsKey(word))
	 * result.addAll(hashMap.get(word)); return
	 * result.stream().sorted().collect(Collectors.toList()); }
	 * 
	 * public void addFromFileToList(String path) { try (BufferedReader br = new
	 * BufferedReader(new FileReader(path))) {
	 * 
	 * String line; for (boolean fillIndex = false; (line = br.readLine()) != null;)
	 * { line = line.replaceAll("\n", ""); if (line.startsWith("The Index:")) {
	 * fillIndex = true; continue; } if (line.startsWith("The Doc Ids:")) break; if
	 * (fillIndex) { String word = line.split(":")[0]; for (String posting :
	 * line.split(":")[1].split(" ")) putToList(word, Integer.parseInt(posting)); }
	 * } } catch (IOException e) { e.printStackTrace(); } }
	 * 
	 * static Indexer buildFromFile(String path) { try (BufferedReader br = new
	 * BufferedReader(new FileReader(path))) { String line; boolean fillIndex =
	 * false; Indexer indexer = new Indexer(); while ((line = br.readLine()) !=
	 * null) { line = line.replaceAll("\n", ""); if (line.startsWith("The Index:"))
	 * { fillIndex = true; continue; } if (line.startsWith("The Doc Ids:")) {
	 * fillIndex = false; continue; } if (!fillIndex) {
	 * indexer.docIdsInverted.add(line); indexer.docIds.put(line,
	 * ++indexer.currentID); } else { String word = line.split(":")[0], stringList =
	 * line.split(":")[1]; for (String posting : stringList.split(" "))
	 * indexer.putToList(word, Integer.parseInt(posting)); } } return indexer; }
	 * catch (IOException e) { e.printStackTrace(); } return null; }
	 */

	void InvertedIndex(String path) {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
			String line;
			Integer currentID = -1;
			while ((line = br.readLine()) != null)
				if (!line.startsWith("<DOCNO>")) {
					if (!line.startsWith("<"))
						buildPostingListForOneLine(line, currentID);
				} else {
					currentID = Integer.parseInt(line.substring(8, line.length() - 9));
					documents.put(currentID, new Bag());
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void buildPostingListForOneLine(String text, Integer currentID) {
		if ("".equals(text))
			return;
		Bag oldDoc = this.documents.get(currentID);
		for (String word : text.split(" "))
			if (!stopWords.contains(word)) {
				oldDoc.add(word);
				corpus.add(word);
			}
		this.documents.put(currentID, oldDoc);
	}

	// using JM smoothing
	private static double p(String word, Bag doc) {
		return doc.getCount(word) / (1. * doc.size());
	}

//	private double pSmoothed(String word, Bag doc) {
//		return 0.9 * doc.getCount(word) / (1. * doc.size()) + 0.1 * corpus.getCount(word) / (1. * corpus.size());
//	}

	private double KLdiv(Integer doc1ID, Integer doc2ID) {
		Bag doc1 = documents.get(doc1ID), doc2 = documents.get(doc2ID);
		double result = 0;
		for (String word : doc1)
			if (doc2.getCount(word) != 0)
				result -= p(word, doc1) * Math.log(p(word, doc1) / p(word, doc2));
		return result;
	}

	public void findBest(Integer docID) {
		documents.keySet().stream().filter(x -> x != docID)
				.map(id -> new AbstractMap.SimpleEntry<Integer, Double>(id, KLdiv(docID, id)))
				.sorted((p1, p2) -> p1.getValue().compareTo(p2.getValue())).limit(3).map(pair -> pair.getKey())
				.forEach(System.out::println);
//		Integer closestID = -1;
//		double bestScore = Double.MIN_VALUE;
//		for (Integer otherID : documents.keySet()) {
//			if(otherID == docID)
//				continue;
//			double currScore = KLdiv(docID, otherID);
//			if (currScore > bestScore) {
//				closestID = otherID;
//				bestScore = currScore;
//			}
//		}
//		System.out.println(closestID);
	}

	/*
	 * void serializeIndex(String path) { try (BufferedWriter writer = new
	 * BufferedWriter(new FileWriter(path));) { writer.write("The Index:\n"); for
	 * (HashMap<String, List<Integer>> postingList : manyPostingLists) for
	 * (Map.Entry<String, List<Integer>> e : postingList.entrySet())
	 * writer.write(e.getKey() + ":" + printPostingListsIndexed(e.getValue()));
	 * writer.write("The Doc Ids:\n"); for (String docName : docIdsInverted)
	 * writer.write(docName + "\n"); writer.close(); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * private static String printPostingListsIndexed(List<Integer> l) {
	 * StringBuilder stringBuilder = new StringBuilder(); for (Integer posting : l)
	 * stringBuilder.append(posting).append(" "); String result =
	 * stringBuilder.toString(); return result.substring(0, result.length() - 1) +
	 * "\n"; }
	 * 
	 * Integer getDocIdByName(String name) { return docIds.get(name); }
	 * 
	 * private List<Integer> opPerform(String op, String term1, String term2) {
	 * switch (op) { case "AND": return performAND(queryParsing(term1),
	 * queryParsing(term2)); case "OR": return performOR(queryParsing(term1),
	 * queryParsing(term2)); case "NOT": return performNOT(queryParsing(term1),
	 * queryParsing(term2)); } return new ArrayList<>(); }
	 * 
	 * List<Integer> queryParsing(String query) { Pattern pattern =
	 * Pattern.compile("\\( (?<WORD>[^\\(\\) ]*?) \\)"); Matcher matcher =
	 * pattern.matcher(query); if (matcher.matches()) return
	 * getPostingList(matcher.group(1)); pattern =
	 * Pattern.compile("\\( ([^\\(\\)]*?) (AND|OR|NOT) ([^\\(\\)]*?) \\)"); matcher
	 * = pattern.matcher(query); if (matcher.matches()) return
	 * opPerform(matcher.group(2), "( " + matcher.group(1) + " )", "( " +
	 * matcher.group(3) + " )"); pattern =
	 * Pattern.compile("\\( (?<LEFT>\\(.*\\)) (AND|OR|NOT) (?<RIGHT>\\(.*\\)) \\)");
	 * matcher = pattern.matcher(query); if (matcher.matches()) return
	 * opPerform(matcher.group(2), matcher.group(1), matcher.group(3)); pattern =
	 * Pattern.compile("\\( (?<LEFT>\\(.*\\)) (AND|OR|NOT) ([^\\(\\)]*?) \\)");
	 * matcher = pattern.matcher(query); if (matcher.matches()) return
	 * opPerform(matcher.group(2), matcher.group(1), "( " + matcher.group(3) +
	 * " )"); pattern =
	 * Pattern.compile("\\( ([^\\(\\)]*?) (AND|OR|NOT) (?<RIGHT>\\(.*\\)) \\)");
	 * matcher = pattern.matcher(query); return !matcher.matches() ? new
	 * ArrayList<>() : opPerform(matcher.group(2), "( " + matcher.group(1) + " )",
	 * matcher.group(3)); }
	 * 
	 * static List<Integer> performAND(List<Integer> postingList1, List<Integer>
	 * postingList2) { return
	 * postingList1.stream().filter(postingList2::contains).collect(Collectors.
	 * toList()); }
	 * 
	 * static List<Integer> performOR(List<Integer> postingList1, List<Integer>
	 * postingList2) { List<Integer> notIn2 = performNOT(postingList1,
	 * postingList2); notIn2.addAll(postingList2); return
	 * notIn2.stream().sorted().collect(Collectors.toList()); }
	 * 
	 * static List<Integer> performNOT(List<Integer> postingList1, List<Integer>
	 * postingList2) { return postingList1.stream().filter(x ->
	 * !postingList2.contains(x)).collect(Collectors.toList()); }
	 * 
	 * private String printPostingList(List<Integer> l) { if (l.isEmpty()) return
	 * "\n"; StringBuilder stringBuilder = new StringBuilder(); for (Integer posting
	 * : l) stringBuilder.append(docIdsInverted.get(posting - 1)).append(" ");
	 * String result = stringBuilder.toString(); return result.substring(0,
	 * result.length() - 1) + "\n"; }
	 * 
	 * void BooleanRetrieval(String queryFile) { try (BufferedReader br = new
	 * BufferedReader(new FileReader(new File(queryFile)))) { String line; while
	 * ((line = br.readLine()) != null)
	 * System.out.println(printPostingList(queryParsing(line))); } catch
	 * (IOException ignored) { // } }
	 */

	public static void main(String[] args) {
		Indexer indexer = new Indexer();
		System.out.println("Welcome to our indexer");
		String baseDir = "src/main/java/indexer/", path = baseDir + "Descriptions.txt";
		System.out.println("Using file: " + path);
		indexer.InvertedIndex(path);
		System.out.println("Outputting the index");
		indexer.findBest(234123);
//		indexer.serializeIndex(baseDir + "output.txt");
//		indexer.BooleanRetrieval(baseDir + "query.txt");
		System.out.println("Done!");
	}

}
