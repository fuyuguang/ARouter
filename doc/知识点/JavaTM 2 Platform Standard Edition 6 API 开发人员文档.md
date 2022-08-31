[JavaTM 2 Platform Standard Edition 6 API 开发人员文档](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/overview-summary.html)

[javax.lang.model.util.Types.isSameType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/util/Types.html#isSameType(javax.lang.model.type.TypeMirror,%20javax.lang.model.type.TypeMirror))

```

boolean isSameType(TypeMirror t1,
                   TypeMirror t2)
测试两个 TypeMirror 对象是否表示同一类型。
警告：如果此方法两个参数中有一个表示通配符，那么此方法将返回 false。因此，通配符与其本身的类型不同。初看起来这可能让人感到很奇怪，但考虑到编译器一定会拒绝以下示例时，这就很合理了：


List<?> list = new ArrayList<Object>();
list.add(list.get(0));

参数：
t1 - 第一种类型
t2 - 第二种类型
返回：
当且仅当两种类型相同时返回 true

```

[javax.lang.model.util.Types.isSubtype](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/util/Types.html#isSameType(javax.lang.model.type.TypeMirror,%20javax.lang.model.type.TypeMirror))

```
isSubtype
boolean isSubtype(TypeMirror t1,
                  TypeMirror t2)
测试一种类型是否是另一个类型的子类型。任何类型都被认为是其本身的子类型。
参数：
t1 - 第一种类型
t2 - 第二种类型
返回：
当且仅当第一种类型是第二种类型的子类型时返回 true
抛出：
IllegalArgumentException - 如果给定一个 executable 或 package 类型

```
