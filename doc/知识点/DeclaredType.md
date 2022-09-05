
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


接口 DeclaredType    是  TypeMirror 的子类

所有超级接口：
ReferenceType, TypeMirror
所有已知子接口：
ErrorType
public interface DeclaredType
extends ReferenceType
表示某一声明类型，是一个类 (class) 类型或接口 (interface) 类型。这包括参数化的类型（比如 java.util.Set<String>）和原始类型。

TypeElement 表示一个类或接口元素，而 DeclaredType 表示一个类或接口类型，后者将成为前者的一种使用（或调用）。有关这种区别的更多信息，请参见 TypeElement。

可以使用 Types.directSupertypes(TypeMirror) 方法找到已声明类型的超类型（类类型和接口类型）。此方法返回所有类型参数都被替换的超类型。

还可以使用此接口表示交集 (intersection) 类型。交集类型在程序中是隐式声明的，而不是显式声明。例如，类型参数 <T extends Number & Runnable> 的边界是一个交集类型。它由 DeclaredType 表示，使用 Number 作为其超类并使用 Runnable 作为其独立超接口。

从以下版本开始：