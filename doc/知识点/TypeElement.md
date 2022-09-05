## 接口 TypeElement

**所有超级接口：**[Element](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/Element.html "javax.lang.model.element 中的接口")---

```
public interface TypeElementextends Element
```

表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注释类型是一种接口。

[]()`TypeElement` 表示一个类或接口 *元素* ，而 [`DeclaredType`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/DeclaredType.html "javax.lang.model.type 中的接口") 表示一个类或接口 *类型* ，后者将成为前者的一种使用（或 *调用* ）。这种区别对于一般的类型是最明显的，对于这些类型，单个元素可以定义一系列完整的类型。例如，元素 `java.util.Set` 对应于参数化类型 `java.util.Set<String>` 和 `java.util.Set<Number>`（以及其他许多类型），还对应于原始类型 `java.util.Set`。

此接口每一个都返回元素列表的方法都将按照这些元素在程序信息底层源代码中的自然顺序返回它们。例如，如果信息的底层源代码是 Java 源代码，则按照源代码顺序返回这些元素。

**从以下版本开始：**1.6**另请参见：**[`DeclaredType`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/DeclaredType.html "javax.lang.model.type 中的接口")
