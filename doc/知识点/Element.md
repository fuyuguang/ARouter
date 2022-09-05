[编译时注解学习一之 Element元素](https://www.jianshu.com/p/ba5a4b159664)
[]()
[]()
[]()
[]()
[]()
[]()
[]()

# Element

Element 是一个接口，它只在编译期存在和Type有区别, 是一个节点元素，

### Element的子类介绍：

TypeElement就是代表一个类或者接口， 例如MainActivity…
PackageElement代表包元素，  表示包程序元素
VariableElement代表参数元素（字段、枚举、形参等），  表示字段，枚举常量，方法或者构造函数参数，局部变量，资源变量或者异常参数
ExecutableElement代表方法元素（某个类 接口的函数）    表示类或者接口中的方法，构造函数或者初始化器
TypeParameterElement   表示类，接口，方法的泛型类型例如T。
总体来看，所有的节点都可以看做是Element

### Element常用的API

TypeMirror asType();
//返回Element的类型
ElementKind getKind();

Set<Modifier> getModifiers();

Name getSimpleName();

//返回当前元素的父元素
Element getEnclosingElement();
//返回当前元素的全部子元素
List<? extends Element> getEnclosedElements();

### Element的类型包括如下几种：

```java
public enum ElementKind {
    PACKAGE,
    ENUM,
    CLASS,
    ANNOTATION_TYPE,
    INTERFACE,
    ENUM_CONSTANT,
    FIELD,
    PARAMETER,
    LOCAL_VARIABLE,
    EXCEPTION_PARAMETER,
    METHOD,
    CONSTRUCTOR,
    STATIC_INIT,
    INSTANCE_INIT,
    TYPE_PARAMETER,
    OTHER,
    RESOURCE_VARIABLE,
    MODULE;

    private ElementKind() {
    }
	//判断是不是类
    public boolean isClass() {
        return this == CLASS || this == ENUM;
    }
	//是不是接口
    public boolean isInterface() {
        return this == INTERFACE || this == ANNOTATION_TYPE;
    }
	//是不是属性
    public boolean isField() {
        return this == FIELD || this == ENUM_CONSTANT;
    }
}
```

## javax.lang.model.element

接口 Element

**所有已知子接口：**[ExecutableElement](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/ExecutableElement.html "javax.lang.model.element 中的接口"), [PackageElement](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/PackageElement.html "javax.lang.model.element 中的接口"), [TypeElement](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/TypeElement.html "javax.lang.model.element 中的接口"), [TypeParameterElement](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/TypeParameterElement.html "javax.lang.model.element 中的接口"), [VariableElement](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/VariableElement.html "javax.lang.model.element 中的接口")---

```
public interface Element
```

表示一个程序元素，比如包、类或者方法。每个元素都表示一个静态的语言级构造（不表示虚拟机的运行时构造）。

元素应该使用 [`equals(Object)`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/Element.html#equals(java.lang.Object)) 方法进行比较。不保证总是使用相同的对象表示某个特定的元素。

要实现基于 `Element` 对象类的操作，可以使用 [visitor](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/ElementVisitor.html "javax.lang.model.element 中的接口") 或者使用 [`getKind()`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/element/Element.html#getKind()) 方法的结果。使用 `instanceof` 确定此建模层次结构中某一对象的有效类 *未必* 可靠，因为一个实现可以选择让单个对象实现多个 `Element` 子接口。

**从以下版本开始：**1.6**另请参见：**[`Elements`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/util/Elements.html "javax.lang.model.util 中的接口"), [`TypeMirror`](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/type/TypeMirror.html "javax.lang.model.type 中的接口")

