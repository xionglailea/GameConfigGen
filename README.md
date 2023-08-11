# GameConfigGen
配置生成工具，支持java，c#，go，typescript代码生成。

## 一、数据格式定义

一个典型的xml定义文件结构如下

``` xml
<module>
    <packageName>test5</packageName>

    <enum name="EnumBean">
        <field name="EASY" alias="简单" value="1"/>
        <field name="HARD" alias="困难" value="2"/>
    </enum>

    <const name="ConstBean">
        <field name="BornPosX" type="int" value="1"/>
        <field name="BornPosY" type="int" value="10"/>
    </const>

    <bean name="DetailBean">
        <field name="x" type="int"/>
        <field name="y" type="int"/>
        <field name="z" type="int"/>
    </bean>

    <bean name="BaseShape">
        <field name="pos" type="int"/>
        <child name="Rect">
            <field name="width" type="int"/>
            <field name="height" type="int"/>
        </child>
        <child name="Circle">
            <field name="radius" type="int"/>
        </child>
    </bean>

    <table name="AllTypeTest" comment="测试表" inputFile="all_test.xlsx" index="intValue">
        <field name="intValue" type="int" comment="int类型"/>
        <field name="intRefValue" type="int" ref="Item" comment="int类型引用检查"/>
        <field name="floatValue" type="float" range="0,4" comment="float类型"/>
        <field name="textValue" type="text" comment="text类型 会自动本地化转化"/>
        <field name="longValue" type="long" comment="long类型"/>
        <field name="boolValue" type="bool" comment="bool类型"/>
        <field name="stringValue" type="string" comment="string类型"/>
        <field name="enumValue" type="EnumBean" comment="枚举类型字段"/>
        <field name="beanValue" type="DetailBean" comment="bean类型字段"/>
        <field name="list1NormalValue" type="list,int" comment="list类型字段"/>
        <field name="list2NormalValue" type="list,int" sep="," comment="list类型字段"/>
        <field name="list1BeanValue" type="list,DetailBean" comment="list类型字段"/>
        <field name="list2BeanValue" type="list,DetailBean"  multiRow="true" comment="list类型字段"/>
        <field name="list3BeanValue" type="list,DetailBean" sep="," comment="list类型字段"/>
        <field name="list4BeanValue" type="list,DetailBean" sep="|," comment="list类型字段"/>
        <field name="map1NormalValue" type="map,int,int" comment="map类型字段"/>
        <field name="map6NormalValue" type="map,int,int" sep="|" comment="map类型字段"/>
        <field name="map2BeanValue" type="map,int,DetailBean" multiRow="true" comment="map类型字段"/>
        <field name="map3BeanValue" type="map,int,DetailBean" sep="," comment="map类型字段"/>
        <field name="map4BeanValue" type="map,int,DetailBean" sep="|," comment="map类型字段"/>
        <!-- 不支持map中嵌套map，map中嵌套list只支持一层,分割符最多定义两个，简化复杂度 -->
        <field name="map5ListBeanValue" type="map,int,list,DetailBean" sep="|," comment="map类型字段"/>
        <!-- 动态字段 -->
        <field name="dynamicBean" type="BaseShape" sep="," comment="动态字段"/>
        <field name="listDynamicBean" type="list,BaseShape" sep="," comment="list动态字段"/>
    </table>
</module>
```


### 1.模块标签`<module>`, 包名标签`<packageName>` 
这两个成对出现，表示定义了一个模块，该模块内的生成文件最后都会在同一个包内。

### 2.枚举结构定义`<enum>`标签
定义一组枚举变量，一般供别的字段引用。

### 3.常量结构定义`<const>`标签
定义一组简单常量，可以自己指定数据类型和数据值。

### 4.字段定义`<field>`标签
目前支持的字段类型有简单类型int，float，long，string，text；复杂类型 list，map，enum，自定义的结构体；
ref标签，表示该字段的引用表。当我们在字段定义中引用别的包中定义的结构时，使用 包名.结构名的形式。 seq标签，表示数据填在一个单元格内的分隔符号，multiRow标签，表示该字段会使用多行存放
path标签，只对string类型有效，表明该字段是一个资源路径，需要我们启动时指定root_dir路径，最终会去检查 root_dir/{field_value} 对应的资源是否存在。
其中text类型的数据，本质上也是字符串，只是在导出的时候会自动转换成本地化的文本，如果没有指定具体的本地化文本，会使用l10n中origin字段作为本地化文本。

