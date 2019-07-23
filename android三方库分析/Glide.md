> ```
> Options保存所有内存或硬盘缓存中的key操作类。
> sizeMultiplier 乘数
> * Applies a multiplier to the {@link com.bumptech.glide.request.target.Target}'s size before
>    * loading the resource. Useful for loading thumbnails or trying to avoid loading huge resources
>    * (particularly {@link Bitmap}s on devices with overly dense screens.
>    
>    DecodeJob
>     A class responsible for decoding resources either from cached data or from the original source
>  * and applying transformations and transcodes.
>  
>    private enum RunReason {
>     /** The first time we've been submitted. */
>     INITIALIZE,
>     /**
>      * We want to switch from the disk cache service to the source executor.
>      */
>     SWITCH_TO_SOURCE_SERVICE,
>     /**
>      * We retrieved some data on a thread we don't own and want to switch back to our thread to
>      * process the data.
>      */
>     DECODE_DATA,
>   }
>  
> ```



### Glide 解析资源时顺序一览代码

DecodeJob 关键类信息

```
  private Stage getNextStage(Stage current) {
    switch (current) {
      case INITIALIZE:
      //从cached resource data开始寻找
      //首先明白resource的定义？？
        return diskCacheStrategy.decodeCachedResource()
            ? Stage.RESOURCE_CACHE : getNextStage(Stage.RESOURCE_CACHE);
      case RESOURCE_CACHE:
      //this request should attempt to decode cached source data.
      //从缓存的源头数据中找
        return diskCacheStrategy.decodeCachedData()
            ? Stage.DATA_CACHE : getNextStage(Stage.DATA_CACHE);
      case DATA_CACHE:
        // Skip loading from source if the user opted to only retrieve the resource from cache.
        //如果设置了onlyRetrieveFromCache那么直接结束，最后从源头上找
        return onlyRetrieveFromCache ? Stage.FINISHED : Stage.SOURCE;
      case SOURCE:
      case FINISHED:
        return Stage.FINISHED;
      default:
        throw new IllegalArgumentException("Unrecognized stage: " + current);
    }
  }
```



##### Glide获取资源的途径

```
public enum DataSource {
  /**
   * Indicates data was probably retrieved locally from the device, although it may have been
   * obtained through a content provider that may have obtained the data from a remote source.
   */
  LOCAL,
  /**
   * Indicates data was retrieved from a remote source other than the device.
   */
  REMOTE,
  /**
   * Indicates data was retrieved unmodified from the on device cache.
   */
  DATA_DISK_CACHE,
  /**
   * Indicates data was retrieved from modified content in the on device cache.
   */
  RESOURCE_DISK_CACHE,
  /**
   * Indicates data was retrieved from the in memory cache.
   */
  MEMORY_CACHE,
}
```



**这里所说的资源是**修改过的文件缓存在文件系统内的

```
from cache files
* containing downsampled/transformed resource data.
```

```
  private DataFetcherGenerator getNextGenerator() {
    switch (stage) {
      case RESOURCE_CACHE:
        return new ResourceCacheGenerator(decodeHelper, this);
      case DATA_CACHE:
        return new DataCacheGenerator(decodeHelper, this);
      case SOURCE:
        return new SourceGenerator(decodeHelper, this);
      case FINISHED:
        return null;
      default:
        throw new IllegalStateException("Unrecognized stage: " + stage);
    }
  }
```



#### ByteBufferBitmapDecoder 关键性InputStream解码Bitmap代码decode



##### SingleRequest 负责资源的加载





##### Glide关键性代码，将配置中的信息从流加载成对应的资源Downsampler

```
  public Resource<Bitmap> decode(InputStream is, int requestedWidth, int requestedHeight,
      Options options, DecodeCallbacks callbacks) throws IOException {
    Preconditions.checkArgument(is.markSupported(), "You must provide an InputStream that supports"
        + " mark()");

    byte[] bytesForOptions = byteArrayPool.get(ArrayPool.STANDARD_BUFFER_SIZE_BYTES, byte[].class);
    BitmapFactory.Options bitmapFactoryOptions = getDefaultOptions();
    bitmapFactoryOptions.inTempStorage = bytesForOptions;

    DecodeFormat decodeFormat = options.get(DECODE_FORMAT);
    DownsampleStrategy downsampleStrategy = options.get(DOWNSAMPLE_STRATEGY);
    boolean fixBitmapToRequestedDimensions = options.get(FIX_BITMAP_SIZE_TO_REQUESTED_DIMENSIONS);

    try {
      Bitmap result = decodeFromWrappedStreams(is, bitmapFactoryOptions,
          downsampleStrategy, decodeFormat, requestedWidth, requestedHeight,
          fixBitmapToRequestedDimensions, callbacks);
      return BitmapResource.obtain(result, bitmapPool);
    } finally {
      releaseOptions(bitmapFactoryOptions);
      byteArrayPool.put(bytesForOptions, byte[].class);
    }
  }
```



