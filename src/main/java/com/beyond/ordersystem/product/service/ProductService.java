package com.beyond.ordersystem.product.service;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductResDto;
import com.beyond.ordersystem.product.dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Product productCreate(ProductSaveReqDto dto){
        MultipartFile image = dto.getProductImage();
        Product product = null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/rnjsc/Desktop/tmp/",
                    product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());
        }catch (IOException e){
            throw new RuntimeException("이미지 저장 실패");
        }
        return product;
    }


    public Page<ProductResDto> productList(Pageable pageable){
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(a->a.fromEntity());
    }


    public Product productAwsCreate(ProductSaveReqDto dto){
        MultipartFile image = dto.getProductImage();
        Product product = null;
        try {
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/rnjsc/Desktop/tmp/",
                    product.getId() + "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());
        }catch (IOException e){
            throw new RuntimeException("이미지 저장 실패");
        }
        return product;
    }
}
