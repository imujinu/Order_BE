package com.order.ordersystem.product.service;

import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.member.repository.MemberRepository;
import com.order.ordersystem.product.domain.Product;
import com.order.ordersystem.product.dto.ProductSearchDto;
import com.order.ordersystem.product.dto.ProductCreateDto;
import com.order.ordersystem.product.dto.ProductResDto;
import com.order.ordersystem.product.dto.ProductUpdateDto;
import com.order.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Long create(ProductCreateDto productCreateDto){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));

        String fileName = "product-"+member.getId()+"-productImage-"+productCreateDto.getProductImage().getOriginalFilename();
        Product lastProduct = productRepository.findTopByOrderByIdDesc();
        Long nextId = (lastProduct != null) ? lastProduct.getId() + 1 : 1L;


        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(productCreateDto.getProductImage().getContentType()) // image//jpg
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(productCreateDto.getProductImage().getBytes()));
        } catch(IOException e ){
            throw new IllegalArgumentException("IOEerror!!");
        }   catch (Exception e) {
            // checked -> unchecked로 바꿔 전체 rollback 되도록 예외처리
            throw new IllegalArgumentException("예상못한 에러!!");
        }
        // 이미지 삭제 시
//        s3Client.deleteObject(a->a.bucket(bucket).key(fileName));

        //이미지 url 추출
        String imgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(fileName)).toExternalForm();
        Product product = productRepository.save(productCreateDto.toEntity(member, imgUrl));
        return product.getId();
    }

    public Page<ProductResDto> findAll(Pageable pageable, ProductSearchDto productSearchDto) {

        Specification<Product> specification = new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // Root : 엔티티의 속성을 접근하기 위한 객체 CriteriaBuilder : 쿼리를 생성하기 위한 객체
                List<Predicate> predicateList = new ArrayList<>();

                if(productSearchDto.getCategory()!=null){
                    predicateList.add(criteriaBuilder.equal(root.get("category"), productSearchDto.getCategory()));
                }
                if(productSearchDto.getProductName()!=null){
                    predicateList.add(criteriaBuilder.like(root.get("name"), "%"+productSearchDto.getProductName()+"%"));
                }

                Predicate[] predicatesArr = new Predicate[predicateList.size()];
                for(int i=0; i<predicateList.size(); i++){
                    predicatesArr[i] = predicateList.get(i);
                }

//                위의 검색 조건들을 하나(한 줄) 의 Predicate 객체로 만들어서 return
                Predicate predicate = criteriaBuilder.and(predicatesArr);
                return predicate;
            }
    };
        Page<Product> productList = productRepository.findAll(specification,pageable);
        return productList.map((a)->new ProductResDto().fromEntity(a));

    }

    public ProductResDto detail(Long id) {
        Product product =  productRepository.findById(id).orElseThrow(()->new EntityNotFoundException("해당 물건이 존재하지 않습니다."));
        return new ProductResDto().fromEntity(product);
    }

    public Product update(Long id, ProductUpdateDto productUpdateDto) {
         Product prevProduct = productRepository.findById(id).orElseThrow(()->new EntityNotFoundException("해당 상품이 존재하지 않습니다."));
         prevProduct.updateProduct(productUpdateDto);
         if(productUpdateDto.getProductImage()!=null && !productUpdateDto.getProductImage().isEmpty()){

         //기존 이미지를 삭제
            String imgUrl = prevProduct.getImagePath();
            //https://jin-ordersystem-bucket.s3.ap-northeast-2.amazonaws.com/product-1-productImage-T07SG50B615-U07SWB36DQE-9a52b35efb20-512.jpg
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
            s3Client.deleteObject(a->a.bucket(bucket).key(fileName));

             String newFileName = "product-"+prevProduct.getId()+"-productImage-"+productUpdateDto.getProductImage().getOriginalFilename();

        //신규 이미지 등록
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(newFileName)
                .contentType(productUpdateDto.getProductImage().getContentType()) // image//jpg
                .build();
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(productUpdateDto.getProductImage().getBytes()));
        } catch(IOException e ){
            throw new IllegalArgumentException("IOEerror!!");
        }   catch (Exception e) {
            // checked -> unchecked로 바꿔 전체 rollback 되도록 예외처리
            throw new IllegalArgumentException("예상못한 에러!!");
        }

        String newImgUrl = s3Client.utilities().getUrl(a->a.bucket(bucket).key(newFileName)).toExternalForm();
        prevProduct.updateUrl(newImgUrl);
         }else{
             prevProduct.updateUrl(null);
         }
        return prevProduct;

    }
}
