#!/usr/bin/env python3
"""
覆盖率扫描工具 - 检查 AI Agent 面试题库的知识点覆盖情况

用法:
    python coverage-checker.py
    python coverage-checker.py --format json
    python coverage-checker.py --check fundamentals
"""

import os
import re
import json
import argparse
from pathlib import Path
from datetime import datetime
from collections import defaultdict

# 项目根目录
PROJECT_ROOT = Path(__file__).parent.parent

# 模块定义
MODULES = {
    "paradigms": {
        "name": "Agent 开发范式",
        "priority": "P0",
        "topics": [
            "react", "plan-and-solve", "reflexion", "multi-agent", "cot-tot",
            "self-consistency", "tree-of-thoughts"
        ]
    },
    "retrieval": {
        "name": "RAG 检索技术",
        "priority": "P0",
        "topics": [
            "bm25", "hybrid-retrieval", "hyde", "parent-child-index",
            "re-ranker", "context-engineering", "context-optimization",
            "rag-evaluation", "dense-retrieval", "embedding-models"
        ]
    },
    "agent": {
        "name": "Agent 核心组件",
        "priority": "P0",
        "topics": [
            "memory-system", "function-calling", "task-planning", "security",
            "tool-use", "error-handling", "retry-fallback"
        ]
    },
    "frameworks": {
        "name": "开发框架",
        "priority": "P1",
        "topics": [
            "langchain", "llamaindex", "autogen", "framework-comparison"
        ]
    },
    "java-examples": {
        "name": "Java 实现示例",
        "priority": "P2",
        "topics": [
            "ReActAgent", "PlanAndSolveAgent", "MultiAgentOrchestrator", "ReflexionAgent"
        ]
    },
    "fundamentals": {
        "name": "模型基础",
        "priority": "P0",
        "topics": [
            "transformer", "attention-mechanism", "positional-encoding",
            "pretraining", "tokenizer", "model-parameters", "architecture-variants"
        ]
    },
    "evaluation": {
        "name": "评估评测",
        "priority": "P0",
        "topics": [
            "agent-evaluation", "human-vs-auto", "llm-as-judge",
            "rag-evaluation", "hallucination-detection", "ab-testing"
        ]
    },
    "deployment": {
        "name": "部署运维",
        "priority": "P1",
        "topics": [
            "inference-optimization", "quantization", "inference-engines",
            "agent-deployment", "streaming", "load-balancing"
        ]
    },
    "multimodal": {
        "name": "多模态 Agent",
        "priority": "P2",
        "topics": [
            "vlm", "image-text-retrieval", "multimodal-agent", "image-tools"
        ]
    },
    "fine-tuning": {
        "name": "微调与对齐",
        "priority": "P2",
        "topics": [
            "sft", "rlhf", "lora", "instruction-data"
        ]
    }
}


def count_questions_in_file(filepath: Path) -> int:
    """统计文件中的面试题数量"""
    if not filepath.exists():
        return 0
    
    content = filepath.read_text(encoding='utf-8')
    # 匹配 "### 题目 X" 或 "### 题目" 或 "## 题目 X"
    question_patterns = [
        r'###\s*题目\s*\d+',
        r'###\s*题目',
        r'##\s*面试题',
        r'##\s*题目\s*\d+'
    ]
    
    count = 0
    for pattern in question_patterns:
        count += len(re.findall(pattern, content))
    
    # 去重（如果多个模式匹配同一题目）
    return max(count, len(re.findall(r'####?\s*考察点', content)))


def analyze_module(module_name: str) -> dict:
    """分析单个模块的覆盖情况"""
    module_dir = PROJECT_ROOT / module_name
    module_info = MODULES.get(module_name, {})
    
    if not module_dir.exists():
        return {
            "name": module_info.get("name", module_name),
            "priority": module_info.get("priority", "P2"),
            "exists": False,
            "files": [],
            "coverage": 0,
            "question_count": 0
        }
    
    md_files = list(module_dir.glob("*.md"))
    md_files = [f for f in md_files if f.name != "README.md"]
    
    total_topics = len(module_info.get("topics", []))
    covered_topics = 0
    question_count = 0
    
    for topic in module_info.get("topics", []):
        # 检查是否有对应的文件
        topic_file = module_dir / f"{topic}.md"
        if topic_file.exists():
            covered_topics += 1
            question_count += count_questions_in_file(topic_file)
    
    coverage = (covered_topics / total_topics * 100) if total_topics > 0 else 0
    
    return {
        "name": module_info.get("name", module_name),
        "priority": module_info.get("priority", "P2"),
        "exists": True,
        "files": [f.name for f in md_files],
        "coverage": round(coverage, 1),
        "question_count": question_count,
        "covered_topics": covered_topics,
        "total_topics": total_topics
    }


