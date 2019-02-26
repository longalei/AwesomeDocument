**一、ImageView设置background和src的区别。**
 1.src是图片内容（前景），bg是背景，可以同时使用。
 2.background会根据ImageView组件给定的长宽进行拉伸，而src就存放的是原图的大小，不会进行拉伸 。
 3.scaleType只对src起作用；bg可设置透明度。

**二、ImageView几种不同的设置图片的方式。**
 设置background：
 1.image.setBackground(getResources().getDrawable(R.drawable.blackk));//变形
 2.image.setBackgroundResource(R.drawable.blackk);//变形       3.image.setBackgroundDrawable(getResources().getDrawable(R.drawable.blackk));////变形
 **源码：这三种方法的实质都是调用方法3setBackgroundDrawable()。**

设置src:
 1.image.setImageDrawable(getResources().getDrawable(R.drawable.blackk)); //不会变形
 2.Stringpath=Environment.getExternalStorageDirectory()+File.separator+"test1.jpg";
 Bitmap bm = BitmapFactory.decodeFile(path);
 image.setImageBitmap(bm);//不会变形
 3.image.setImageResource(R.drawable.blackk);//不会变形
 **源码： 其中方法2就是将bitmap转换为drawable然后调用方法1，方法1和方法3都是调用updateDrawable（）方法。**