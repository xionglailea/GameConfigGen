package ${packageName};

/**
* ${comment}
*/
public final class ${name} {

<#list fields as field>
  public static final ${field.runType.getJavaType()} ${field.name} = <#if field.type == "string">"${field.value}" <#else >${field.value}</#if>;
</#list>

}