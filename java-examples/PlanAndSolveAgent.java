/**
 * Plan-and-Solve 范式 Java 实现示例
 * 
 * 先制定计划，再按步骤执行
 */
public class PlanAndSolveAgent extends Agent {
    
    @Override
    public String run(String task) {
        // 1. 生成计划
        List<Step> plan = generatePlan(task);
        
        // 2. 执行计划
        Map<String, Object> context = new HashMap<>();
        
        for (Step step : plan) {
            String result = executeStep(step, context);
            context.put(step.getId(), result);
            
            // 检查是否需要调整计划
            if (needsReplanning(step, result)) {
                plan = revisePlan(plan, step, result);
            }
        }
        
        // 3. 生成最终答案
        return generateFinalAnswer(context);
    }
    
    private List<Step> generatePlan(String task) {
        String prompt = String.format("""
            任务: %s
            
            请制定详细的执行计划，每步一行，格式:
            1. [步骤描述]
            2. [步骤描述]
            ...
            """, task);
        
        String response = llm.complete(prompt);
        return parsePlan(response);
    }
    
    private String executeStep(Step step, Map<String, Object> context) {
        String prompt = String.format("""
            执行步骤: %s
            上下文: %s
            
            请执行此步骤，如需使用工具请说明:
            Tool: [工具名] | Input: [参数]
            或直接输出结果。
            """, step.getDescription(), context);
        
        String response = llm.complete(prompt);
        
        // 检查是否需要调用工具
        if (response.contains("Tool:")) {
            ToolCall call = parseToolCall(response);
            Tool tool = findTool(call.getToolName());
            return tool.execute(call.getInput());
        }
        
        return response;
    }
    
    private boolean needsReplanning(Step step, String result) {
        // 根据结果判断是否需要调整计划
        String prompt = String.format("""
            步骤: %s
            结果: %s
            
            这个结果是否满足预期？是否需要调整后续计划？
            回复: YES/NO
            """, step.getDescription(), result);
        
        return llm.complete(prompt).trim().equalsIgnoreCase("YES");
    }
    
    private List<Step> revisePlan(List<Step> currentPlan, Step failedStep, String result) {
        String prompt = String.format("""
            当前计划: %s
            失败步骤: %s
            失败结果: %s
            
            请修订计划，考虑替代方案:
            """, currentPlan, failedStep, result);
        
        return parsePlan(llm.complete(prompt));
    }
    
    private String generateFinalAnswer(Map<String, Object> context) {
        String prompt = "基于以下执行结果，生成最终答案:\n" + context;
        return llm.complete(prompt);
    }
}

// 辅助类
class Step {
    private String id;
    private String description;
    private List<String> dependencies;
    
    // getters/setters...
}

class ToolCall {
    private String toolName;
    private String input;
    
    // getters...
}
