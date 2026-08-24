package org.example.hackathon_de07.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RAGService {
    @Autowired
    private VectorStore vectorStore;

    public String loadAndSaveDocument(MultipartFile file){
        try {
            Resource resource = file.getResource();
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

            List<Document> rawDocument = tikaDocumentReader.get();

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder().build();
            List<Document> documents = tokenTextSplitter.split(rawDocument);

            vectorStore.add(documents);
            return "Thêm tài liệu thành công";
        }catch (Exception e){
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    public String loadAndSaveDocument(Resource resource){
        try {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

            List<Document> rawDocument = tikaDocumentReader.get();

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder().build();
            List<Document> documents = tokenTextSplitter.split(rawDocument);

            vectorStore.add(documents);
            return "Thêm tài liệu thành công";
        }catch (Exception e){
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    public String searchDocument(String keyword){
        SearchRequest searchRequest = SearchRequest
                .builder()
                .query(keyword)
                .topK(3)
                .build();
        String result = vectorStore.similaritySearch(searchRequest).stream()
                .map(document -> document.getText())
                .filter(text -> text != null && !text.trim().isEmpty())
                .collect(Collectors.joining("\n\n"));
        if (result == null || result.trim().isEmpty()) {
            return "Không tìm thấy thông tin phù hợp trong vector_story.";
        }
        return result;
    }
}