def get_gap_report() -> list:
    """生成缺口报告"""
    gaps = []
    
    for module_name, module_info in MODULES.items():
        module_dir = PROJECT_ROOT / module_name
        
        if not module_dir.exists():
            gaps.append({
                "module": module_name,
                "name": module_info["name"],
                "priority": module_info["priority"],
                "type": "missing_module",
                "missing_topics": module_info["topics"]
            })
            continue
        
        # 检查具体缺失的主题
        missing_topics = []
        for topic in module_info["topics"]:
            topic_file = module_dir / f"{topic}.md"
            if not topic_file.exists():
                missing_topics.append(topic)
        
        if missing_topics:
            gaps.append({
                "module": module_name,
                "name": module_info["name"],
                "priority": module_info["priority"],
                "type": "missing_topics",
                "missing_topics": missing_topics
            })
    
    # 按优先级排序
    priority_order = {"P0": 0, "P1": 1, "P2": 2}
    gaps.sort(key=lambda x: priority_order.get(x["priority"], 3))
    
    return gaps


def print_report(results: dict, gaps: list, format_type: str = "text"):
    """打印报告"""
    
    if format_type == "json":
        print(json.dumps({"modules": results, "gaps": gaps}, indent=2, ensure_ascii=False))
        return
    
    # 文本格式
    print("=" * 60)
    print("AI Agent 面试题库 - 覆盖率扫描报告")
    print(f"扫描时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 60)
    print()
    
    # 总体统计
    total_modules = len(MODULES)
    existing_modules = sum(1 for r in results.values() if r["exists"])
    total_questions = sum(r["question_count"] for r in results.values())
    avg_coverage = sum(r["coverage"] for r in results.values()) / total_modules
    
    print("📊 总体统计")
    print("-" * 40)
    print(f"模块总数: {total_modules}")
    print(f"已创建模块: {existing_modules} ({existing_modules/total_modules*100:.1f}%)")
    print(f"面试题总数: {total_questions}")
    print(f"平均覆盖率: {avg_coverage:.1f}%")
    print()
    
    # 模块详情
    print("📁 模块覆盖详情")
    print("-" * 60)
    print(f"{'模块':<15} {'状态':<8} {'覆盖':<8} {'题目数':<8} {'优先级':<6}")
    print("-" * 60)
    
    for module_name, result in results.items():
        status = "✅" if result["coverage"] >= 70 else "⚠️" if result["coverage"] >= 30 else "❌"
        if not result["exists"]:
            status = "❌"
        print(f"{result['name']:<15} {status:<8} {result['coverage']:>5.1f}% {result['question_count']:>5} {result['priority']:<6}")
    
    print()
    
    # 缺口报告
    if gaps:
        print("🔴 缺口报告（按优先级排序）")
        print("-" * 60)
        
        current_priority = None
        for gap in gaps:
            if gap["priority"] != current_priority:
                current_priority = gap["priority"]
                print(f"\n【优先级 {current_priority}】")
            
            if gap["type"] == "missing_module":
                print(f"  ❌ 缺失模块: {gap['name']} ({gap['module']})")
                print(f"     待补充主题: {', '.join(gap['missing_topics'][:3])}{'...' if len(gap['missing_topics']) > 3 else ''}")
            else:
                print(f"  ⚠️  {gap['name']} - 缺失 {len(gap['missing_topics'])} 个主题")
                for topic in gap["missing_topics"][:3]:
                    print(f"     - {topic}")
                if len(gap["missing_topics"]) > 3:
                    print(f"     ... 等共 {len(gap['missing_topics'])} 个")
        print()
    else:
        print("✅ 所有模块已完整覆盖！")
        print()
    
    # 建议
    print("💡 建议")
    print("-" * 40)
    p0_gaps = [g for g in gaps if g["priority"] == "P0"]
    if p0_gaps:
        print(f"• 优先处理 {len(p0_gaps)} 个 P0 级缺口")
        print(f"• 建议从 '{p0_gaps[0]['name']}' 开始")
    else:
        print("• P0 级缺口已清理完毕，可开始处理 P1 任务")
    print(f"• 运行 'python coverage-checker.py --format json' 获取结构化数据")
    print()


def main():
    parser = argparse.ArgumentParser(description="AI Agent 面试题库覆盖率扫描工具")
    parser.add_argument("--format", choices=["text", "json"], default="text",
                        help="输出格式")
    parser.add_argument("--check", type=str, default=None,
                        help="只检查指定模块")
    args = parser.parse_args()
    
    # 分析模块
    if args.check:
        if args.check not in MODULES:
            print(f"错误: 未知模块 '{args.check}'")
            print(f"可用模块: {', '.join(MODULES.keys())}")
            return
        results = {args.check: analyze_module(args.check)}
    else:
        results = {name: analyze_module(name) for name in MODULES.keys()}
    
    # 生成缺口报告
    gaps = get_gap_report()
    if args.check:
        gaps = [g for g in gaps if g["module"] == args.check]
    
    # 打印报告
    print_report(results, gaps, args.format)


if __name__ == "__main__":
    main()
