import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> allPdfs = new HashMap<>();

    public BooleanSearchEngine(File srcDir) throws IOException {
        File[] files = srcDir.listFiles();
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
                                freqs.put(word.toLowerCase(),
                                        freqs.getOrDefault(word.toLowerCase(), 0) + 1);
                            }
                            for (var entry : freqs.entrySet()) {
                                PageEntry pageEntry = new PageEntry(file.getName(), i, entry.getValue());
                                sorting(allPdfs, entry.getKey(), pageEntry);
                            }
                        }
                    }
                }
            }
        }
    }

    private void sorting(Map<String, List<PageEntry>> allPdfs, String word, PageEntry pageEntry) {
        List<PageEntry> pdfList = allPdfs.getOrDefault(word, new ArrayList<>());
        pdfList.add(pageEntry);
        allPdfs.put(word, pdfList);
        allPdfs.values().forEach(Collections::sort);
    }

    public List<PageEntry> search(String word) {
        return allPdfs.get(word.toLowerCase());
    }
}