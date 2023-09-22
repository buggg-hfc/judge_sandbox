package com.judge.judge_sandbox.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.judge.judge_sandbox.mapper.TestCaseMapper;
import com.judge.judge_sandbox.model.TestCase;
import com.judge.judge_sandbox.model.UploadSubmissionRequest;
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
    @RabbitHandler
    public void submissionService(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UploadSubmissionRequest request = objectMapper.readValue(message, UploadSubmissionRequest.class);
            QueryWrapper<TestCase> wrapper = new QueryWrapper<>();
            wrapper.eq("pid",request.getProblemId());
            ArrayList<TestCase> testCases = (ArrayList<TestCase>) testCaseMapper.selectList(wrapper);

        }catch (Error | JsonProcessingException e) {
            e.printStackTrace();

        }
    }
}
