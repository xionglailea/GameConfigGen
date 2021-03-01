using System;
using System.Linq;

/**
 * ${comment}
 */
namespace ${packageName}
{
    public sealed class ${name}
    {
  <#assign x = "">
  <#list fields as field>
        public const int ${field.name} = ${field.value}; //${field.alias};
    <#if field?is_last>
        <#assign  x = x + field.value>
    <#else >
        <#assign  x = x + field.value + ", ">
    </#if>
  </#list>
        public const int[] enums = new int[]{${x}};
        public const System.Collections.Generic.HashSet<int> enumSet = new System.Collections.Generic.HashSet<int>(enums.ToHashSet());
    }
}
