package ${cfgDefine.rootPackage};


import lombok.Getter;

@Getter
public final class CfgMgr {
    private static String _dir = "config";

    public static void setDir(String dir) {
        _dir = dir;
    }

    public static volatile CfgMgr ins;

    private datastream.Octets createOctets(String file) {
        try {
            return datastream.Octets.wrap(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(_dir + "/" + file)));
        } catch (java.io.IOException e) {
              throw new RuntimeException(e);
        }
    }

<#list tables?values as table>
    <#if table.canExport() == true>
    <#if table.single = true>
    private ${table.readFileType.getJavaType()} ${table.name?uncap_first};
    <#else >
    private ${table.readFileType.getJavaType()} ${table.name?uncap_first}List;
    private java.util.Map<${table.indexField.runType.getJavaBoxType()}, ${table.fullName}> ${table.name?uncap_first}Map = new java.util.HashMap<>();
    </#if>
    </#if>
</#list>

    public static void load() {
        ins = new CfgMgr();
    }


    private CfgMgr() {
        datastream.Octets os;
<#list tables?values as table>
    <#if table.canExport() == true>
        os = createOctets("${table.name?lower_case}.data");
    <#if table.single = true>
        ${table.name?uncap_first} = ${table.readFileType.getUnmarshal()};
    <#else >
        ${table.name?uncap_first}List = ${table.readFileType.getUnmarshal()};
        for (var temp : ${table.name?uncap_first}List) {
            ${table.name?uncap_first}Map.put(temp.get${table.indexField.name?cap_first}(), temp);
        }
    </#if>
    </#if>
</#list>
    }

}
