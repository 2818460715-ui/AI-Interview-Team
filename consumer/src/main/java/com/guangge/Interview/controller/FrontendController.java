////package com.guangge.Interview.controller;
////
////import com.guangge.Interview.mail.MailService;
////import com.guangge.Interview.record.CandidateRecord;
////import com.guangge.Interview.record.InterViewRecord;
////import com.guangge.Interview.services.CandidatesService;
////import com.guangge.Interview.services.ResumeService;
////import com.guangge.Interview.services.ResumeVectorService;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.List;
////
////@RestController()
////@RequestMapping("frontend")
////public class FrontendController {
////    private final ResumeService resumeService;
////    private final MailService mailService;
////    private final ResumeVectorService resumeVectorService;
////    private final CandidatesService candidatesService;
////
////    public FrontendController(ResumeService resumeService,
////                              MailService mailService,
////                              ResumeVectorService resumeVectorService,
////                              CandidatesService candidatesService) {
////        this.resumeService = resumeService;
////        this.mailService = mailService;
////        this.resumeVectorService = resumeVectorService;
////        this.candidatesService = candidatesService;
////    }
////
////    /**
////     * 面试信息
////     * @return 面试者信息
////     */
////    @GetMapping(value = "/interView")
////    public List<InterViewRecord> getInterView() {return this.resumeService.getInterViews();}
////
////    /**
////     * 给面试者发送结果
////     * @param name 面试者名
////     */
////    @PostMapping(value = "/sendMail")
////    public void sendMail(@RequestParam("name") String name) {
////        this.mailService.sendMailForAttachment(name);
////    }
////
////    /**
////     * 查询面试信息
////     * @param question 问题
////     * @return 面试者信息
////     */
////    @GetMapping(value = "/findInterView")
////    public List<InterViewRecord> findInterView(@RequestParam("question") String question) {
////        return this.resumeVectorService.findInterViewsByQuestion(question);
////    }
////
////    /**
////     * 面试者信息
////     * @return 面试者
////     */
////    @GetMapping(value = "/candidates")
////    public List<CandidateRecord> getCandidates() {return this.candidatesService.getCandidates();}
////}
//
//package com.guangge.Interview.controller;
//
//import com.guangge.Interview.practice.dto.ChatRequest;
//import com.guangge.Interview.mail.MailService;
//import com.guangge.Interview.record.CandidateRecord;
//import com.guangge.Interview.record.InterViewRecord;
//import com.guangge.Interview.services.CandidatesService;
//import com.guangge.Interview.services.ResumeService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController()
//@RequestMapping("frontend")
//public class FrontendController {
//    private final ResumeService resumeService;
//    private final MailService mailService;
//    private final CandidatesService candidatesService;
//
//    public FrontendController(ResumeService resumeService,
//                              MailService mailService,
//                              CandidatesService candidatesService) {
//        this.resumeService = resumeService;
//        this.mailService = mailService;
//        this.candidatesService = candidatesService;
//    }
//
//    /**
//     * 连通测试接口
//     */
//    @GetMapping("/test")
//    public Map<String, Object> test(HttpServletRequest req) {
//        System.out.println("===== 收到请求！来源IP：" + req.getRemoteAddr());
//        return Map.of("msg", "连通成功 ✅");
//    }
//
//    /**
//     * 聊天接口（调试放开，不需要 token）
//     */
//    @PostMapping("/chat")
//    public Map<String, Object> chat(@RequestBody ChatRequest body) {
//        System.out.println("收到用户 prompt：" + body.prompt());
//        return Map.of("reply", "收到消息：" + body.prompt());
//    }
//
//    /**
//     * 面试信息
//     * @return 面试者信息
//     */
//    @GetMapping(value = "/interView")
//    public List<InterViewRecord> getInterView() {
//        return this.resumeService.getInterViews();
//    }
//
//    /**
//     * 给面试者发送结果
//     * @param name 面试者名
//     */
//    @PostMapping(value = "/sendMail")
//    public void sendMail(@RequestParam("name") String name) {
//        this.mailService.sendMailForAttachment(name);
//    }
//
//    /**
//     * 面试者信息
//     * @return 面试者
//     */
//    @GetMapping(value = "/candidates")
//    public List<CandidateRecord> getCandidates() {
//        return this.candidatesService.getCandidates();
//    }
//}


package com.guangge.Interview.controller;

import com.guangge.Interview.practice.dto.ChatRequest;
import com.guangge.Interview.mail.MailService;
import com.guangge.Interview.record.CandidateRecord;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.services.CandidatesService;
import com.guangge.Interview.services.ResumeService;
import com.guangge.Interview.util.FeatureUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("frontend")
public class FrontendController {
    private final ResumeService resumeService;
    private final MailService mailService;
    private final CandidatesService candidatesService;
    private final FeatureUtil featureUtil;

    public FrontendController(ResumeService resumeService,
                              MailService mailService,
                              CandidatesService candidatesService,
                              FeatureUtil featureUtil) {
        this.resumeService = resumeService;
        this.mailService = mailService;
        this.candidatesService = candidatesService;
        this.featureUtil = featureUtil;
    }

    /**
     * 连通测试接口
     */
    @GetMapping("/test")
    public Map<String, Object> test(HttpServletRequest req) {
        System.out.println("===== 收到请求！来源IP：" + req.getRemoteAddr());
        return Map.of("msg", "连通成功 ✅");
    }

    /**
     * 聊天接口（调试放开，不需要 token）
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody ChatRequest body) {
        featureUtil.checkFreeChat();
        System.out.println("收到用户 prompt：" + body.prompt());
        return Map.of("reply", "收到消息：" + body.prompt());
    }

    /**
     * 面试信息
     * @return 面试者信息
     */
    @GetMapping(value = "/interView")
    public List<InterViewRecord> getInterView() {
        return this.resumeService.getInterViews();
    }

    /**
     * 给面试者发送结果
     * @param name 面试者名
     */
    @PostMapping(value = "/sendMail")
    public void sendMail(@RequestParam("name") String name) {
        this.mailService.sendMailForAttachment(name);
    }

    /**
     * 面试者信息
     * @return 面试者
     */
    @GetMapping(value = "/candidates")
    public List<CandidateRecord> getCandidates() {
        return this.candidatesService.getCandidates();
    }
}
