package com.getinfo.gestaocontratual.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentoService {

    private final String supabaseUrl = "https://dczwhcefblmnjnhhpvzn.supabase.co";
    private final String supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRjendoY2VmYmxtbmpuaGhwdnpuIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjIyOTEzMSwiZXhwIjoyMDYxODA1MTMxfQ.UIerebZCN4SDCQlu-JCH20gjC3Wanaq7d7tAznH2DjE";
    private final String storageUrl = supabaseUrl + "/storage/v1";
    private final String bucketName = "documentos";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void uploadFile(String fileName, MultipartFile file) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        String url = storageUrl + "/object/" + bucketName + "/" + encodedFileName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", file.getContentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                try {
                    JsonObject errorResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    String errorMessage = errorResponse.get("message").getAsString();
                    throw new IOException("Failed to upload file to Supabase Storage: " + errorMessage);
                } catch (Exception e) {
                    throw new IOException("Failed to upload file to Supabase Storage. Status Code: " + response.statusCode() + ", Response Body: " + response.body());
                }
            }
        } catch (InterruptedException e) {
            throw new IOException("Error uploading file: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String url = storageUrl + "/object/" + bucketName + "/" + fileName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + supabaseKey)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Failed to delete file from Supabase Storage. Status Code: " + response.statusCode() + ", Response Body: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }
}
