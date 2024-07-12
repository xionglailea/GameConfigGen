using System;
using System.Linq;

/**
 * ${comment!""}
 */
namespace ${packageName}
{
    public sealed class ${name}
    {
  <#assign x = "">
  <#list fields as field>
        public static int ${field.name} = ${field.value?c}; //${field.alias};
    <#if field?is_last>
        <#assign  x = x + field.value?c>
    <#else >
        <#assign  x = x + field.value?c + ", ">
    </#if>
  </#list>
        public static int[] enums = {${x}};
        public static System.Collections.Generic.HashSet<int> enumSet = new System.Collections.Generic.HashSet<int>(enums.ToList());
    }
}