#### 4.1 list
list的值类型支持嵌套定义，比如list,int 表示int类型的列表，list,bean1 表示自定义类型bean1的列表
list,list,int 表示外层list的值类型是int类型的列表

#### 4.2 map
map的key只支持简单类型，map的value也支持嵌套定义，map,int,int表示键值都为int类型
map,int,list,int表示值类型为int类型的列表。

### 5.普通数据结构定义`<bean>`标签
一组字段的集合，定义好数据结构后，就可以当作普通类型，供字段定义使用。
我们可以在&lt;bean&gt;中使用&lt;child&gt;标签，来表示后者为前者的子类定义，子类型会继承父类中定义的所有字段
比如上面的Equip结构中定义了两个子类型ShoeEquip和WeaponEquip，两个子类型中分别定义了自己需要的字段，这样可以减少
在填写数据时对冗余字段的处理。

比如我们定义一个字段类型为Equip，该类型是一个抽象类型，具体我们使用的是什么类型，在真正填写数据的时候才会指定，
通过这种方式，我们只需要在代码中将Equip的所有子类型都处理，这样不论怎么修改数据，都不会影响使用该定义的地方。
将其称之为配置的多态。
特别是游戏中，使用一些条件检查的地方，我们可能会定义一个抽象类型Condition，具体的子类型可能有LvlCondition，VipCondition，
，CompositeCondition等各种条件，我们在使用的地方可能会是各种条件的组合，具体使用什么条件，根据配置的数据而定，但在定义的地方
只需要定义该字段是一个Condition类型

### 6.表格数据结构定义`<table>`标签
真正读取数据的地方，该定义跟&lt;bean&gt;定义的区别在于table中需要指定input字段，标识数据的来源。我们可以指定多个数据源和数据目录
作为表格数据的来源。


### 7.`<group>`标签
该定义可以用在字段定义和表格定义上，根据需求到处数据，比如标识某个field  group="server"，那么会将该数据导出到服务器，不会给客户端导出。
有client，server，editor（table上使用，表示可以用编辑器编辑）三个选项

## 二、数据定义

### 1.数据源，支持excel和json。
excel中添加注释行只需要在行的第一个单元格头添加##。
填写数据时，按照定义的字段一个个按顺序填写，按照我们定义数据的方式，去除了冗余字段，
所以每个字段应该都是有一个有意义的值的。
excel中，每个页签的前三行是固定的格式，第一行：页签说明；第二行：字段名，需要和xml中定义的字段名一致；第三行：字段说明；
填写动态类型的字段，需要先填写具体子类的名字，比如上面定义的Equip类型，在填写数据时，先填写
WeaponEquip，然后填写Equip中定义的字段，接着填写WeaponEquip中定义的字段。

对于json数据，如果是动态数据类型，json会使用subType：子类名 来标识具体的子类；如果是Map类型的数据结构
json会使用JsonArray[{"key" mapkey, "value" mapValue}]来表示，其它的数据类型使用正常
的json结构就能表示。

### 2.使用单一数据源
不支持混合数据源，即不支持同一个表的数据一部分来自json，一部分来自excel，如果表定义的group字段有"editor"
的标签，该表的所有数据应该都来自json，避免混合编辑改变excel的结构。普通表的数据都来自excel。

### 3.编辑器
提供了一个简单的编辑器用来编辑复杂数据，特别对于嵌套层级深的情况，比如Ai数据，任务数据等。
编辑器填写的数据最终会保存为json文件，文件名为 表名_主键id 形式。

## 三、使用方式
有三种工作模式

1.检查数据模式，检查当前数据的正确性，包括字段定义正确性检查，数据引用检查，数据格式检查。 2.生成模式，包括只生成代码，生成代码的同时生成数据。 3.编辑器模式，筛选出标识了editor的表格，能够添加，删除，修改数据。


2021/9/5 开始将excel数据源，改为列固定的结构 excel格式的数据约束 第一行 注释 第二行 变量名 第三行 注释

2021/10/9 准备重构：编辑器数据格式和解析的excel数据 使用adapter的方式耦合。

2022/1/19 完成初版go的生成代码，相对java和c#的生成方式，有较大的不同。
准备重构不同语言的生成格式，现在基本上全在IType中定义的接口。

2022/12/22 完成typescript的代码生成。
联合主键
资源path校验
本地化支持
