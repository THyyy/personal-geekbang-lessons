## 小马哥 Java 训练营一期

### 第一周

#### 作业内容：

- 参考 com.salesmanager.shop.tags.CommonResponseHeadersTag 实现一个自定义的 Tag，将 Hard Code 的 Header 名值对，变为属性配置的方式，类似于:

```xml
<tag> 
  <name>common-response-headers</name> 
  <tag-class>com.salesmanager.shop.tags.CommonResponseH eadersTag</tag-class>
	<body-content>empty</body-content>
	<attribute>
		<name>Cache-Control</name> 
  	<required>false</required> <rtexprvalue>no-cache</rtexprvalue> <type>java.lang.String</type>
  </attribute>
  <attribute>
		<name>Pragma</name>
 		<required>false</required> <rtexprvalue>no-cache</rtexprvalue> <type>java.lang.String</type>
  </attribute>
  <attribute>
		<name>Expires</name> 
    <required>false</required> <rtexprvalue>-1</rtexprvalue> <type>java.lang.Long</type>
  </attribute>
</tag>
```

1. 实现自定义标签
2. 编写自定义标签 tld 文件 3. 部署到 Servlet 应用

**不要求整合到当前，可以做一个 demo 项目**

#### 完成内容：

1. 实现 `javax.servlet.jsp.tagext.SimpleTagSupport` 接口：

```java
public class CommonResponseHeadersTag extends SimpleTagSupport {

    private String header;
    private String value;
    //todo getter,setter

    @Override
    public void doTag() throws JspException, IOException {
        HttpServletResponse response = GlobalHttpServletResponseHolder.getHttpServletResponse();
        if (response != null && isValid() )
            response.setHeader(header, value);

        super.doTag();
    }

    /**
     * 校验响应头是否合法
     * @return 是否合法
     */
    private boolean isValid();
}
```

2. 全局 HTTP Servlet Response 本地线程容器

```java
public class GlobalHttpServletResponseHolder {

    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();

    public static void setHttpServletResponse(HttpServletResponse response) {
        responseHolder.set(response);
    }

    public static HttpServletResponse getHttpServletResponse() {
        return responseHolder.get();
    }

    public static void reset() {
        responseHolder.remove();
    }

}
```

3. 实现 Filter 接口，过滤请求的同时注入 HttpServletResponse 到 GlobalHttpServletResponseHolder

```java
public class InjectServletResponseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            //注入全局响应
            System.out.println("inject global servlet response");
            GlobalHttpServletResponseHolder.setHttpServletResponse((HttpServletResponse) servletResponse);
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
```

4. 自定义标签文件：customResponseHeader.tld

```xml
<taglib xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0"
        xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd">
    <description><![CDATA["Shopizer tag libs"]]></description>
    <display-name>"ResponseHeaderTags"</display-name>
    <tlib-version>2.3</tlib-version>
    <short-name>rh</short-name>

    <tag>
        <name>ResponseHeaders</name>
        <tag-class>com.yuancome.tag.CommonResponseHeadersTag</tag-class>
        <body-content>empty</body-content>
        <attribute>
            <name>header</name>
            <required>true</required>
            <type>java.lang.String</type>
        </attribute>

        <attribute>
            <name>value</name>
            <required>true</required>
            <type>java.lang.String</type>
        </attribute>
    </tag>
</taglib>
```

5. 在 JSP 文件中调用

```jsp
<%@ taglib prefix="ex" uri="/WEB-INF/customResponseHeader.tld"%>
<ex:ResponseHeaders header="Cache-Control" value="true" />
<ex:ResponseHeaders header="X-REQUEST-ID" value="123456" />
```