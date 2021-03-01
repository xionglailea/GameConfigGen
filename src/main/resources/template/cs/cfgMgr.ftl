using System;

namespace ${cfgDefine.rootPackage}
{
    public sealed class CfgMgr
    {
        private static String _dir {get; set;}= "config";

        public static volatile CfgMgr ins;

        private datastream.Octets createOctets(String file)
        {
            return datastream.Octets.wrap(System.IO.File.ReadAllBytes(_dir + "/" + file)));
        }

<#list tables?values as table>
    <#if table.canExport() == true>
    <#if table.single = true>
        private ${table.readFileType.getCsType()} ${table.name?uncap_first};
    <#else >
        private ${table.readFileType.getCsType()} ${table.name?uncap_first}List;
        private System.Collections.Generic.Dictionary<${table.indexField.runType.getCsType()}, ${table.fullName}> ${table.name?uncap_first}Map = new System.Collections.Generic.Dictionary<${table.indexField.runType.getCsType()}, ${table.fullName}>();
    </#if>
    </#if>
</#list>
        public static void load()
        {
            ins = new CfgMgr();
        }

        private CfgMgr()
        {
            datastream.Octets os;
<#list tables?values as table>
    <#if table.canExport() == true>
            os = createOctets("${table.name?lower_case}.data");
    <#if table.single = true>
            ${table.name?uncap_first} = ${table.readFileType.getUnmarshal()};
    <#else >
            ${table.name?uncap_first}List = ${table.readFileType.getUnmarshal()};
            foreach (var temp in ${table.name?uncap_first}List)
            {
                ${table.name?uncap_first}Map.put(temp.get${table.indexField.name?cap_first}(), temp);
            }
    </#if>
    </#if>
</#list>
        }

}
}
