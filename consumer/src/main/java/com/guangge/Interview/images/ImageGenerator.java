//package com.guangge.Interview.images;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class ImageController {
//
//    private final ImageGenerator imageGenerator;
//
//    public ImageController(@Autowired(required = false) ImageGenerator imageGenerator) {
//        this.imageGenerator = imageGenerator;
//    }
//
//    @PostMapping("/image/generate")
//    public Object generate(@RequestParam String prompt) {
//        if (imageGenerator == null) {
//            return "图片生成功能暂未开启";
//        }
//        return imageGenerator.generate(prompt);
//    }
//}
