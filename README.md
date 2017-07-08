# spring_issues_proxy_factory
Shows issues with spring BenaProxyFactory and any type-based search (e.g. Provider) 

The issue stem from having Spring perform a type-search when we also have a bean factory that relies on another bean (e.g. ProxyFactoryBean) in the application context.
In order to find the object type, Spring instantiate the bean factory. The factory is fully instantiated and initialized, which means that the dependencies are instantiated and supplied as well. However, if the dependencies cannot, at that time, be fully instantiated, the whole process ends with an exception that is swallowed in Spring and the factory is deemed "not cache-able".
When we have multiple such Dependant beans and multiple type-searches, we reach a very high number of instantiations - exponential growth for every new factory and multiplication for every type search.

In a complex application this has been observed to cause 100,000's of objects to be instantiated (with about 500 beans defined), causing startup to take multiple minutes and GC to spike.

# Sample
The sample has 5 beans. Each has a Provider<DummyBeanA> to trigger a type-search during bean initialization. The beans are declared in the applicationContext.xml in order (bean1, bean2...) and for each bean we declare a ProxyFactoryBean (bean1Factory, bean2Factory...) also in order. Each bean rely on the previous bean (bean 5 on bean 4, bean 4 on bean 3).
Note that order of the beans in the xml and their dependencies are inverse on purpose. The type-search scans the beans in the order they are discovered (either from component-scan or from the xml) and the inverse order cause the beans to be impossible to initialize in the XMl order (throwing the aforementioned exception).

# Solution
Yet to be found...

# Execution output

+65: (Bean1): Constructor (times: 1, total constructions 1)
+94: (Bean2): Constructor (times: 1, total constructions 2)
+154: (Bean3): Constructor (times: 1, total constructions 3)
+158: (Bean4): Constructor (times: 1, total constructions 4)
+165: (Bean5): Constructor (times: 1, total constructions 5)
+174: (Bean5): Constructor (times: 2, total constructions 6)
+175: (Bean4): Constructor (times: 2, total constructions 7)
+176: (Bean4): Constructor (times: 3, total constructions 8)
+177: (Bean4): Constructor (times: 4, total constructions 9)
+178: (Bean3): Constructor (times: 2, total constructions 10)
+179: (Bean5): Constructor (times: 3, total constructions 11)
+181: (Bean5): Constructor (times: 4, total constructions 12)
+181: (Bean3): Constructor (times: 3, total constructions 13)
+183: (Bean3): Constructor (times: 4, total constructions 14)
+184: (Bean5): Constructor (times: 5, total constructions 15)
+190: (Bean5): Constructor (times: 6, total constructions 16)
+200: (Bean3): Constructor (times: 5, total constructions 17)
+201: (Bean4): Constructor (times: 5, total constructions 18)
+202: (Bean4): Constructor (times: 6, total constructions 19)
+203: (Bean3): Constructor (times: 6, total constructions 20)
+217: (Bean3): Constructor (times: 7, total constructions 21)
+220: (Bean4): Constructor (times: 7, total constructions 22)
+220: (Bean3): Constructor (times: 8, total constructions 23)
+226: (Bean3): Constructor (times: 9, total constructions 24)
+227: (Bean3): Constructor (times: 10, total constructions 25)
+228: (Bean4): Constructor (times: 8, total constructions 26)
+229: (Bean5): Constructor (times: 7, total constructions 27)
+230: (Bean5): Constructor (times: 8, total constructions 28)
+230: (Bean4): Constructor (times: 9, total constructions 29)
+231: (Bean4): Constructor (times: 10, total constructions 30)
+232: (Bean4): Constructor (times: 11, total constructions 31)
+233: (Bean5): Constructor (times: 9, total constructions 32)
+235: (Bean5): Constructor (times: 10, total constructions 33)
+252: === Init done
+252: (Bean1): Called NOP
+252: (Bean2): Called NOP
+252: (Bean3): Called NOP
+252: (Bean4): Called NOP
+252: (Bean5): Called NOP
+332: (Bean1$$EnhancerBySpringCGLIB$$79cb3e3e): Constructor (times: 1, total constructions 34)
+351: (Bean1): Called NOP
+358: (Bean2$$EnhancerBySpringCGLIB$$693c271f): Constructor (times: 1, total constructions 35)
+365: (Bean2): Called NOP
+373: (Bean3$$EnhancerBySpringCGLIB$$58ad1000): Constructor (times: 1, total constructions 36)
+380: (Bean3): Called NOP
+387: (Bean4$$EnhancerBySpringCGLIB$$481df8e1): Constructor (times: 1, total constructions 37)
+412: (Bean4): Called NOP
+423: (Bean5$$EnhancerBySpringCGLIB$$378ee1c2): Constructor (times: 1, total constructions 38)
+435: (Bean5): Called NOP
+435: === NOP done


