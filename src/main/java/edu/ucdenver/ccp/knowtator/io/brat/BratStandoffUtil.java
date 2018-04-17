package edu.ucdenver.ccp.knowtator.io.brat;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.TextSourceManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BratStandoffUtil implements BasicIOUtil {
	private static final Logger log = Logger.getLogger(BratStandoffUtil.class);

	private static Map<Character, List<String[]>> collectAnnotations(Stream<String> standoffStream) {
		Map<Character, List<String[]>> annotationCollector = createAnnotationCollector();

		standoffStream.forEach(
				line -> {
					if (!line.trim().isEmpty()) {
						String[] entries = line.split(StandoffTags.columnDelimiter);
						char annotationType = entries[0].charAt(0);
						annotationCollector.get(annotationType).add(entries);
					}
        });

		return annotationCollector;
	}

	private static Map<Character, List<String[]>> createAnnotationCollector() {
		Map<Character, List<String[]>> annotationCollector = new HashMap<>();
		IntStream.range(0, StandoffTags.tagList.length)
				.mapToObj(i -> StandoffTags.tagList[i])
				.forEach(tag -> annotationCollector.put(tag, new ArrayList<>()));

		return annotationCollector;
	}

	@Override
	public void read(Savable textSourceManager, File file) {
		try {
			Stream<String> standoffStream = Files.lines(Paths.get(file.toURI()));

			Map<Character, List<String[]>> annotationMap = collectAnnotations(standoffStream);

			annotationMap
					.get(StandoffTags.DOCID)
					.add(new String[]{FilenameUtils.getBaseName(file.getName())});

			textSourceManager.readFromBratStandoff(file, annotationMap, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(Savable savable, File file) {
		if (savable instanceof TextSourceManager) {
			((TextSourceManager) savable)
					.getTextSourceCollection()
					.getCollection()
					.forEach(
							textSource -> {
								File outputFile =
										new File(file.getAbsolutePath() + File.separator + textSource.getId() + ".ann");
                writeToOutputFile(textSource, outputFile);
							});
		} else {
			writeToOutputFile(savable, file);
		}
	}

	private void writeToOutputFile(Savable savable, File file) {
		try {
			log.warn("Writing to " + file.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			savable.writeToBratStandoff(bw);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
