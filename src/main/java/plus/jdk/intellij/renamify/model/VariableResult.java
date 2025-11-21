package plus.jdk.intellij.renamify.model;

import lombok.Data;

@Data
public class VariableResult  {

    /**
     * 变量的名称。
     */
    private String variable;

    /**
     * 变量结果的原因说明。
     */
    private String reason;
}
