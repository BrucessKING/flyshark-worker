package top.bigking.flyshark.utils;

import org.springframework.expression.ParserContext;

public class CustomTemplateParseContext implements ParserContext {

    @Override
    public boolean isTemplate() {
        return true;
    }

    @Override
    public String getExpressionPrefix() {
        return "{{";
    }

    @Override
    public String getExpressionSuffix() {
        return "}}";
    }
}
