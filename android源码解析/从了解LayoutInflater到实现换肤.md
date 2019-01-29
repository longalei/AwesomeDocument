#### LayoutInflater

 我们经常使用LayoutInflater，比如：

 - LayoutInflater.from()

 - getLayoutInflater().inflate()
 - context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
 - ...

 而关于LayoutInflater的信息还有这么几点，以下是做个简要的概括。

 - [x] ###### LayoutInflater是个抽象类

   关于它的具体实现类是PhoneLayoutInflater.在LayoutInflater中有个抽象方法cloneInContext().在PhoneLayoutInflater具体实现是重新创建了一个新的PhoneLayoutInflater。关于

   ```
   LayoutInflater LayoutInflater =
       (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   ```

   当通过context获取到系统的任意系统服务时，可在SystemServiceRegistry找到具体的服务注册。比如

   ```
   registerService(Context.LAYOUT_INFLATER_SERVICE, LayoutInflater.class,
       new CachedServiceFetcher<LayoutInflater>() {
   @Override
   public LayoutInflater createService(ContextImpl ctx) {
       return new PhoneLayoutInflater(ctx.getOuterContext());
   }});
   ```

- [x] ###### inflate() 

  当LayoutInflater在使用XmlPullParser解析XML到具体View的过程中出现的核心方法createViewFromTag().

  ```
  //期间，我们可以通过mFactory2、mFactory对xml文件实例到View的过程中进行拦截，从而实现自己所约定的一套更换View属性的控件组，这也变相的实现了我们所言的换肤。
  
  优先级：实现的Factory接口>>framework mPrivateFactory onCreateView>>LayoutInflater onCreateView
  try {
  View view;
  if (mFactory2 != null) {
      view = mFactory2.onCreateView(parent, name, context, attrs);
  } else if (mFactory != null) {
      view = mFactory.onCreateView(name, context, attrs);
  } else {
      view = null;
  }
  
  //一般在Activity attach中调用setPrivateFactory(),而Fragment onGetLayoutInflater中
  if (view == null && mPrivateFactory != null) {
      view = mPrivateFactory.onCreateView(parent, name, context, attrs);
  }
  
  if (view == null) {
      final Object lastContext = mConstructorArgs[0];
      mConstructorArgs[0] = context;
      try {
          if (-1 == name.indexOf('.')) {
          //最后都会走createView方法，createView会实例化对应的每一个view
              view = onCreateView(parent, name, attrs);
          } else {
              view = createView(name, null, attrs);
          }
      } finally {
          mConstructorArgs[0] = lastContext;
      }
  }
  
  return view;
  } catch (InflateException e) {
  throw e;
  
  } 
  ```

- [x] ###### Factory、Factory2

  Factory、Factory2都是接口，Factory2(api 11 added)继承Factory.唯一的区别是Factory2新增了参数，(新增的View parent参数主要用于Fragment中，具体可看FragmentManager onCreateView)对于Activity而言，默认返回的是null.对Activity而言，去实现Factory2接口的是在AppCompatActivity中通过AppCompatDelegate实现。对于Fragment而言，getLayoutInflater中默认对Factory2接口进行了实现。

  ```
  public interface Factory {
      /**
       * Hook you can supply that is called when inflating from a LayoutInflater.
       * You can use this to customize the tag names available in your XML
       * layout files.
       *
       * <p>
       * Note that it is good practice to prefix these custom names with your
       * package (i.e., com.coolcompany.apps) to avoid conflicts with system
       * names.
       *
       * @param name Tag name to be inflated.
       * @param context The context the view is being created in.
       * @param attrs Inflation attributes as specified in XML file.
       *
       * @return View Newly created view. Return null for the default
       *         behavior.
       */
      public View onCreateView(String name, Context context, AttributeSet attrs);
  }
  
  public interface Factory2 extends Factory {
      /**
       * Version of {@link #onCreateView(String, Context, AttributeSet)}
       * that also supplies the parent that the view created view will be
       * placed in.
       *
       * @param parent The parent that the created view will be placed
       * in; <em>note that this may be null</em>.
       * @param name Tag name to be inflated.
       * @param context The context the view is being created in.
       * @param attrs Inflation attributes as specified in XML file.
       *
       * @return View Newly created view. Return null for the default
       *         behavior.
       */
      public View onCreateView(View parent, String name, Context context, AttributeSet attrs);
  }
  ```

---

