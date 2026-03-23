/**
 * Reflexion 范式 Java 实现示例
 * 
 * 自我反思、从错误中学习的 Agent
 */
public class ReflexionAgent extends Agent {
    
    private static final int MAX_ATTEMPTS = 3;
    private List<Reflection> reflectionMemory = new ArrayList<>();
    
    @Override
    public String run(String task) {
        return runWithReflection(task, 0);
    }
    
    private String runWithReflection(String task, int attempt) {
        if (attempt >= MAX_ATTEMPTS) {
            return "Error: 达到最大重试次数";
        }
        
        try {
            // 1. 执行任务
            String result = executeTask(task);
            
            // 2. 自我评估
            Evaluation evaluation = selfEvaluate(task, result);
            
            // 3. 检查是否成功
            if (evaluation.isSuccess()) {
                return result;
            }
            
            // 4. 生成反思
            Reflection reflection = generateReflection(task, result, evaluation);
            reflectionMemory.add(reflection);
            
            // 5. 调整策略重试
            String adjustedTask = adjustTask(task, reflection);
            return runWithReflection(adjustedTask, attempt + 1);
            
        } catch (Exception e) {
            // 记录错误并反思
            Reflection errorReflection = new Reflection(
                "执行异常: " + e.getMessage(),
                "需要添加异常处理和边界检查",
                attempt
            );
            reflectionMemory.add(errorReflection);
            
            // 重试
            return runWithReflection(task, attempt + 1);
        }
    }
    
    private String executeTask(String task) {
        String prompt = String.format("""
            任务: %s
            历史反思: %s
            
            请执行任务，注意避免之前犯过的错误。
            """, task, formatReflections());
        
        return llm.complete(prompt);
    }
    
    private Evaluation selfEvaluate(String task, String result) {
        String prompt = String.format("""
            任务: %s
            执行结果: %s
            
            请评估结果是否满足任务要求:
            1. 是否成功完成？YES/NO
            2. 存在的问题:
            3. 改进建议:
            """, task, result);
        
        String response = llm.complete(prompt);
        return parseEvaluation(response);
    }
    
    private Reflection generateReflection(String task, String result, Evaluation evaluation) {
        String prompt = String.format("""
            任务: %s
            结果: %s
            评估: %s
            
            历史反思:
            %s
            
            请生成结构化的反思:
            - 错误原因:
            - 改进策略:
            - 预防措施:
            """, task, result, evaluation, formatReflections());
        
        String response = llm.complete(prompt);
        return parseReflection(response);
    }
    
    private String adjustTask(String task, Reflection reflection) {
        String prompt = String.format("""
            原始任务: %s
            反思: %s
            
            请基于反思调整任务描述或策略，避免重复犯错。
            """, task, reflection);
        
        return llm.complete(prompt);
    }
    
    private String formatReflections() {
        if (reflectionMemory.isEmpty()) {
            return "无";
        }
        
        return reflectionMemory.stream()
            .map(r -> String.format("[%d] %s: %s", 
                r.getAttempt(), 
                r.getErrorCause(), 
                r.getImprovementStrategy()))
            .collect(Collectors.joining("\n"));
    }
    
    public List<Reflection> getReflectionMemory() {
        return new ArrayList<>(reflectionMemory);
    }
}

// 辅助类
class Reflection {
    private String errorCause;
    private String improvementStrategy;
    private String preventionMeasure;
    private int attempt;
    private long timestamp;
    
    public Reflection(String errorCause, String improvementStrategy, int attempt) {
        this.errorCause = errorCause;
        this.improvementStrategy = improvementStrategy;
        this.attempt = attempt;
        this.timestamp = System.currentTimeMillis();
    }
    
    // getters...
}

class Evaluation {
    private boolean success;
    private String issues;
    private String suggestions;
    
    public boolean isSuccess() {
        return success;
    }
    
    // getters...
}
