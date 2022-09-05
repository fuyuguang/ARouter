### TypeMirror 元素类型

### TypeMirror是用来判断Element的类型，是基本数据类型，还是类，还是方法…

```java
TypeMirror typeMirror = element.asType()
    if(typeMirror.kind == TypeKind.INT){
        str = "intent.getIntExtra(%S,0)"
    }else if(typeMirror.kind == TypeKind.BOOLEAN){
        str = "intent.getBooleanExtra(%S,false)"
    }
```

### TypeMirror怎么判断一个类是否继承自Activity

通过TypesUtils 的 isSubtype 方法

[]()

[]()

[]()

[]()
[]()

[]()
[]()
[]()
[]()
[]()
[]()
[]()
[]()
[]()
[]()

> ## 接口 TypeMirror

**所有已知子接口：**[ArrayType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/ArrayType.html "javax.lang.model.type 中的接口"), [DeclaredType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/DeclaredType.html "javax.lang.model.type 中的接口"), [ErrorType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/ErrorType.html "javax.lang.model.type 中的接口"), [ExecutableType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/ExecutableType.html "javax.lang.model.type 中的接口"), [NoType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/NoType.html "javax.lang.model.type 中的接口"), [NullType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/NullType.html "javax.lang.model.type 中的接口"), [PrimitiveType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/PrimitiveType.html "javax.lang.model.type 中的接口"), [ReferenceType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/ReferenceType.html "javax.lang.model.type 中的接口"), [TypeVariable](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/TypeVariable.html "javax.lang.model.type 中的接口"), [WildcardType](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/WildcardType.html "javax.lang.model.type 中的接口")---

```
public interface TypeMirror
```

表示 Java 编程语言中的类型。这些类型包括基本类型、声明类型（类和接口类型）、数组类型、类型变量和 null 类型。还可以表示通配符类型参数、executable 的签名和返回类型，以及对应于包和关键字 `void` 的伪类型。

应该使用 [`Types`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/util/Types.html "javax.lang.model.util 中的接口") 中的实用工具方法比较这些类型。不保证总是使用相同的对象表示某个特定的类型。

要实现基于 `TypeMirror` 对象类的操作，可以使用 [visitor](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/TypeVisitor.html "javax.lang.model.type 中的接口") 或者使用 [`getKind()`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/TypeMirror.html#getKind()) 方法的结果。使用 `instanceof` 确定此建模层次结构中某一对象的有效类 *未必* 可靠，因为一个实现可以选择让单个对象实现多个 `TypeMirror` 子接口。











