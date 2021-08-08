#GameConfigGen
1.支持java代码和c#代码生成。

2.数据源，支持来自excel和json，json数据编辑使用编辑器

3.如果是动态数据类型，json会使用subType：子类名 来标识具体的子类；如果是Map类型的数据结构
json会使用JsonArray[{"key" mapkey, "value" mapValue}]来表示，其它的数据类型使用正常
的json结构就能表示。

4.不支持混合数据源，即不支持同一个表的数据一部分来自json，一部分来自excel，如果表定义的group字段有"editor"
的标签，该表的所有数据应该都来自json，避免混合编辑改变excel的结构。普通表的数据都来自excel。