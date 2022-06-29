package ${cfgDefine.rootPackage}

import (
    "io/ioutil"
    "os"
    "path"
    "${cfgDefine.rootPackage}/extension"
    "${cfgDefine.rootPackage}/datastream"
<#list modules as module>
    "${cfgDefine.rootPackage}/${module.packageName}"
</#list>
)

var Dir = "data"

func createOctets(fileName string) *datastream.Octets {
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
    ${table.name?cap_first}Data *${table.getModuleName()}.${table.getName()}
        <#else >
            <#if table.dynamic = true>
    ${table.name?cap_first}Map map[${table.indexField.runType.getGoType()}]${table.getModuleName()}.I${table.getName()}
            <#else >
    ${table.name?cap_first}Map map[${table.indexField.runType.getGoType()}]*${table.getModuleName()}.${table.getName()}
            </#if>
        </#if>
    </#if>
</#list>
}

func New() *ConfigMgr {
    ins := new(ConfigMgr)
    var os *datastream.Octets
<#list tables?values as table>
    <#if table.canExport() == true>
    os = createOctets("${table.name?lower_case}.data")
        <#if table.single = true>
    ${table.name?cap_first}Data = extension.${table.readFileType.getGoUnmarshal()}
        <#else >
    <#if table.dynamic = true>
        <#assign interfaceName = "${table.getModuleName()}.I${table.getName()}">
    ins.${table.name?cap_first}Map = make(map[${table.indexField.runType.getGoType()}]${interfaceName})
    <#else >
        <#assign typeName = "*${table.getModuleName()}.${table.getName()}">
    ins.${table.name?cap_first}Map = make(map[${table.indexField.runType.getGoType()}]${typeName})
    </#if>
    ${table.name?uncap_first}List := extension.${table.readFileType.getGoUnmarshal()}
    for e := ${table.name?uncap_first}List.Front(); e != nil; e = e.Next() {
    <#if table.dynamic = true>
        ins.${table.name?cap_first}Map[e.Value.(${interfaceName}).Get${table.indexField.name?cap_first}()] = e.Value.(${interfaceName})
    <#else >
        ins.${table.name?cap_first}Map[e.Value.(${typeName}).${table.indexField.name?cap_first}] = e.Value.(${typeName})
    </#if>
    }
        </#if>
    </#if>
</#list>
    return ins
}