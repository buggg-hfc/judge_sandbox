package com.judge.judge_sandbox.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.judge_sandbox.JavaNativeCodeSandbox;
import com.judge.judge_sandbox.mapper.TestCaseMapper;
import com.judge.judge_sandbox.mapper.VerdictMapper;
import com.judge.judge_sandbox.model.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@RabbitListener(queues = "submission")
@Component
public class SubmissionConsumer {
    @Autowired
    TestCaseMapper testCaseMapper;
    @Autowired
    VerdictMapper verdictMapper;
    @RabbitHandler
    public void submissionService(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Submission submission = objectMapper.readValue(message, Submission.class);
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("pid",submission.getPid());
            ArrayList<TestCase> testCases = (ArrayList<TestCase>) testCaseMapper.selectList(wrapper);
            JavaNativeCodeSandbox sandbox = new JavaNativeCodeSandbox();
            ExecuteCodeRequest req = new ExecuteCodeRequest();
            req.setCode(submission.getCode());
            req.setLanguage(req.getLanguage());
            ArrayList<String> inputs = new ArrayList<>();
            for(TestCase testCase:testCases){
                String input = testCase.getInput();
                inputs.add(input);
            }
            req.setInputList(inputs);
            ExecuteCodeResponse resp = sandbox.executeCode(req);
            ArrayList<String> outputs = (ArrayList<String>) resp.getOutputList();
            if(outputs.size() != testCases.size()){
                resp.setStatus(0);
            }
            for(int i=0;i< outputs.size();i++){
                String out = outputs.get(i);
                String ans = testCases.get(i).getAns();
                Verdict verdict = new Verdict();
                verdict.setSid(submission.getId());
                verdict.setTid(testCases.get(i).getId());
                verdict.setExecTime(resp.getJudgeInfo().getTime());
                ans = ans.trim();
                out = out.trim();
                System.out.println(ans);
                System.out.println(out);
                if(ans.equals(out)){
                    verdict.setStatus(1L);
                    verdictMapper.insert(verdict);
                }else{
                    verdict.setStatus(0L);
                    verdictMapper.insert(verdict);
                }
            }
        }catch (Error | JsonProcessingException e) {
            e.printStackTrace();

        }
    }
}
