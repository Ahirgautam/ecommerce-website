package com.ecommerce.ecommerce_site.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageStorageService {

    private final Path root = Paths.get("uploads");

    public ImageStorageService() throws IOException{
        if(!Files.exists(root)){
            Files.createDirectories(root);
        }
    }



    public void deleteImage(String imageName) {
        try{
            Path uploadPath = root.resolve(imageName);
            File file = uploadPath.toFile();
            if(file.exists()){
                Files.delete(uploadPath);
                System.out.println("file deleted");
            }
            else{
                throw new Exception("file does not exits");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new RuntimeException("file is empty");
        }

        String imageName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path destination = root.resolve(imageName);

        // Try background removal API
        byte[] processedImage = null;
        try {
            processedImage = callBackgroundRemovalApi(image);
            System.out.println("..........................image background is removed");
        } catch (Exception e) {
            System.err.println("Background removal failed: " + e.getMessage());
        }


        if (processedImage != null && processedImage.length > 0) {
            Files.write(destination, processedImage);
        } else {
            Files.copy(image.getInputStream(), destination);
        }

        return imageName;
    }


    private byte[] callBackgroundRemovalApi(MultipartFile image) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "cd9f2c73-4bc6-493b-aa08-9fefa688e345");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        body.add("size", "auto");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://api.rembg.com/rmbg",
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new IOException("API error: " + response.getStatusCode());
        }
    }

}
