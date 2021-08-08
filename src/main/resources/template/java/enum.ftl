package ${packageName};
import java.util.*;

/**
* ${comment!""}
 */
public final class ${name} {
  <#assign x = "">
  <#list fields as field>
    public static final int ${field.name} = ${field.value}; //${field.alias};
    <#if field?is_last>
        <#assign  x = x + field.value>
    <#else >
        <#assign  x = x + field.value + ", ">
    </#if>
  </#list>
    public static final int[] enums = new int[]{${x}};
    public static final HashSet<Integer> enumSet = new HashSet<>(Arrays.stream(enums).boxed().collect(java.util.stream.Collectors.toList()));
}
