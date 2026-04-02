package com.haizhou.smarttax.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    @Value("${deepseek.api-url}")
    private String apiUrl;
    
    @Value("${deepseek.model}")
    private String model;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public DeepSeekService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 调用DeepSeek AI获取回复
     */
    public String chat(String userMessage, List<Map<String, String>> conversationHistory) {
        try {
            System.out.println("========== DeepSeek AI调用开始 ==========");
            System.out.println("API Key: " + (apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL"));
            System.out.println("API URL: " + apiUrl);
            System.out.println("用户消息: " + userMessage);
            
            // 构建消息历史
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", 
                "你是税税通的专业智能客服「小舟」，专门帮助跨境电商卖家解决退税问题。\n\n" +
                "你的职责：\n" +
                "1. 解答税务申报、出口退税、汇率转换等问题\n" +
                "2. 介绍平台功能（基础退税¥99/月、高级分析、API接入）\n" +
                "3. 用简洁、专业、友好的语气回答，不要重复用户的话\n" +
                "4. 直接回答问题，不要问\"我能为您做什么\"之类的反问\n\n" +
                "联系方式（复杂问题时提供）：\n" +
                "- 客服电话：19129841841\n" +
                "- 邮箱：sl2200767364@163.com\n" +
                "- 工作时间：9:00-18:00\n\n" +
                "记住：直接、高效地帮助用户，不要寒暄过度。");
            messages.add(systemMessage);
            
            // 添加历史对话
            if (conversationHistory != null && !conversationHistory.isEmpty()) {
                messages.addAll(conversationHistory);
            }
            
            // 添加当前用户消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            // 发送HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("API响应状态码: " + response.statusCode());
            System.out.println("API响应内容: " + response.body());
            
            if (response.statusCode() == 200) {
                // 解析响应
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                String aiReply = jsonResponse.at("/choices/0/message/content").asText();
                System.out.println("AI回复: " + aiReply);
                System.out.println("========== DeepSeek AI调用成功 ==========");
                return aiReply;
            } else {
                System.err.println("❌ DeepSeek API Error: " + response.statusCode() + " - " + response.body());
                System.out.println("使用备用回复");
                return getFallbackResponse(userMessage);
            }
            
        } catch (Exception e) {
            System.err.println("DeepSeek API调用失败: " + e.getMessage());
            e.printStackTrace();
            return getFallbackResponse(userMessage);
        }
    }
    
    /**
     * 简单对话（不带历史）
     */
    public String chat(String userMessage) {
        return chat(userMessage, null);
    }
    
    /**
     * 备用回复（API失败时使用）
     */
    private String getFallbackResponse(String userMessage) {
        String msg = userMessage.toLowerCase();
        
        if (msg.contains("退税") || msg.contains("申报")) {
            return "关于退税申报，您可以在首页进入'基础智能退税'或'高级财务分析'进行操作。" +
                   "我们支持自动申报、进度可视化等功能。如需详细帮助，请致电：19129841841";
        } else if (msg.contains("汇率") || msg.contains("转换")) {
            return "您可以使用我们的汇率转换工具，支持CNY、USD、JPY、EUR、KRW、SGD等多种货币。" +
                   "实时汇率数据，转换快速准确。";
        } else if (msg.contains("价格") || msg.contains("费用") || msg.contains("多少钱")) {
            return "基础智能退税服务为¥99/月，包含：\n" +
                   "✅ 自动申报（对接10+平台）\n" +
                   "✅ 进度可视化\n" +
                   "✅ 合规风控\n" +
                   "✅ 7×24小时支持\n" +
                   "详情请查看产品页面或联系客服。";
        } else if (msg.contains("api") || msg.contains("接口")) {
            return "我们提供完整的API接入服务，支持：\n" +
                   "• 获取访问令牌\n" +
                   "• 提交退税申报\n" +
                   "• 查询申报状态\n" +
                   "请在'API接入'页面申请密钥。";
        } else if (msg.contains("工作时间") || msg.contains("营业时间")) {
            return "我们的工作时间是：周一至周五 9:00-18:00\n" +
                   "非工作时间留言将在下个工作日回复。\n" +
                   "紧急问题请致电：19129841841";
        } else {
            return "感谢您的咨询！我是税税通的AI客服。\n" +
                   "我可以帮您解答退税申报、汇率转换、API接入等问题。\n" +
                   "如需人工服务，请致电：19129841841（工作时间：9:00-18:00）";
        }
    }
}
