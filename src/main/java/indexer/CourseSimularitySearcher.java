package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CourseSimularitySearcher {
	private final double λ = 0.2;

	private HashSet<String> stopwords = new HashSet<>();
	private HashMap<Integer, BagOfWords> coursesSyllabi = new HashMap<>();
	private BagOfWords corpus = new BagOfWords();

	private void initStopwords(){
		this.stopwords.addAll(Arrays.asList("a", "about", "above", "according", "across", "after", "afterwards",
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
	}

	public CourseSimularitySearcher initCourses(String descPath, String swPath) {
		try {
			if (swPath != null)
				initStopwords();
			try (BufferedReader br = new BufferedReader(new FileReader(new File(descPath)))) {
				String line;
				Integer courseNumber = Integer.valueOf(-1);
				while ((line = br.readLine()) != null)
					if (!line.startsWith("<DOCNO>")) {
						if (!line.startsWith("<"))
							parseLine(courseNumber, line);
					} else {
						courseNumber = Integer.valueOf(Integer.parseInt(line.substring(8, line.length() - 9)));
						this.coursesSyllabi.put(courseNumber, new BagOfWords());
					}
			}
			return this;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void parseLine(Integer courseNumber, String line) {
		if ("".equals(line))
			return;
		BagOfWords oldBag = this.coursesSyllabi.get(courseNumber);
		for (String word : line.split(" "))
			if (!this.stopwords.contains(word)) {
				oldBag.add(word);
				this.corpus.add(word);
			}
		this.coursesSyllabi.put(courseNumber, oldBag);
	}

	private double p(String word, Integer courseNumber) {
		BagOfWords courseBag = this.coursesSyllabi.get(courseNumber);
		return (courseBag.getCount(word) * 1.) / courseBag.size();
	}

	private double pSmoothed(String word, Integer courseNumber) {
		return (1 - λ) * p(word, courseNumber) + λ * (corpus.getCount(word) * 1.) / corpus.size();
	}

	private double singlePairLikelihood(BagOfWords syl, Integer n2) {
		double total = 1;
		for (String word : syl) {
			total *= Math.pow(pSmoothed(word, n2), syl.getCount(word));
		}
		return total;
	}

	public List<Integer> courseLikelihood(Integer courseNumber, int limit) {
		return this.coursesSyllabi.keySet().stream().filter(n -> !n.equals(courseNumber))
				.map(n -> new AbstractMap.SimpleEntry<Integer, Double>(n,
						Double.valueOf(singlePairLikelihood(this.coursesSyllabi.get(courseNumber), n))))
				.sorted((x,y) -> y.getValue().compareTo(x.getValue())).limit(limit).map(p -> p.getKey()).collect(Collectors.toList());
	}
	
	private static String formatList(List<Integer> l) {
		if (l.isEmpty())
			return "\n";
		StringBuilder stringBuilder = new StringBuilder();
		for (Integer courseNumber : l)
			stringBuilder.append(courseNumber).append(" ");
		String result = stringBuilder.toString();
		return result.substring(0, result.length() - 1) + "\n";
	}
	
	public static String searchSimilarCourses(Integer courseNumber, int limit) {
		String baseDir = "src/main/java/indexer/";
		return formatList(new CourseSimularitySearcher().initCourses(baseDir + "Descriptions.txt", baseDir + "stopwords.txt")
				.courseLikelihood(Integer.valueOf(236370), 3));
	}

//	public static void main(String[] args) {
//		String baseDir = "src/main/java/indexer/";
//		System.out.println(formatList(new CourseSimularitySearcher().initCourses(baseDir + "Descriptions.txt", baseDir + "stopwords.txt")
//				.courseLikelihood(Integer.valueOf(236370), 3)));
//	}
}
