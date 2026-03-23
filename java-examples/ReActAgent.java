/**
 * ReAct 范式 Java 实现示例
 * 
 * ReAct = Reasoning + Acting
 * 交替进行思考和行动，直到完成任务
 */
public class ReActAgent extends Agent {
    
    private static final int MAX_STEPS = 10;
    
    @Override
    public String run(String task) {
        String thought = "我需要完成: " + task;
        
        for (int step = 0; step < MAX_STEPS; step++) {
            // 1. 构建 ReAct Prompt
            String prompt = buildReActPrompt(thought);
            
            // 2. 调用 LLM 生成 Thought + Action
            String response = llm.complete(prompt);
            ReActOutput output = parseReActOutput(response);
            
            // 3. 检查是否是最终答案
            if (output.isFinalAnswer()) {
                return output.getAnswer();
            }
            
            // 4. 执行工具
            Tool tool = findTool(output.getAction());
            String observation = tool.execute(output.getActionInput());
            
            // 5. 更新记忆和状态
            memory.add("Thought: " + output.getThought());
            memory.add("Action: " + output.getAction());
            memory.add("Observation: " + observation);
            
            // 准备下一步的思考
            thought = "基于观察: " + observation + "，我需要...";
        }
        
        return "Error: 达到最大步数限制";
    }
    
    private String buildReActPrompt(String thought) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位智能助手，请通过思考和行动来解决问题。\n\n");
        sb.append("可用工具:\n");
        for (Tool tool : tools) {
            sb.append("- ").append(tool.getName()).append("\n");
        }
        sb.append("\n");
        
        if (!memory.isEmpty()) {
            sb.append("历史记录:\n").append(memory.getContext()).append("\n\n");
        }
        
        sb.append("当前思考: ").append(thought).append("\n\n");
        sb.append("请按以下格式输出:\n");
        sb.append("Thought: [你的推理过程]\n");
        sb.append("Action: [工具名称，如无则写 None]\n");
        sb.append("Action Input: [工具参数]\n");
        sb.append("或\n");
        sb.append("Final Answer: [最终答案]\n");
        
        return sb.toString();
    }
    
    private ReActOutput parseReActOutput(String response) {
        String thought = extractField(response, "Thought:");
        String action = extractField(response, "Action:");
        String actionInput = extractField(response, "Action Input:");
        String finalAnswer = extractField(response, "Final Answer:");
        
        if (finalAnswer != null && !finalAnswer.isEmpty()) {
            return new ReActOutput(true, finalAnswer, null, null);
        }
        
        return new ReActOutput(false, null, action, actionInput);
    }
    
    private String extractField(String text, String field) {
        int start = text.indexOf(field);
        if (start == -1) return null;
        
        start += field.length();
        int end = text.indexOf("\n", start);
        if (end == -1) end = text.length();
        
        return text.substring(start, end).trim();
    }
    
    private Tool findTool(String actionName) {
        if (actionName == null || actionName.equals("None")) {
            return null;
        }
        
        return tools.stream()
            .filter(t -> t.getName().equalsIgnoreCase(actionName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("未知工具: " + actionName));
    }
}

// 辅助类
class ReActOutput {
    private final boolean finalAnswer;
    private final String answer;
    private final String action;
    private final String actionInput;
    
    public ReActOutput(boolean finalAnswer, String answer, String action, String actionInput) {
        this.finalAnswer = finalAnswer;
        this.answer = answer;
        this.action = action;
        this.actionInput = actionInput;
    }
    
    public boolean isFinalAnswer() { return finalAnswer; }
    public String getAnswer() { return answer; }
    public String getAction() { return action; }
    public String getActionInput() { return actionInput; }
}
