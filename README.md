# SimpleCommunity
一个初学者做的关于Android的物业管理系统（无服务器端代码，服务器是PHP通过JSON进行数据解析），该项目请求服务器的方式均为异步线程。细节有待完善。

该系统的主要界面：

[**登录和注册界面**]()

用户名为大陆电话和密码不少于6位都有校验。忘记密码只是输入用户名对拖动图片验证通过即可跳到修改密码界面。如图所示

![image](https://github.com/Anogi88/SimpleCommunity/blob/master/images/登录.png)

**主界面**

主界面由本地图片轮播和服务器获取最新的五条公告翻滚。查询，缴费，投诉，报修和公告都可进行相应操作。界面如图所示。

![image](https://github.com/Anogi88/SimpleCommunity/blob/master/images/主页.jpg)

点击支付按钮需要校验是否设置了支付密码，没有设置将跳转到设置支付密码界面。



![image](https://github.com/Anogi88/SimpleCommunity/blob/master/images/缴费.png)

**社区界面**

点击每一条可进入到文章详情页，右上有搜索（模糊搜索）含搜索记录和发布的功能。

![image](https://github.com/Anogi88/SimpleCommunity/blob/master/images/社区.jpg)

**我的页面**

可以选择拍照和图片进行裁剪做头像。点击退出登录退到登录界面。

![image](https://github.com/Anogi88/SimpleCommunity/blob/master/images/我的.png)
