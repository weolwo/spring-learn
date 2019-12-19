# Springboot启动过程分析

### 首先从一个入口程序开始

```java
@SpringBootApplication
public class SpringLearnApplication {

    public static void main(String[] args) {
        System.out.println(SpringLearnApplication.class.getClassLoader());
        //从这个run方法开始
        SpringApplication.run(SpringLearnApplication.class, args);
    }

}

public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		//这个方法分为两步，首先我们来看创建SpringApplication对象
		return new SpringApplication(primarySources).run(args);
	}
	
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		...
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
    	//推断应用类型，servlet还是reactor and so on
		this.webApplicationType = WebApplicationType.deduceFromClasspath();
    	//实例化ApplicationContextInitializer接口的所有实现类
		setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
    //实例化ApplicationListener接口的所有实现类
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
    //推断主应用程序的fully qualified class name
		this.mainApplicationClass = deduceMainApplicationClass();
	}
 //deduceMainApplicationClass()方法的核心代码：
StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				if ("main".equals(stackTraceElement.getMethodName())) {
					return Class.forName(stackTraceElement.getClassName());
				}
			}
```

### 加载spring factory

```java
	private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
		//用于存储从"META-INF/spring.factories"文件中加载的类的fully qualified class name
		MultiValueMap<String, String> result = cache.get(classLoader);//第一次为空
		if (result != null) {
			return result;
		}

		try {
			Enumeration<URL> urls = (classLoader != null ?
					classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
			result = new LinkedMultiValueMap<>();
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
                //分别获取到beans，springboot,autoconfigture下面的META-INF/spring.factories文件路径
				UrlResource resource = new UrlResource(url);
				Properties properties = PropertiesLoaderUtils.loadProperties(resource);
				for (Map.Entry<?, ?> entry : properties.entrySet()) {
					String factoryTypeName = ((String) entry.getKey()).trim();
					for (String factoryImplementationName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
                        //把分别获从beans，springboot,autoconfigture下面获的META-INF/spring.factories文件中获取到的所有fully qualified class name添加到容器中，待后面的实例化
						result.add(factoryTypeName, factoryImplementationName.trim());
					}
				}
			}
			cache.put(classLoader, result);
			return result;
		}
		...
	}
```



### 第二步，启动程序

```java
public ConfigurableApplicationContext run(String... args) {
    	//该秒表用于计算程序执行的时间
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    	//表示是一个运行于服务器的程序，没有键盘
		configureHeadlessProperty();
    	//获取到所有实现SpringApplicationRunListener该接口的实现类的集合
		SpringApplicationRunListeners listeners = getRunListeners(args);
    	//调用监听器starting()方法启动对应用的监听
		listeners.starting();
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            //根据应用程序类型创建对应的环境，此处创建的是StandardServletEnvironment环境
			ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
			configureIgnoreBeanInfo(environment);
            //打印Banner信息，默认为控制台输出spring标志及版本
			Banner printedBanner = printBanner(environment);
            //通过实例化AnnotationConfigServletWebServerApplicationContext创建上下文环境
			context = createApplicationContext();
            //实例化实现了SpringBootExceptionReporter该接口的所有类
			exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
			prepareContext(context, environment, listeners, applicationArguments, printedBanner);
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			stopWatch.stop();
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
			}
			listeners.started(context);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}
```

