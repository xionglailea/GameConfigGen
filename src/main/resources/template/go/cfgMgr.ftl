package ${cfgDefine.rootPackage}

import (
"io/ioutil"
"os"
"path"
"cfg/ext"
"datastream"
<#list modules as module>
    "${module.packageName}"
</#list>
)

var Dir = "config"

func CreateOctets(fileName string) *datastream.Octets {
file, err := os.Open(path.Join(Dir, fileName))
if err != nil {
panic("can not find file " + fileName)
}
data, _ := ioutil.ReadAll(file)
return datastream.NewOctetsByData(data)
}

type ConfigMgr struct {
<#list tables?values as table>
    <#if table.canExport() == true>
        <#if table.single = true>
            ${table.name?cap_first}Data *${table.getPackageName()}.${table.getName()}
        <#else >
            <#if table.dynamic = true>
                ${table.name?cap_first}Map = map[${table.indexField.runType.getGoType()}]${table.getPackageName()}.I${table.getName()}
            <#else >
                ${table.name?cap_first}Map = map[${table.indexField.runType.getGoType()}]*${table.getPackageName()}.${table.getName()}
            </#if>
        </#if>
    </#if>
</#list>
}

func New() *ConfigMgr {
ins := new(ConfigMgr)
<#list tables?values as table>
    <#if table.canExport() == true>
        os = createOctets("${table.name?lower_case}.data");
        <#if table.single = true>
            ${table.name?cap_first}Data = ${table.readFileType.getGoUnmarshal()};
        <#else >
            ${table.name?uncap_first}List := ${table.readFileType.getGoUnmarshal()};
            for (e := ${table.name?uncap_first}List.Front(); e != nil; e = e.Next() {
            ins.${table.name?cap_first}Map[e.Value.${table.indexField.name?cap_first}] = e.Value
            }
        </#if>
    </#if>
</#list>


return ins
}