# springBoot打包后的jar启动过程分析

- 如果一个jar文件想要被java命令执行必须按照jar文件的文件规范来组织文件目录

### 下面是一个spring boot项目打包后标准的jar文件目录结构

```
spring-learn.jar
    |--BOOT-INF 
        |--BOOT-INF\classes 该文件下的文件是我们最后需要执行的代码
        |--BOOT-INF\lib 该文件下的文件是我们最后需要执行的代码的依赖
    |--META-INF
    |--org 该文件下的文件是一个spring loader文件，应用类加载器首先会加载执行该目录下的代码
```



### 启动类`JarLauncher`

```java
public static void main(String[] args) throws Exception {
		new JarLauncher().launch(args);
	}
```

### 获取类加载器

```java
    /**
    * Launch the application. This method is the initial entry point that should be
    * called by a subclass {@code public static void main(String[] args)} method.
    * @param args the incoming arguments
    * @throws Exception if the application fails to launch
    */
    protected void launch(String[] args) throws Exception {
    JarFile.registerUrlProtocolHandler();
     //创建spring boot自己的类加载器
    ClassLoader classLoader = createClassLoader(getClassPathArchives());
     //getMainClass()获取到springboot中启动类的路径
    launch(args, getMainClass(), classLoader);
    }

    protected ClassLoader createClassLoader(List<Archive> archives) throws Exception {
    List<URL> urls = new ArrayList<>(archives.size());
    for (Archive archive : archives) {
    //获取到每个具体归档文件的路径
    urls.add(archive.getUrl());
    }
    return createClassLoader(urls.toArray(new URL[0]));
    }

	/**
	 * Create a classloader for the specified URLs.
	 * @param urls the URLs
	 * @return the classloader
	 * @throws Exception if the classloader cannot be created
	 */
	protected ClassLoader createClassLoader(URL[] urls) throws Exception {
		return new LaunchedURLClassLoader(urls, getClass().getClassLoader());
	}
```



### 设置当前线程上下文类加载器

```java
	/**
	 * Launch the application given the archive file and a fully configured classloader.
	 * @param args the incoming arguments
	 * @param mainClass the main class to run
	 * @param classLoader the classloader
	 * @throws Exception if the launch fails
	 */
	protected void launch(String[] args, String mainClass, ClassLoader classLoader) throws Exception {
		Thread.currentThread().setContextClassLoader(classLoader);
		createMainMethodRunner(mainClass, args, classLoader).run();
	}
```

### 最终使用反射技术去启动启动类

```java
public void run() throws Exception {
    	//获取当前线程的上下文类加载器
		Class<?> mainClass = Thread.currentThread().getContextClassLoader().loadClass(this.mainClassName);
		Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
    	//null由于main方法是静态的，只属于字节码
		mainMethod.invoke(null, new Object[] { this.args });
	}
```

### 模拟启动过程

```java

public class Test1 {

    public static void main(String[] args) {
        System.out.println("hello");
    }
}

public class Test2 {

    public static void main(String[] args) throws Exception {
        Class<Test1> clazz = Test1.class;
        Method method = clazz.getMethod("main", String[].class);
        //当然此处我们也可以使用new Test2()
        method.invoke(null, new Object[]{null});
    }
}
```

### JDWP远程调式

- 服务端命令：

  ```
   java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005  -jar .\spring-learn-1.0.jar
  ```