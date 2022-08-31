### TypeMirror 元素类型

### TypeMirror是用来判断Element的类型，是基本数据类型，还是类，还是方法…


``` java

   TypeMirror typeMirror = element.asType()
    if(typeMirror.kind == TypeKind.INT){
        str = "intent.getIntExtra(%S,0)"
    }else if(typeMirror.kind == TypeKind.BOOLEAN){
        str = "intent.getBooleanExtra(%S,false)"
    }

```




### TypeMirror怎么判断一个类是否继承自Activity

通过TypesUtils 的 isSubtype 方法


