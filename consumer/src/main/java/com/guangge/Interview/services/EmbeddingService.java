package com.guangge.Interview.services;

// 全部注释掉spring ai的导入
// import org.springframework.ai.document.Document;
// import org.springframework.ai.embedding.EmbeddingModel;
// import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {
    // 注释掉这两个依赖
    // private final VectorStore vectorStore;
    // private final EmbeddingModel embeddingModel;

    // 构造器注释
    /*
    public EmbeddingService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }
    */

    // 方法注释或者抛异常
    /*
    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(text);
    }

    public float[] generateEmbedding(Document document) {
        return embeddingModel.embed(document.getText());
    }

    public void saveEmbedding(String text) {
        List<Document> documents = List.of(new Document(text));
        vectorStore.add(documents);
    }
    */
}