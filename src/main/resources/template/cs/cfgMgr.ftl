using System;

namespace ${cfgDefine.rootPackage}
{
    public sealed class CfgMgr
    {
        private static String _dir {get; set;}= "config";

        public static volatile CfgMgr ins;

        private datastream.Octets createOctets(String file)
        {
            return new datastream.Octets(System.IO.File.ReadAllBytes(_dir + "/" + file));
        }

<#list tables?values as table>
    <#if table.canExport() == true>
    <#if table.single = true>
        public ${table.readFileType.getCsType()} ${table.name?uncap_first};
    <#else >
        public ${table.readFileType.getCsType()} ${table.name?uncap_first}List;
        public System.Collections.Generic.Dictionary<${table.indexField.runType.getCsType()}, ${table.fullName}> ${table.name?uncap_first}Map = new System.Collections.Generic.Dictionary<${table.indexField.runType.getCsType()}, ${table.fullName}>();
    </#if>
    </#if>
</#list>
        public static void Load()
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
            ${table.name?uncap_first} = ${table.readFileType.getCsUnmarshal()};
    <#else >
            ${table.name?uncap_first}List = ${table.readFileType.getCsUnmarshal()};
            foreach (var temp in ${table.name?uncap_first}List)
            {
                ${table.name?uncap_first}Map.Add(temp.${table.indexField.name}, temp);
            }
    </#if>
    </#if>
</#list>
        }

}
}
