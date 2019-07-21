### ViewModel——以注重生命周期的方式管理界面相关的数据     

正如ViewModel所言，第一：注重生命周期方式，第二：用于界面管理数据，而我们所言的界面，通常是Activity、Fragment.我们现在就来发现它是怎么注重生命周期管理这些数据的？

---

**日常开发场景**

> 在我们开发的过程中，经常要遇到这些问题。
>
> 一、Android framework销毁重建Activity、Fragment时如何对数据进行保存？
>
> 常见的onSaveInstanceState()方法保存的数据有限制。那么就意味着大部分数据进行重新请求？
>
> 二、Activity、Fragment重建上资源的消耗问题
>
> 三、Activity、Fragment业务逻辑太多导致测试困难
>
> 四、Activity、Fragment之间的数据传递
>
> - setArguments数据限制
> - 接口数据传递麻烦（假设Activity有3~4个Fragment,其中一个Fragment的数据更新了需要同步到其它Fragment上。）
> - EventBus不好维护
>
> 五、Activity中处理异步数据的内存泄露



---

那么现在使用ViewModel，只要使用**3-4**行的代码就可以统统解决这些问题，极大的解耦了你的业务逻辑，非常灵活与方便。

需要了解到的类以及使用Demo如下：

- ViewModel
- LiveData<T>
- ViewModelProviders

第一步：创建你的ModelController继承ViewModel并且使用LiveData包裹你的Model

```
public class ArticleList extends ViewModel {

    private MutableLiveData<List<Article>> articleList;

    public LiveData<List<Article>> getArticleList() {
        if (articleList == null) {
            articleList = new MutableLiveData<List<Article>>();
            //同、异步获取数据
            loadArticles();
        }
        return articleList;
    }

    private void loadArticles() {
        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setTitle("这是一条标题");
        article.setContent("这是一条内容");
        articles.add(article);
        articleList.setValue(articles);
    }

    public void attend() {
        if (articleList != null) {
            List<Article> value = articleList.getValue();
            if (value != null && !value.isEmpty()) {
                value.get(0).setFllowed(true);
                articleList.setValue(value);
            }
        }

    }

    public void cancelAttend() {
        if (articleList != null) {
            List<Article> value = articleList.getValue();
            if (value != null && !value.isEmpty()) {
                value.get(0).setFllowed(false);
                articleList.setValue(value);
            }
        }
    }
}
```

第二步：在你的Activity、Fragment中使用

```
public class ViewModelActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ViewModelActivity.class);
        context.startActivity(intent);
    }

    private TextView content, activity;
    private FrameLayout containerOne, containerTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_model);
        activity = findViewById(R.id.activity);
        content = findViewById(R.id.content);
        containerOne = findViewById(R.id.container_one);
        containerTwo = findViewById(R.id.container_two);
        ArticleList articleList = ViewModelProviders.of(this).get(ArticleList.class);
        articleList.getArticleList().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                for (int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);
                    String contents = article.getTitle() + "\n" + article.getContent();
                    if (article.isFllowed()) {
                        contents += "\n" + "我被关注了";
                    } else {
                        contents += "\n" + "我被取消关注了";
                    }
                    content.setText(contents);
                }
            }
        });
        initFragments();
        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleActivity.start(ViewModelActivity.this);
            }
        });
    }
```

像这样，你可以通过ViewModelProviders获取你的ModelController对象，操纵你的数据源。这些都会在相应的observe方法中得到反馈。

---

下面我们就来慢慢探究ViewModel是怎样来帮助你管理Activity、Fragment上的数据的，并且又是怎样来帮你处理Activity与Fragment中的数据传递的？

其实，在研究这个过程时，我们不妨先猜想一下它是怎么做的？第一个，怎么处理生命周期问题？是静态内存还是给Activity添加一个无大小的Fragment然后setRetainInstance(true)还是Activity的onRetainCustomNonConfigurationInstance()? 第二个，怎样快速在Activity、Fragment中传递数据和维护一块共享的数据源？还是静态？

先报个料，所有的都是在Activity或FragmentActivity上的静态类NonConfigurationInstances上做的文章。下面我们就具体来分析它是怎么做的？

##### ViewModel

> ViewModel是个抽象类，里面只有一个onCleared()方法，其实这也很好理解，当ViewModel不再使用时就会调用此方法，做一些清楚订阅类的操作。这个思想有点类似MVP中的P层

```
public abstract class ViewModel {
    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @SuppressWarnings("WeakerAccess")
    protected void onCleared() {
    }
}
```

##### LiveData<T>

