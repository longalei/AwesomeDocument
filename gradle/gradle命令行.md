> 此处的命令行都是基于mac

##### 查看App 依赖树

``./gradlew app:dependencies``

> 尾部可以配置 --configuration compile 
>
> 因为生成的结果很长，在Terminal内可能会显示不全。所以需要将结果输出到文件内查看。
>
> 所以可以改进为：./gradlew :app:dependencies --configuration compile > dependenciesTree.txt

