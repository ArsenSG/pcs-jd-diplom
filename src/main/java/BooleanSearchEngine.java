import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<PageEntry, String> allPdfs = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] files = pdfsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".pdf")) {
                    try (var doc = new PdfDocument(new PdfReader(file))) {
                        int numberOfPages = doc.getNumberOfPages();
                        for (int i = 0; i < numberOfPages; i++) {
                            var page = doc.getPage(i + 1);
                            var text = PdfTextExtractor.getTextFromPage(page);
                            var words = text.split("\\P{IsAlphabetic}+");
                            Map<String, Integer> freqs = new HashMap<>();
                            for (var word : words) {
                                if (word.isEmpty()) {
                                    continue;
                                }
                                int num = freqs.getOrDefault(word, 0) + 1;
                                freqs.put(word.toLowerCase(), num);
                                allPdfs.put(new PageEntry(file.getName(), doc.getPageNumber(page), num), word.toLowerCase());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        var list = new ArrayList<PageEntry>();
        for (Map.Entry<PageEntry, String> entry : allPdfs.entrySet()) {
            if (entry.getValue().equals(word)) {
                list.add(entry.getKey());
            }
        }
        Collections.sort(list);
        return list;
    }
}