package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

	private void initStopwords(String swPath) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(swPath)))) {
			String line;
			while ((line = br.readLine()) != null) {
				this.stopwords.addAll(Arrays.asList(line.split(",")));
			}
		}
	}

	public CourseSimularitySearcher initCourses(String descPath, String swPath) {
		try {
			if (swPath != null)
				initStopwords(swPath);
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