从上面我们可以大概明白LayoutInflater中已经为我们提供了接口让我们去实现我们想实例化的任何View,同时，我们也可以猜想在AppCompatActivity中我们知道所有相关View都提供了新的特性，比如TintMode等等，而这些新的特性的提供正式通过LayoutInflater的Factory的接口进行提供的。

#### AppCompatActivity、 AppCompatDelegate抽象类

在AppCompatActivity的onCreate()方法中会执行AppCompatDelegate的installViewFactory()，正是这个方法，对Factory接口进行了实现。名言说的好，照葫芦画瓢，只有我们弄清楚AppCompatDelegate是如何做的，那么我们也就可以一步步打造属于我们自己的各种多变的View.

探究源码，关于Factory的具体实现是在AppCompatDelegate的实现类AppCompatDelegateImplV9中：

```
//实现了Factory2接口
class AppCompatDelegateImplV9 extends AppCompatDelegateImplBase
        implements MenuBuilder.Callback, LayoutInflater.Factory2 {
        
 ...
 
 //实现了installViewFactory，并且将LayoutInflater设置了Factory2
 //LayoutInflaterCompat兼容了api 21之前的getFactory2()
@Override
public void installViewFactory() {
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    if (layoutInflater.getFactory() == null) {
        LayoutInflaterCompat.setFactory2(layoutInflater, this);
    } else {
        if (!(layoutInflater.getFactory2() instanceof AppCompatDelegateImplV9)) {
            Log.i(TAG, "The Activity's LayoutInflater already has a Factory installed"
                    + " so we can not install AppCompat's");
        }
    }
 }
}
```

紧接着，继续看其实现了Factory2中的onCreateView方法（一个带parent参数,一个不带。）：

- [x] ##### callActivityOnCreateView()

```
/**
 * From {@link LayoutInflater.Factory2}.
 */
@Override
public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
    // First let the Activity's Factory try and inflate the view
    final View view = callActivityOnCreateView(parent, name, context, attrs);
    if (view != null) {
        return view;
    }

    // If the Factory didn't handle it, let our createView() method try
    return createView(parent, name, context, attrs);
}

/**
 * From {@link LayoutInflater.Factory2}.
 */
@Override
public View onCreateView(String name, Context context, AttributeSet attrs) {
    return onCreateView(null, name, context, attrs);
}

View callActivityOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
    // Let the Activity's LayoutInflater.Factory try and handle it
    //Window.Callback mOriginalWindowCallback;Callback 在Activity attach中setCallback(),这里是检查Activity是否自己实现了Factory接口。
    if (mOriginalWindowCallback instanceof LayoutInflater.Factory) {
        final View result = ((LayoutInflater.Factory) mOriginalWindowCallback)
                .onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }
    }
    return null;
}
```

- [x] ##### createView()

  紧接着我们细看createView()方法中具体做了哪些事情？

  ```
  @Override
  public View createView(View parent, final String name, @NonNull Context context,
          @NonNull AttributeSet attrs) {
      if (mAppCompatViewInflater == null) {
      //不要误以为这里是实现的LayoutInflater，这个类的功能是提供了createView()、createViewFromTag()方法的具体实现。
          mAppCompatViewInflater = new AppCompatViewInflater();
      }
  
      boolean inheritContext = false;
      //做的工作主要是兼容5.0之前的处理，具体是在createView()方法中
      if (IS_PRE_LOLLIPOP) {
          inheritContext = (attrs instanceof XmlPullParser)
                  // If we have a XmlPullParser, we can detect where we are in the layout
                  ? ((XmlPullParser) attrs).getDepth() > 1
                  // Otherwise we have to use the old heuristic
                  : shouldInheritContext((ViewParent) parent);
      }
  
      return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
              IS_PRE_LOLLIPOP, /* Only read android:theme pre-L (L+ handles this anyway) */
              true, /* Read read app:theme as a fallback at all times for legacy reasons */
              VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */
      );
  }
  ```