> LiveData也是个抽象类，类似的（ComputableLiveData<T>支持对数据进行再处理）其下的实现类有：MutableLiveData<T>(观察一个LiveData)、MediatorLiveData<T>(支持观察多个LiveData)
>
> LiveData是个数据存储类，并且有着生命周期的功能。在生命周期活跃的时候才会对数据进行响应。
>
> 使用方式：
>
> ```
>         ArticleList articleList = ViewModelProviders.of(this).get(ArticleList.class);
>         articleList.getArticleList().observe(this, new Observer<List<Article>>() {
>             @Override
>             public void onChanged(@Nullable List<Article> articles) {
>                 for (int i = 0; i < articles.size(); i++) {
>                     Article article = articles.get(i);
>                     String contents = article.getTitle() + "\n" + article.getContent();
>                     if (article.isFllowed()) {
>                         contents += "\n" + "我被关注了";
>                     } else {
>                         contents += "\n" + "我被取消关注了";
>                     }
>                     content.setText(contents);
>                 }
>             }
>         });
>         
>         //下面对应的是observe方法
>      @MainThread
>     public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
>     //生命周期处理，当Activity销毁时不再更新Ui
>         if (owner.getLifecycle().getCurrentState() == DESTROYED) {
>             // ignore
>             return;
>         }
>         //Activity引用进入LifecycleBoundObserver
>         LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
>         ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
>         if (existing != null && !existing.isAttachedTo(owner)) {
>             throw new IllegalArgumentException("Cannot add the same observer"
>                     + " with different lifecycles");
>         }
>         if (existing != null) {
>             return;
>         }
>         owner.getLifecycle().addObserver(wrapper);
>     }
>         
>        调用通过：
>          articleList.setValue(value);
>          articleList.postValue(value);//异步，在子线程中处理，最后转发到主线程
> ```
>
> 这里我要做一点补充，大家可以发现LifecycleOwner这个接口，里面可以获取Activity的生命周期，
>
> ```
> public interface LifecycleOwner {
>     /**
>      * Returns the Lifecycle of the provider.
>      *
>      * @return The lifecycle of the provider.
>      */
>     @NonNull
>     Lifecycle getLifecycle();
> }
> ```
>
> 这个接口的实现是在2017.9 [Android 26.1.0版本更新](https://developer.android.com/topic/libraries/support-library/revisions.html#26-1-0)上添加进来的。所以这里可以明白，LiveData包裹的数据源因此有了生命周期控制的本能，从而在Activity销毁时不会做Ui上的更新。同时,我们再继续观察LifecycleBoundObserver
>
> ```
>     class LifecycleBoundObserver extends ObserverWrapper implements GenericLifecycleObserver {
>         @NonNull final LifecycleOwner mOwner;
> 
>         LifecycleBoundObserver(@NonNull LifecycleOwner owner, Observer<T> observer) {
>             super(observer);
>             mOwner = owner;
>         }
> 
>         @Override
>         boolean shouldBeActive() {
>             return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
>         }
> 
>         @Override
>         public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
>             if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
>             //在这里我们可以发现Activity销毁时会清除Observer(Activity引用)
>                 removeObserver(mObserver);
>                 return;
>             }
>             activeStateChanged(shouldBeActive());
>         }
> 
>         @Override
>         boolean isAttachedTo(LifecycleOwner owner) {
>             return mOwner == owner;
>         }
> 
>         @Override
>         void detachObserver() {
>             mOwner.getLifecycle().removeObserver(this);
>         }
>     }
>     
> ```
> 
>从而，根据代码分析，Activity销毁时不再对数据进行更新和持有Activity的引用，这有效的避免了在异步处理时内存泄露的风险。
>

##### ViewModelProviders

> 这个类是ViewModel中的核心，使用它的简单方式只是通过:
>
> ```
> ArticleList articleList = ViewModelProviders.of(this).get(ArticleList.class);
> ```
>
> 接下来，我们就详细分析这样的一个过程。
>
> 第一步： ViewModelProviders.of(this)
>
> ```
> 
>     /**
>      * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
>      * is alive. More detailed explanation is in {@link ViewModel}.
>      * <p>
>      * It uses the given {@link Factory} to instantiate new ViewModels.
>      *
>      * @param activity an activity, in whose scope ViewModels should be retained
>      * @param factory  a {@code Factory} to instantiate new ViewModels
>      * @return a ViewModelProvider instance
>      */
>     @NonNull
>     @MainThread
>     public static ViewModelProvider of(@NonNull FragmentActivity activity,
>             @Nullable Factory factory) {
>         Application application = checkApplication(activity);
>         if (factory == null) {
>             factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
>         }
>         return new ViewModelProvider(ViewModelStores.of(activity), factory);
>     }
>     
>     //ViewModelProvider.class
>      public ViewModelProvider(@NonNull ViewModelStore store, @NonNull Factory factory)      
>      {
>         mFactory = factory;
>         this.mViewModelStore = store;
>      }
> 这里创建了一个ViewModelProvider。在ViewModelProvider中提供了ViewModelStore和Factory。ViewModelStore是个数据存储类，专门用来存储ViewModel。
> 
> ViewModelStore.class
> public class ViewModelStore {
> 
>     private final HashMap<String, ViewModel> mMap = new HashMap<>();
> 
>     final void put(String key, ViewModel viewModel) {
>         ViewModel oldViewModel = mMap.put(key, viewModel);
>         if (oldViewModel != null) {
>             oldViewModel.onCleared();
>         }
>     }
> 
>     final ViewModel get(String key) {
>         return mMap.get(key);
>     }
> 
>     /**
>      *  Clears internal storage and notifies ViewModels that they are no longer used.
>      */
>     public final void clear() {
>         for (ViewModel vm : mMap.values()) {
>             vm.onCleared();
>         }
>         mMap.clear();
>     }
> }
> 我们可以看到当ViewModelStore调用clear()方法时，所有的ViewModel的onCleared()方法都会被调用。
> 
> 回过头来，我们紧接着查看ViewModelStores.of(activity)中处理了什么？
> ```

##### ViewModelStores.of(activity)中处理了什么？

> ```
> public class ViewModelStores {
> 
>     private ViewModelStores() {
>     }
> 
>     /**
>      * Returns the {@link ViewModelStore} of the given activity.
>      *
>      * @param activity an activity whose {@code ViewModelStore} is requested
>      * @return a {@code ViewModelStore}
>      */
>     @NonNull
>     @MainThread
>     public static ViewModelStore of(@NonNull FragmentActivity activity) {
>         if (activity instanceof ViewModelStoreOwner) {
>             return ((ViewModelStoreOwner) activity).getViewModelStore();
>         }
>         return holderFragmentFor(activity).getViewModelStore();
>     }
> 
>     /**
>      * Returns the {@link ViewModelStore} of the given fragment.
>      *
>      * @param fragment a fragment whose {@code ViewModelStore} is requested
>      * @return a {@code ViewModelStore}
>      */
>     @NonNull
>     @MainThread
>     public static ViewModelStore of(@NonNull Fragment fragment) {
>         if (fragment instanceof ViewModelStoreOwner) {
>             return ((ViewModelStoreOwner) fragment).getViewModelStore();
>         }
>         return holderFragmentFor(fragment).getViewModelStore();
>     }
> }
> 注意这里，在所有的of()方法中都有 if (activity instanceof ViewModelStoreOwner) 的判断，这就意味着ViewModelStoreOwner是个接口，并且在Activity、Fragment中实现了，同时这个接口是后期的平台上加进来的,经过查阅资料这是在2018年2月份加进来的。在27.1.0上实现了这个接口。正式如此，在FragmentActivity中有个静态类NonConfigurationInstances，维护着ViewModelStore对象。所以在Activity重建的时候会通过getLastNonConfigurationInstance()检查NonConfigurationInstances对象从而对ViewModelStore做了处理，这和我们之前MVP实践时分析如何对P层持久化一个道理。部分代码如下：
> protected void onCreate(@Nullable Bundle savedInstanceState) {
>         this.mFragments.attachHost((Fragment)null);
>         super.onCreate(savedInstanceState);
>         FragmentActivity.NonConfigurationInstances nc = (FragmentActivity.NonConfigurationInstances)this.getLastNonConfigurationInstance();
>         if (nc != null && nc.viewModelStore != null && this.mViewModelStore == null) {
>             this.mViewModelStore = nc.viewModelStore;
>         }
>    
>    //在Activity销魂的时候调用mViewModelStore.clear()
>        protected void onDestroy() {
>         super.onDestroy();
>         if (this.mViewModelStore != null && !this.isChangingConfigurations()) {
>             this.mViewModelStore.clear();
>         }
>         this.mFragments.dispatchDestroy();
>     }
>     
>     对ViewModelStore在Activity重建时持久化处理了，也意味着ViewModel不会在重建时重新被初始化，从而减少部分性能损耗。并且在destroy的时候调用了ViewModel的onCleared(),这样就绑定好了生命周期。
>     
> ```
>
> [Android Library更新地址](<https://developer.android.com/topic/libraries/support-library/revisions.html#26-1-0>)
>
> 回过头来，细问Android在前期版本又是如何处理的了？这就要回到holderFragmentFor(fragment).getViewModelStore();这行代码的处理了。

##### Android 27.1.0如何实现ViewModel的生命周期控制？

> 经过发现，我们可以知道holderFragmentFor(activity).getViewModelStore();创建了一个无大小的HolderFragment并依赖在这个Activity上，并且在Fragment的构造方法中调用了setRetainInstance(true);保证了Activity的销毁重建不会影响Fragment的生命周期，从而保证了ViewModel一直在Activity的作用域上。
>
> 部分代码如下：
>
> ```
>        public HolderFragment() {
>         setRetainInstance(true);
>     }
>    
>    /**
>      * @hide
>      */
>     @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
>     public static HolderFragment holderFragmentFor(FragmentActivity activity) {
>         return sHolderFragmentManager.holderFragmentFor(activity);
>     }
> ```

经过我们上面对ViewModelProviders.of(this)的分析，可以明白Android在不同的版本上对ViewModel的处理是不一样的。顺便补充一下:这些类是Android Jetpack下[lifecycle组件](<https://developer.android.com/reference/android/arch/lifecycle/package-summary.html>)是在2017.9 [Android 26.1.0版本更新](https://developer.android.com/topic/libraries/support-library/revisions.html#26-1-0)上添加进来的。

```
Revision 26.1.0 Release
(September 2017)
This is a special release to integrate the Support Library with Lifecycles from Architecture Components. If you are not using the Lifecycles library, you don’t need to update from 26.0.2. For more information, see the Architecture Components release notes.
```

##### 分析 ViewModelProviders.of(this).get(ArticleList.class)中的get()

>get()从ViewModelProvider中查找对应的ViewModel
>
>```
>/**
>* Returns an existing ViewModel or creates a new one in the scope (usually, a fragment or
>* an activity), associated with this {@code ViewModelProvider}.
>* <p>
>* The created ViewModel is associated with the given scope and will be retained
>* as long as the scope is alive (e.g. if it is an activity, until it is
>* finished or process is killed).
>*
>* @param modelClass The class of the ViewModel to create an instance of it if it is not
>*                   present.
>* @param <T>        The type parameter for the ViewModel.
>* @return A ViewModel that is an instance of the given type {@code T}.
>*/
>@NonNull
>@MainThread
>public <T extends ViewModel> T get(@NonNull Class<T> modelClass) {
>   String canonicalName = modelClass.getCanonicalName();
>   if (canonicalName == null) {
>       throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
>   }
>   return get(DEFAULT_KEY + ":" + canonicalName, modelClass);
>}
>
>
>
>/**
>* Returns an existing ViewModel or creates a new one in the scope (usually, a fragment or
>* an activity), associated with this {@code ViewModelProvider}.
>* <p>
>* The created ViewModel is associated with the given scope and will be retained
>* as long as the scope is alive (e.g. if it is an activity, until it is
>* finished or process is killed).
>*
>* @param key        The key to use to identify the ViewModel.
>* @param modelClass The class of the ViewModel to create an instance of it if it is not
>*                   present.
>* @param <T>        The type parameter for the ViewModel.
>* @return A ViewModel that is an instance of the given type {@code T}.
>*/
>@NonNull
>@MainThread
>public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
>   ViewModel viewModel = mViewModelStore.get(key);
>
>   if (modelClass.isInstance(viewModel)) {
>       //noinspection unchecked
>       return (T) viewModel;
>   } else {
>       //noinspection StatementWithEmptyBody
>       if (viewModel != null) {
>           // TODO: log a warning.
>       }
>   }
>
>   viewModel = mFactory.create(modelClass);
>   mViewModelStore.put(key, viewModel);
>   //noinspection unchecked
>   return (T) viewModel;
>}
>
>
>我们可以明白，如果没有从ViewModelStore中找到相应的ViewModel,将会创建一个新的ViewModel，并且把它存储其中。
>```
>
>这样，就完成了对ViewModel生命周期的处理。从而也为Activity与Fragment之间传递数据提供了方便。

#####Question???

综上，我们大致可以明白ViewModel的工作方式，Android在更新库中完全的支持带来了使用ViewModel处理上述问题异常的方便。在这里我们思考一个问题。因为通过:

```
 ArticleList articleList = ViewModelProviders.of(this).get(ArticleList.class);
```

就可以获取到对应的ViewModel，也就意味着可以对这个Activity上的数据做任意的修改。那么我们在平常的开发中要处理Activity、Fragment打开另一个Activity、Fragment时，怎样优雅的获取上一个Activity、Fragment对象，以便可以操作它的数据，从而不用使用onActivityResult?这个留给大家思考。



**参考官方链接**

[ViewModel Overview](<https://developer.android.com/topic/libraries/architecture/viewmodel>)