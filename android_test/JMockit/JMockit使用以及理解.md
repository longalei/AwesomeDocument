基于Junit或者TestNG上的第三方测试框架

Recoding  `Expectations`,Verifications `Verifications`

注解参数一览

1、@BeforeClass所修饰的方法在所有方法加载前执行，而且他是静态的在类加载后就会执行该方法，在内存中只有一份实例，适合用来加载配置文件。

2、@AfterClass所修饰的方法在所有方法执行完毕之后执行，通常用来进行资源清理，例如关闭数据库连接。

3、@Before和@After在每个测试方法执行前都会执行一次。

4、@Test(excepted=XX.class) 在运行时忽略某个异常。

5、@Test(timeout=毫秒) 允许程序运行的时间。

6、@Ignore 所修饰的方法被测试器忽略。



**1. @Mocked**

@Mocked不仅能修饰一个类，也能修饰接口。@Mocked修饰的类/接口，是告诉JMockit，帮我生成一个Mocked对象，这个对象方法（包含静态方法)返回默认值。

**2. @Tested & @Injectable**

@Injectable 也是告诉 JMockit生成一个Mocked对象，但@Injectable只是针对其修饰的实例，而@Mocked是针对其修饰类的所有实例。此外，@Injectable对类的静态方法，构造函数没有影响。因为它只影响某一个实例。

@Tested修饰的类，表示是我们要测试对象,如果该对象没有赋值，JMockit会去实例化它。

@Tested & @Injectable通常搭配使用。若@Tested的构造函数有参数，则JMockit通过在测试属性、测试参数中查找@Injectable修饰的Mocked对象注入@Tested对象的构造函数来实例化，不然，则用无参构造函数来实例化。

除了构造函数的注入，JMockit还会通过属性查找的方式，把@Injectable对象注入到@Tested对象中。注入的匹配规则：先类型，再名称(构造函数参数名，类的属性名)。若找到多个可以注入的@Injectable，则选择最优先定义的@Injectable对象。当然，我们的测试程序要尽量避免这种情况出现。因为给哪个测试属性/测试参数加@Injectable，是人为控制的。

**3. @Capturing**

@Capturing主要用于子类/实现类的Mock, 我们只知道父类或接口时，但我们需要控制它所有子类的行为时，子类可能有多个实现（可能有人工写的，也可能是AOP代理自动生成时）。就用@Capturing。



###三种不同的Mock annotations

##### @Mocked

> 模仿所有的方法和构造，整个类都可以使用

###### @Injectable

> 有约束的模拟实例方法

##### @Capturing

> 扩展类实现了模拟接口或者拓展模拟类的子类



[参考链接](https://www.jianshu.com/p/37de454c5f34)

