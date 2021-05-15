#### Q：关于Android中的R文件？

1.Android主项目中R文件的生成和Module中R文件的生成区别？

> 关于id,主项目中生成的是个常量，module中生成的是个变量
>
> 因此：module中对id处理时不能使用swich  case语句

2.Butterknife中生成R2文件？

> 因为注解中引用的是常量，因此为了避免语法检查，Butterknife会自动复制一份R文件生成R2文件，并将所有id定义成final型常量。