```
  private Bitmap decodeFromWrappedStreams(InputStream is,
      BitmapFactory.Options options, DownsampleStrategy downsampleStrategy,
      DecodeFormat decodeFormat, int requestedWidth, int requestedHeight,
      boolean fixBitmapToRequestedDimensions, DecodeCallbacks callbacks) throws IOException {

    int[] sourceDimensions = getDimensions(is, options, callbacks, bitmapPool);
    int sourceWidth = sourceDimensions[0];
    int sourceHeight = sourceDimensions[1];
    String sourceMimeType = options.outMimeType;

    int orientation = ImageHeaderParserUtils.getOrientation(parsers, is, byteArrayPool);
    int degreesToRotate = TransformationUtils.getExifOrientationDegrees(orientation);

    options.inPreferredConfig = getConfig(is, decodeFormat);
    if (options.inPreferredConfig != Bitmap.Config.ARGB_8888) {
      options.inDither = true;
    }

    int targetWidth = requestedWidth == Target.SIZE_ORIGINAL ? sourceWidth : requestedWidth;
    int targetHeight = requestedHeight == Target.SIZE_ORIGINAL ? sourceHeight : requestedHeight;

    calculateScaling(downsampleStrategy, degreesToRotate, sourceWidth, sourceHeight, targetWidth,
        targetHeight, options);

    boolean isKitKatOrGreater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    // Prior to KitKat, the inBitmap size must exactly match the size of the bitmap we're decoding.
    if ((options.inSampleSize == 1 || isKitKatOrGreater)
        && shouldUsePool(is)) {
      int expectedWidth;
      int expectedHeight;
      if (fixBitmapToRequestedDimensions && isKitKatOrGreater) {
        expectedWidth = targetWidth;
        expectedHeight = targetHeight;
      } else {
        float densityMultiplier = isScaling(options)
            ? (float) options.inTargetDensity / options.inDensity : 1f;
        int sampleSize = options.inSampleSize;
        int downsampledWidth = (int) Math.ceil(sourceWidth / (float) sampleSize);
        int downsampledHeight = (int) Math.ceil(sourceHeight / (float) sampleSize);
        expectedWidth = Math.round(downsampledWidth * densityMultiplier);
        expectedHeight = Math.round(downsampledHeight * densityMultiplier);

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
          Log.v(TAG, "Calculated target [" + expectedWidth + "x" + expectedHeight + "] for source"
              + " [" + sourceWidth + "x" + sourceHeight + "]"
              + ", sampleSize: " + sampleSize
              + ", targetDensity: " + options.inTargetDensity
              + ", density: " + options.inDensity
              + ", density multiplier: " + densityMultiplier);
        }
      }
      // If this isn't an image, or BitmapFactory was unable to parse the size, width and height
      // will be -1 here.
      if (expectedWidth > 0 && expectedHeight > 0) {
        setInBitmap(options, bitmapPool, expectedWidth, expectedHeight);
      }
    }
    Bitmap downsampled = decodeStream(is, options, callbacks, bitmapPool);
    callbacks.onDecodeComplete(bitmapPool, downsampled);

    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      logDecode(sourceWidth, sourceHeight, sourceMimeType, options, downsampled,
          requestedWidth, requestedHeight);
    }

    Bitmap rotated = null;
    if (downsampled != null) {
      // If we scaled, the Bitmap density will be our inTargetDensity. Here we correct it back to
      // the expected density dpi.
      downsampled.setDensity(displayMetrics.densityDpi);

      rotated = TransformationUtils.rotateImageExif(bitmapPool, downsampled, orientation);
      if (!downsampled.equals(rotated)) {
        bitmapPool.put(downsampled);
      }
    }

    return rotated;
  }
```



