# HotFix
## 一个ClassLoader可以包含多个dex文件，每个dex文件是一个Element，多个dex文件排列成一个有序的数组dexElements，当找类的时候，会按顺序遍历dex文件，然后从当前遍历的dex文件中找类，如果找到类则返回，如果找不到从下一个dex文件继续查找.那么这样的话，我们可以在这个dexElements中去做一些事情，比如，在这个数组的第一个元素放置我们的patch.jar，里面包含修复过的类，这样的话，当遍历findClass的时候，我们修复的类就会被查找到，从而替代有bug的类。