# Execution output with logging post processor (see application context xml)

+0: 		Spring startup: before instantiation of [bean1] of [com.rb.springissues.sample.Bean1]
+1: (Bean1): Constructor (times: 1, total constructions 1)
+21: 		Spring startup: after instantiation of [bean1] of [com.rb.springissues.sample.Bean1]
+32: 		Spring startup: before initialization of [bean1] of [com.rb.springissues.sample.Bean1]
+33: 		Spring startup: after initialization of [bean1] of [com.rb.springissues.sample.Bean1]
+33: 		Spring startup summary: time for bean [bean1] of [com.rb.springissues.sample.Bean1]: instantiation: [21], initialization [1], total [33]
+33: 		Spring startup: before instantiation of [bean2] of [com.rb.springissues.sample.Bean2]
+33: (Bean2): Constructor (times: 1, total constructions 2)
+40: 		Spring startup: after instantiation of [bean2] of [com.rb.springissues.sample.Bean2]
+42: 		Spring startup: before instantiation of [bean1Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+58: 		Spring startup: after instantiation of [bean1Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+139: 		Spring startup: before initialization of [bean1Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+139: 		Spring startup: after initialization of [bean1Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+139: 		Spring startup summary: time for bean [bean1Factory] of [org.springframework.aop.framework.ProxyFactoryBean]: instantiation: [16], initialization [0], total [97]
+139: 		Spring startup: before instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+147: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+149: 		Spring startup: before instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+154: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+154: 		Spring startup: before instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+154: (Bean3): Constructor (times: 1, total constructions 3)
+155: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+158: *** after inst: already defined bean2Factory
+158: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+158: 		Spring startup: before instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+161: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+162: 		Spring startup: before instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+162: (Bean4): Constructor (times: 1, total constructions 4)
+163: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+166: *** after inst: already defined bean2Factory
+166: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+168: 		Spring startup: before instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+180: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+181: 		Spring startup: before instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+181: (Bean5): Constructor (times: 1, total constructions 5)
+182: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+184: *** after inst: already defined bean2Factory
+184: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+189: *** after inst: already defined bean5Factory
+189: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+189: (Bean5): Constructor (times: 2, total constructions 6)
+189: *** after inst: already defined bean5
+189: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+189: *** after inst: already defined bean2Factory
+189: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+189: *** after inst: already defined bean4Factory
+189: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+189: (Bean4): Constructor (times: 2, total constructions 7)
+190: *** after inst: already defined bean4
+190: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+190: *** after inst: already defined bean2Factory
+190: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+192: (Bean4): Constructor (times: 3, total constructions 8)
+192: *** after inst: already defined bean4
+192: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+195: *** after inst: already defined bean2Factory
+195: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+196: *** after inst: already defined bean4Factory
+196: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+198: *** after inst: already defined bean4Factory
+198: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+198: (Bean4): Constructor (times: 4, total constructions 9)
+198: *** after inst: already defined bean4
+198: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+200: *** after inst: already defined bean2Factory
+200: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+201: *** after inst: already defined bean3Factory
+201: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+201: (Bean3): Constructor (times: 2, total constructions 10)
+201: *** after inst: already defined bean3
+201: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+202: *** after inst: already defined bean2Factory
+202: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+208: *** after inst: already defined bean5Factory
+208: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+209: (Bean5): Constructor (times: 3, total constructions 11)
+209: *** after inst: already defined bean5
+209: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+209: *** after inst: already defined bean2Factory
+209: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+214: *** after inst: already defined bean5Factory
+214: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+214: (Bean5): Constructor (times: 4, total constructions 12)
+214: *** after inst: already defined bean5
+214: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+216: *** after inst: already defined bean2Factory
+216: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+216: *** after inst: already defined bean3Factory
+216: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+216: (Bean3): Constructor (times: 3, total constructions 13)
+216: *** after inst: already defined bean3
+216: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+218: *** after inst: already defined bean2Factory
+218: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+220: (Bean3): Constructor (times: 4, total constructions 14)
+220: *** after inst: already defined bean3
+220: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+220: *** after inst: already defined bean2Factory
+220: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+226: *** after inst: already defined bean3Factory
+226: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+231: *** after inst: already defined bean5Factory
+231: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+231: (Bean5): Constructor (times: 5, total constructions 15)
+231: *** after inst: already defined bean5
+231: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+234: *** after inst: already defined bean2Factory
+234: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+234: *** after inst: already defined bean3Factory
+234: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+235: *** after inst: already defined bean5Factory
+235: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+235: (Bean5): Constructor (times: 6, total constructions 16)
+235: *** after inst: already defined bean5
+235: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+235: *** after inst: already defined bean2Factory
+235: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+236: *** after inst: already defined bean3Factory
+236: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+236: (Bean3): Constructor (times: 5, total constructions 17)
+236: *** after inst: already defined bean3
+236: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+242: *** after inst: already defined bean2Factory
+242: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+242: *** after inst: already defined bean4Factory
+242: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+242: (Bean4): Constructor (times: 5, total constructions 18)
+242: *** after inst: already defined bean4
+242: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+243: *** after inst: already defined bean2Factory
+243: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+247: *** after inst: already defined bean4Factory
+247: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+247: (Bean4): Constructor (times: 6, total constructions 19)
+247: *** after inst: already defined bean4
+247: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+247: *** after inst: already defined bean2Factory
+247: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+247: *** after inst: already defined bean3Factory
+247: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+248: (Bean3): Constructor (times: 6, total constructions 20)
+248: *** after inst: already defined bean3
+248: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+249: *** after inst: already defined bean2Factory
+249: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+251: (Bean3): Constructor (times: 7, total constructions 21)
+251: *** after inst: already defined bean3
+251: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+251: *** after inst: already defined bean2Factory
+251: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+252: *** after inst: already defined bean3Factory
+252: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+257: (Bean4): Constructor (times: 7, total constructions 22)
+257: *** after inst: already defined bean4
+257: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+260: *** after inst: already defined bean2Factory
+260: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+261: *** after inst: already defined bean3Factory
+261: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+261: (Bean3): Constructor (times: 8, total constructions 23)
+261: *** after inst: already defined bean3
+261: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+261: *** after inst: already defined bean2Factory
+261: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+261: *** after inst: already defined bean4Factory
+261: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+262: *** after inst: already defined bean4Factory
+262: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+263: (Bean3): Constructor (times: 9, total constructions 24)
+263: *** after inst: already defined bean3
+263: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+263: *** after inst: already defined bean2Factory
+263: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+263: *** after inst: already defined bean3Factory
+263: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+263: *** after inst: already defined bean4Factory
+263: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+265: 		Spring startup: before initialization of [bean2] of [com.rb.springissues.sample.Bean2]
+265: 		Spring startup: after initialization of [bean2] of [com.rb.springissues.sample.Bean2]
+265: 		Spring startup summary: time for bean [bean2] of [com.rb.springissues.sample.Bean2]: instantiation: [7], initialization [0], total [232]
+265: (Bean3): Constructor (times: 10, total constructions 25)
+266: *** after inst: already defined bean3
+266: 		Spring startup: after instantiation of [bean3] of [com.rb.springissues.sample.Bean3]
+266: *** after inst: already defined bean2Factory
+270: 		Spring startup: after instantiation of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+271: 		Spring startup: before initialization of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+271: 		Spring startup: after initialization of [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+271: 		Spring startup summary: time for bean [bean2Factory] of [org.springframework.aop.framework.ProxyFactoryBean]: instantiation: [127], initialization [0], total [132]
+271: *** after inst: already defined bean3Factory
+271: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+271: *** after inst: already defined bean4Factory
+271: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+271: (Bean4): Constructor (times: 8, total constructions 26)
+271: *** after inst: already defined bean4
+271: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+271: *** after inst: already defined bean3Factory
+271: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+272: *** after inst: already defined bean5Factory
+272: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+272: (Bean5): Constructor (times: 7, total constructions 27)
+272: *** after inst: already defined bean5
+272: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+272: *** after inst: already defined bean3Factory
+272: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+273: *** after inst: already defined bean5Factory
+273: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+273: (Bean5): Constructor (times: 8, total constructions 28)
+273: *** after inst: already defined bean5
+273: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+274: *** after inst: already defined bean3Factory
+274: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+274: *** after inst: already defined bean4Factory
+274: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+274: (Bean4): Constructor (times: 9, total constructions 29)
+274: *** after inst: already defined bean4
+274: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+274: *** after inst: already defined bean3Factory
+274: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+275: (Bean4): Constructor (times: 10, total constructions 30)
+275: *** after inst: already defined bean4
+275: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+276: *** after inst: already defined bean3Factory
+276: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+276: *** after inst: already defined bean4Factory
+276: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+278: 		Spring startup: before initialization of [bean3] of [com.rb.springissues.sample.Bean3]
+278: 		Spring startup: after initialization of [bean3] of [com.rb.springissues.sample.Bean3]
+278: 		Spring startup summary: time for bean [bean3] of [com.rb.springissues.sample.Bean3]: instantiation: [112], initialization [0], total [124]
+278: (Bean4): Constructor (times: 11, total constructions 31)
+278: *** after inst: already defined bean4
+278: 		Spring startup: after instantiation of [bean4] of [com.rb.springissues.sample.Bean4]
+278: *** after inst: already defined bean3Factory
+278: 		Spring startup: after instantiation of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+279: 		Spring startup: before initialization of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+279: 		Spring startup: after initialization of [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+279: 		Spring startup summary: time for bean [bean3Factory] of [org.springframework.aop.framework.ProxyFactoryBean]: instantiation: [129], initialization [0], total [130]
+279: *** after inst: already defined bean4Factory
+279: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+279: *** after inst: already defined bean5Factory
+279: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+279: (Bean5): Constructor (times: 9, total constructions 32)
+279: *** after inst: already defined bean5
+279: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+280: *** after inst: already defined bean4Factory
+280: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+285: 		Spring startup: before initialization of [bean4] of [com.rb.springissues.sample.Bean4]
+285: 		Spring startup: after initialization of [bean4] of [com.rb.springissues.sample.Bean4]
+285: 		Spring startup summary: time for bean [bean4] of [com.rb.springissues.sample.Bean4]: instantiation: [116], initialization [0], total [123]
+285: (Bean5): Constructor (times: 10, total constructions 33)
+285: *** after inst: already defined bean5
+285: 		Spring startup: after instantiation of [bean5] of [com.rb.springissues.sample.Bean5]
+285: *** after inst: already defined bean4Factory
+285: 		Spring startup: after instantiation of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+286: 		Spring startup: before initialization of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+286: 		Spring startup: after initialization of [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+286: 		Spring startup summary: time for bean [bean4Factory] of [org.springframework.aop.framework.ProxyFactoryBean]: instantiation: [127], initialization [0], total [128]
+286: *** after inst: already defined bean5Factory
+286: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+287: 		Spring startup: before initialization of [bean5] of [com.rb.springissues.sample.Bean5]
+287: 		Spring startup: after initialization of [bean5] of [com.rb.springissues.sample.Bean5]
+287: 		Spring startup summary: time for bean [bean5] of [com.rb.springissues.sample.Bean5]: instantiation: [104], initialization [0], total [106]
+287: *** after inst: already defined bean5Factory
+287: 		Spring startup: after instantiation of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+287: 		Spring startup: before initialization of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+287: 		Spring startup: after initialization of [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]
+287: 		Spring startup summary: time for bean [bean5Factory] of [org.springframework.aop.framework.ProxyFactoryBean]: instantiation: [119], initialization [0], total [119]
+287: 		Spring startup: before instantiation of [aProvider] of [com.rb.springissues.sample.DummyBeanAProvider]
+292: 		Spring startup: after instantiation of [aProvider] of [com.rb.springissues.sample.DummyBeanAProvider]
+292: 		Spring startup: before initialization of [aProvider] of [com.rb.springissues.sample.DummyBeanAProvider]
+293: 		Spring startup: after initialization of [aProvider] of [com.rb.springissues.sample.DummyBeanAProvider]
+293: 		Spring startup summary: time for bean [aProvider] of [com.rb.springissues.sample.DummyBeanAProvider]: instantiation: [5], initialization [0], total [5]
+296: === Init done
+296: (Bean1): Called NOP
+296: (Bean2): Called NOP
+296: (Bean3): Called NOP
+296: (Bean4): Called NOP
+296: (Bean5): Called NOP
+373: (Bean1$$EnhancerBySpringCGLIB$$79cb3e3e): Constructor (times: 1, total constructions 34)
+373: *** after init: already defined bean1Factory
+373: 		Spring startup: after initialization of [bean1Factory] of [com.rb.springissues.sample.Bean1$$EnhancerBySpringCGLIB$$79cb3e3e]
+373: 		Spring startup summary: time for bean [bean1Factory] of [com.rb.springissues.sample.Bean1$$EnhancerBySpringCGLIB$$79cb3e3e]: instantiation: [16], initialization [234], total [331]
+392: (Bean1): Called NOP
+400: (Bean2$$EnhancerBySpringCGLIB$$693c271f): Constructor (times: 1, total constructions 35)
+400: *** after init: already defined bean2Factory
+400: 		Spring startup: after initialization of [bean2Factory] of [com.rb.springissues.sample.Bean2$$EnhancerBySpringCGLIB$$693c271f]
+400: 		Spring startup summary: time for bean [bean2Factory] of [com.rb.springissues.sample.Bean2$$EnhancerBySpringCGLIB$$693c271f]: instantiation: [127], initialization [129], total [261]
+417: (Bean2): Called NOP
+444: (Bean3$$EnhancerBySpringCGLIB$$58ad1000): Constructor (times: 1, total constructions 36)
+445: *** after init: already defined bean3Factory
+445: 		Spring startup: after initialization of [bean3Factory] of [com.rb.springissues.sample.Bean3$$EnhancerBySpringCGLIB$$58ad1000]
+445: 		Spring startup summary: time for bean [bean3Factory] of [com.rb.springissues.sample.Bean3$$EnhancerBySpringCGLIB$$58ad1000]: instantiation: [129], initialization [166], total [296]
+461: (Bean3): Called NOP
+471: (Bean4$$EnhancerBySpringCGLIB$$481df8e1): Constructor (times: 1, total constructions 37)
+471: *** after init: already defined bean4Factory
+471: 		Spring startup: after initialization of [bean4Factory] of [com.rb.springissues.sample.Bean4$$EnhancerBySpringCGLIB$$481df8e1]
+471: 		Spring startup summary: time for bean [bean4Factory] of [com.rb.springissues.sample.Bean4$$EnhancerBySpringCGLIB$$481df8e1]: instantiation: [127], initialization [185], total [313]
+477: (Bean4): Called NOP
+499: (Bean5$$EnhancerBySpringCGLIB$$378ee1c2): Constructor (times: 1, total constructions 38)
+499: *** after init: already defined bean5Factory
+499: 		Spring startup: after initialization of [bean5Factory] of [com.rb.springissues.sample.Bean5$$EnhancerBySpringCGLIB$$378ee1c2]
+499: 		Spring startup summary: time for bean [bean5Factory] of [com.rb.springissues.sample.Bean5$$EnhancerBySpringCGLIB$$378ee1c2]: instantiation: [119], initialization [212], total [331]
+515: (Bean5): Called NOP
+516: === NOP done