###### 分析：

>1.从流中获取图片的大小
>
>​        其中也可以获取它的方向以及角度
>
>```
>int orientation = ImageHeaderParserUtils.getOrientation(parsers, is, byteArrayPool);
>int degreesToRotate = TransformationUtils.getExifOrientationDegrees(orientation);
>```
>
>2.根据选中的策略，比如指定的图片大小对原图片大小进行转换行处理



```
 static void calculateScaling(DownsampleStrategy downsampleStrategy, int degreesToRotate,
      int sourceWidth, int sourceHeight, int targetWidth, int targetHeight,
      BitmapFactory.Options options) {
    // We can't downsample source content if we can't determine its dimensions.
    if (sourceWidth <= 0 || sourceHeight <= 0) {
      return;
    }

    final float exactScaleFactor;
    if (degreesToRotate == 90 || degreesToRotate == 270) {
      // If we're rotating the image +-90 degrees, we need to downsample accordingly so the image
      // width is decreased to near our target's height and the image height is decreased to near
      // our target width.
      //noinspection SuspiciousNameCombination
      exactScaleFactor = downsampleStrategy.getScaleFactor(sourceHeight, sourceWidth,
          targetWidth, targetHeight);
    } else {
      exactScaleFactor =
          downsampleStrategy.getScaleFactor(sourceWidth, sourceHeight, targetWidth, targetHeight);
    }

    if (exactScaleFactor <= 0f) {
      throw new IllegalArgumentException("Cannot scale with factor: " + exactScaleFactor
          + " from: " + downsampleStrategy);
    }
    SampleSizeRounding rounding = downsampleStrategy.getSampleSizeRounding(sourceWidth,
        sourceHeight, targetWidth, targetHeight);
    if (rounding == null) {
      throw new IllegalArgumentException("Cannot round with null rounding");
    }

    int outWidth = (int) (exactScaleFactor * sourceWidth + 0.5f);
    int outHeight = (int) (exactScaleFactor * sourceHeight + 0.5f);

    int widthScaleFactor = sourceWidth / outWidth;
    int heightScaleFactor = sourceHeight / outHeight;

    int scaleFactor = rounding == SampleSizeRounding.MEMORY
        ? Math.max(widthScaleFactor, heightScaleFactor)
        : Math.min(widthScaleFactor, heightScaleFactor);

    int powerOfTwoSampleSize;
    // BitmapFactory does not support downsampling wbmp files on platforms <= M. See b/27305903.
    if (Build.VERSION.SDK_INT <= 23
        && NO_DOWNSAMPLE_PRE_N_MIME_TYPES.contains(options.outMimeType)) {
      powerOfTwoSampleSize = 1;
    } else {
      powerOfTwoSampleSize = Math.max(1, Integer.highestOneBit(scaleFactor));
      if (rounding == SampleSizeRounding.MEMORY
          && powerOfTwoSampleSize < (1.f / exactScaleFactor)) {
        powerOfTwoSampleSize = powerOfTwoSampleSize << 1;
      }
    }

    float adjustedScaleFactor = powerOfTwoSampleSize * exactScaleFactor;

    options.inSampleSize = powerOfTwoSampleSize;
    // Density scaling is only supported if inBitmap is null prior to KitKat. Avoid setting
    // densities here so we calculate the final Bitmap size correctly.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      options.inTargetDensity = (int) (1000 * adjustedScaleFactor + 0.5f);
      options.inDensity = 1000;
    }
    if (isScaling(options)) {
      options.inScaled = true;
    } else {
      options.inDensity = options.inTargetDensity = 0;
    }

    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Calculate scaling"
          + ", source: [" + sourceWidth + "x" + sourceHeight + "]"
          + ", target: [" + targetWidth + "x" + targetHeight + "]"
          + ", exact scale factor: " + exactScaleFactor
          + ", power of 2 sample size: " + powerOfTwoSampleSize
          + ", adjusted scale factor: " + adjustedScaleFactor
          + ", target density: " + options.inTargetDensity
          + ", density: " + options.inDensity);
    }
  }
```





```
到最后float densityMultiplier = isScaling(options)
            ? (float) options.inTargetDensity / options.inDensity : 1f;
        int sampleSize = options.inSampleSize;
        这两个参数关键性影响最后的值
```