- [x] ##### AppCompatViewInflater的createView()

  在AppCompatViewInflater的createView()中，会对所有的控件标签重新初始化成新的带有新属性的控件。这也就是之前说的TintMode支持。具体我们细看代码：

  ```
  public final View createView(View parent, final String name, @NonNull Context context,
          @NonNull AttributeSet attrs, boolean inheritContext,
          boolean readAndroidTheme, boolean readAppTheme, boolean wrapContext) {
      final Context originalContext = context;
  
      // We can emulate Lollipop's android:theme attribute propagating down the view hierarchy
      // by using the parent's context
      if (inheritContext && parent != null) {
          context = parent.getContext();
      }
      if (readAndroidTheme || readAppTheme) {
          // We then apply the theme on the context, if specified
          context = themifyContext(context, attrs, readAndroidTheme, readAppTheme);
      }
      if (wrapContext) {
          context = TintContextWrapper.wrap(context);
      }
  
      View view = null;
  //从这里可以看到我们在XML里使用的TextView等都将会被重新初始化成新的AppCompatXXX,从而支持新的部分属性。
      // We need to 'inject' our tint aware Views in place of the standard framework versions
      switch (name) {
          case "TextView":
              view = new AppCompatTextView(context, attrs);
              break;
          case "ImageView":
              view = new AppCompatImageView(context, attrs);
              break;
          case "Button":
              view = new AppCompatButton(context, attrs);
              break;
          case "EditText":
              view = new AppCompatEditText(context, attrs);
              break;
          case "Spinner":
              view = new AppCompatSpinner(context, attrs);
              break;
          case "ImageButton":
              view = new AppCompatImageButton(context, attrs);
              break;
          case "CheckBox":
              view = new AppCompatCheckBox(context, attrs);
              break;
          case "RadioButton":
              view = new AppCompatRadioButton(context, attrs);
              break;
          case "CheckedTextView":
              view = new AppCompatCheckedTextView(context, attrs);
              break;
          case "AutoCompleteTextView":
              view = new AppCompatAutoCompleteTextView(context, attrs);
              break;
          case "MultiAutoCompleteTextView":
              view = new AppCompatMultiAutoCompleteTextView(context, attrs);
              break;
          case "RatingBar":
              view = new AppCompatRatingBar(context, attrs);
              break;
          case "SeekBar":
              view = new AppCompatSeekBar(context, attrs);
              break;
      }
  	// 手动修改支持android:theme
      if (view == null && originalContext != context) {
          // If the original context does not equal our themed context, then we need to manually
          // inflate it using the name so that android:theme takes effect.
          view = createViewFromTag(context, name, attrs);
      }
  
      if (view != null) {
      //检查android：onClick事件
          // If we have created a view, check its android:onClick
          checkOnClickListener(view, attrs);
      }
  
      return view;
  }
  ```

从上面所有的代码我们可以总结概括出来当我们的Activity继承了AppCompatActivity时，里面的xml会有哪些变化，下面是具体步骤：

1. setContentView()交给LayoutInflater实例化成相应的View
2. AppCompatActivity实现了LayoutFactory2接口，并且设置给了LayoutInflater
3. LayoutInflater解析XML时被实现的LayoutFactory2接口拦截从而实现自定义的View
4. AppCompatActivity中的AppCompatViewInflater赋予了部分View新的TintMode属性并且支持的部分新特性
5. 对于自己的Activity而言，没有处理任何东西，但是对于之前的View却可以有新的属性支持

从而，我们现在可以明白，如果要实现换肤类似的功能，我们只需要实现我们自己的LayoutInflater的Factory2接口就可以给XML中的view带来新的变化。因为Factory2接口中的onCreateView()返回null时走系统的逻辑，不返回null时走自己的逻辑。

---

下面我们找了三款开源的关于换肤的库的对比,以便我们能更好的理解换肤的思路。


| 换肤库 | stars | Issues | 更新时间 | 原理 |
| :--------------: | :----------: | :--------------: | :--------------: | :--------------: |
| **Android-skin-support** | 3669 | 104 | 4个月之前 | 通过Factory2拦截进行换肤 |
| **Android-Skin-Loader** | 2074      | 25         | 3年前 | 通过Factory2拦截进行换肤 |
| **AndroidChangeSkin** | 1499 | 31 | 3年前 | 通过android:tag属性进行自定义value换肤 |



| **特点**： |
| :-: |
| Android-skin-support是模仿AppCompatActivity中的AppCompatViewInflater创造的，它提供了丰富的继承系统内置的View,从而保证了AppCompatActivity下各种View的兼容。是一个比较完美的换肤框架，并且在搜狐新闻等大型app中使用 |
| Android-Skin-Loader是依赖于FragmentActivity，从而不支持新的support特性。 |
| AndroidChangeSkin是通过tag方式进行无代码侵入式替换，但是这种方式存在很多兼容性问题。 |

关于更多细节可以具体研究Android-skin-support，强烈推荐。