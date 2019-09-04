# 模块化工具使用指南

模块化工具是一个基于maven开发的快速开发工具，开发者只需要在maven配置文件中全局指定该插件即可使用。该工具有助于 项目搭建，模块拉取、模块查询、模块推送、创建模块各个环节中提高开发效率，并且和模块市场打通，可以快速实现模块信息复用和能力回收。
<a name="8f2Uy"></a>
### 
<a name="JbRT8"></a>
## 环境准备
<a name="ofVFt"></a>
#### 1.1 安装maven，并且配置maven的环境变量
使用mvn -version出现如下提示，maven就安装好<br />![image.png](https://cdn.nlark.com/yuque/0/2019/png/386602/1566990773894-2eb087f2-3ac9-4ea1-b3d1-bcd8a6ba328a.png#align=left&display=inline&height=278&name=image.png&originHeight=348&originWidth=1216&size=51388&status=done&width=972.8)
<a name="MhJ4t"></a>
#### 1.2 进行spaas插件配置
找到maven的settting.xml配置spaas插件，同时需要指定插件仓库，否则无法拉取插件工具。<br />**插件配置：**
```xml
 <pluginGroups>
 			<pluginGroup>com.deepexi</pluginGroup>
  </pluginGroups>
```

**仓库信息：**
```xml
 <mirror>
      <id>deepexi-central</id>
      <name>public Repository</name>
      <url>http://nexus.deepexi.top/repository/maven-public/</url>
    </mirror>
```

**完整配置：**
```xml
  <pluginGroups>
			<pluginGroup>com.deepexi</pluginGroup>
  </pluginGroups>

	<mirrors>
    <mirror>
      <id>deepexi-central</id>
      <name>public Repository</name>
      <url>http://nexus.deepexi.top/repository/maven-public/</url>
    </mirror>
  </mirrors>
```


<a name="kmrd4"></a>
## 工具使用
<a name="M7thw"></a>
### 1. 创建工程的命令 
<a name="faUwR"></a>
####  1.1 命令使用介绍
创建工程命令是一个快速搭建项目结构命令，是在项目初期，初始化工程时使用到。使用该命令，可以根据spaas部门的工程规范，生成项目结构。同时可以根据参数生成MAVEN 模块信息。**注意**：如果在已有项目下使用该命令，只会生成模块信息。同时如果已经有了项目，只是单独增加模块可以使用  **`mvn spaas:add`** 命令。
<a name="dkgmL"></a>
####  1.2 命令  `mvn spaas:init`
<a name="aiXa2"></a>
####  1.3 命令参数
-DgroupId   #组织标识<br />-DartifactId  #指定项目名称<br />-Dversion  #项目的版本号<br />-Dpackage  #包名<br />-Dmodule  #需要生成的模块名称
<a name="t0EmM"></a>
#### 1.4使用示例
```shell
mvn spaas:init -DgroupId=com.deepexi -DartifactId=spaas-demo-center -Dversion=1.0.0 -Dpackage=com.deepexi.demo -Dmodule=spaas-demo-center-test,spaas-demo-center-abcd
#
#-DgroupId=com.deepexi   #指定组织标识，默认为com.deepexi
#-DartifactId=spaas-demo-center  #指定项目名称，如果命令执行的目录是工程目录，则表示模块名。项目名默认为spaas-demo-center, 模块名默认为spaas-demo-module
#-Dversion=1.0.0  #指定项目的版本号 默认1.0.0-SNAPSHOT
#-Dpackage=com.deepexi.demo  #指定包名，如果没有指定为默认为groupId
#-Dmodule=spaas-demo-center-test,spaas-demo-center-abcd #如果你想一次生成多个模块目录，可以使用这个参数进行指定
```
<a name="DbrCq"></a>
### 2. 拉取模块市场的命令
<a name="cwqtj"></a>
#### 2.1 命令使用介绍
拉取模块命令是用来拉取模块市场存在，且开放的模块源码信息到本地项目中。
<a name="UWR3X"></a>
#### 2.2 命令 `mvn spaas:pull`
<a name="s4M30"></a>
#### 2.3 参数
-DartifactId  模块标识（必须）<br />-Dversion 版本号       
<a name="4UjYP"></a>
#### 2.4 使用示例
```shell
mvn spaas:pull -DartifactId=spaas-task-center-common -Dversion=1.0.0

#-DartifactId=spaas-task-center-commmon 这个参数必填
#-Dversion=1.0.0 这个参数可以省略，默认拉取最新版本信息
```
<a name="ouxil"></a>
#### 
<a name="bmRDR"></a>
### 3 推送模块源码到模块市场命令
<a name="HsGjX"></a>
#### 3.1 命令使用介绍
推送模块命令主要是提供给开发者，快速将当前开发模块推送给到模块市场。注意，首次使用的时候，会提示输入模块市场账户信息。该命令需要在maven工程的模块下执行。
<a name="AmP3B"></a>
#### 3.2 命令  `mvn spaas:push`
<a name="gB88i"></a>
#### 3.3 参数
无
<a name="CuuEg"></a>
#### 3.4 使用示例
```shell
mvn spaas:push

##这个命令必须在模块下执行，会把所有的模块名和版本还有源码文件打包推送到模块市场。
```
<a name="nf1Uh"></a>
#### 3.5 忽略文件
在推送的时候，某些信息，比如编译后的源文件以及IDEA的配置文件我们希望忽略，不要推送到模块市场时，你需要在项目根路径添加名为** .spaasignore **文件，文件内容根据自己的需求添加。<br />示例：
```yaml
## .spaasignore
*.class

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.ear

# virtual machine crash logs, see http://www.java.com/en/download/help/error_hotspot.xml
hs_err_pid*

#eclipse project files
.classpath
.project
.settings/
target/

#Mac OSX
.DS_Store
.svn

#idea project files
*.iml
overlays/
.vscode/

### gradle ###
.gradle
/build/
!gradle/wrapper/gradle-wrapper.jar

### STS ###
.settings/
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
bin/

### IntelliJ IDEA ###
.idea/
*.iws
*.iml
*.ipr
rebel.xml

### NetBeans ###
nbproject/private/
build/
nbbuild/
dist/
nbdist/
.nb-gradle/

### maven ###
target/
*.war
*.ear
*.zip
*.tar
*.tar.gz

### logs ####
/logs/
*.log

### temp ignore ###
*.cache
*.diff
*.patch
*.tmp
*.java~
*.properties~
*.xml~

### system ignore ###
.DS_Store
Thumbs.db
Servers
.metadata
upload
gen_code

## git
/.git/

```
源文件：[spaasignore.rar](https://www.yuque.com/attachments/yuque/0/2019/rar/239804/1566926045097-a53084cf-a3b4-4da2-9d41-3c486e055f33.rar?_lake_card=%7B%22uid%22%3A%22rc-upload-1566925699936-8%22%2C%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2019%2Frar%2F239804%2F1566926045097-a53084cf-a3b4-4da2-9d41-3c486e055f33.rar%22%2C%22name%22%3A%22spaasignore.rar%22%2C%22size%22%3A621%2C%22type%22%3A%22%22%2C%22ext%22%3A%22rar%22%2C%22progress%22%3A%7B%22percent%22%3A0%7D%2C%22status%22%3A%22done%22%2C%22percent%22%3A0%2C%22id%22%3A%22z01pT%22%2C%22card%22%3A%22file%22%7D)
<a name="ATRbQ"></a>
#### 3.6  模块市场账号密码变更    
如果你的账号密码有变更，请在maven默认的配置文件路径下，例如：C:\Users\chenl\.m2 下的 【user.xml】文件中修改用户信息。<br />**注意**：文件格式固定，请勿随便变更<br />示例：
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<user>
	<userName>qiye</userName>
	<password>abcd1234</password>
	<tenantId>2ba56b7c3f8c49598207d2a9fe54970b</tenantId>
</user>
```
 文件参数说明：<br />**userName**:模块市场用户名<br />**password**:模块市场用户登录密码<br />**tenantId**:该用户所属租户

<a name="uxxXX"></a>
### 4. 查询模块市场有什么模块源码的命令
<a name="UxHop"></a>
#### 4.1 命令使用介绍
当开发者不清楚有哪些模块可以提供给拉取的时候，可以使用该命令查询需要的模块信息。
<a name="y7iqV"></a>
#### 4.2 命令 `mvn spaas:search`
<a name="rQNgC"></a>
#### 4.3 参数 
-DartifactId 模块标识<br />-Dsize 分页查询条数，默认15条
<a name="fGhaW"></a>
#### 4.4 使用示例
```shell
mvn spaas:search -DartifactId=test -Dsize=30

#-DartifactId=test 模糊查询模块市场要模块信息返回显示
#-Dsize=30 指定查询条数，默认15条
```


<a name="BgZxc"></a>
### 5. 增加模块命令 
<a name="L2bJs"></a>
#### 5.1 命令使用介绍
使用模块增加命令，前提是在现有maven工程内。该命令只能帮助开发人员创建模块，并不能像<br />`mvn spaas:init `那样能够初始化工程。
<a name="UuW7y"></a>
#### 5.2 命令 `mvn spaas:add`
<a name="L5Y5C"></a>
#### 5.3 参数
-DartifactId  指定模块名称<br />-DpackageName  指定模块包路径

<a name="qdJWc"></a>
#### 5.4 使用示例
```shell
mvn  spaas:add -DartifactId=spaas-task-center -DpackageName=com.deepexi.task
```

