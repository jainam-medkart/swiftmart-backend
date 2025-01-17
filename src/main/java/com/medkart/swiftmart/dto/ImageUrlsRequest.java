package com.medkart.swiftmart.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImageUrlsRequest {
    private List<String> imageUrls;
}