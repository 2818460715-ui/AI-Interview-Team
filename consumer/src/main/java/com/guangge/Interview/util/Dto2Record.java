//package com.guangge.Interview.util;
//
//import com.guangge.Interview.data.Candidates;
//import com.guangge.Interview.data.Resume;
//import com.guangge.Interview.record.CandidateRecord;
//import com.guangge.Interview.record.InterViewRecord;
//
//public class Dto2Record {
//    public static InterViewRecord toInterViewDetails(Resume resume){
//        return new InterViewRecord(
//                resume.getId().toString(),
//                resume.getName(),
//                resume.getScore(),
//                resume.getInterViewStatus().toString(),
//                resume.getEvaluate(),
//                resume.getEmail(),
//                resume.getMp3Path(),
//                resume.getInterviewEvaluate()
//        );
//    }
//
//    public static CandidateRecord toCandidateRecord(Candidates candidates) {
//        return new CandidateRecord(
//                candidates.getId(),
//                candidates.getName(),
//                candidates.getCv(),
//                candidates.getEmail(),
//                candidates.getBirth(),
//                candidates.getStatus().toString(),
//                candidates.getPictureUrl());
//    }
//}
package com.guangge.Interview.util;

import com.guangge.Interview.data.Candidates;
import com.guangge.Interview.data.Resume;
import com.guangge.Interview.record.CandidateRecord;
import com.guangge.Interview.record.InterViewRecord;

public class Dto2Record {
    /**
     * 严格匹配 InterViewRecord 3个构造参数：number、score、evaluate
     */
    public static InterViewRecord toInterViewDetails(Resume resume){
        return new InterViewRecord(
                resume.getId().toString(),  // number：序号
                resume.getScore(),         // score：分数（int）
                resume.getEvaluate()       // evaluate：笔试评语
        );
    }

    public static CandidateRecord toCandidateRecord(Candidates candidates) {
        return new CandidateRecord(
                candidates.getId(),
                candidates.getName(),
                candidates.getCv(),
                candidates.getEmail(),
                candidates.getBirth(),
                candidates.getStatus().toString(),
                candidates.getPictureUrl());
    }
}
