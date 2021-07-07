package com.yuancome.tag;

import com.yuancome.servlet.GlobalHttpServletResponseHolder;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * 自定义通用响应头标签
 *
 * @author yuancome
 * @date 2021/7/7
 */

public class CommonResponseHeadersTag extends SimpleTagSupport {
    private String header;
    private String value;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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
    private boolean isValid() {
        return (header != null && header.length() > 0) && (value != null && value.length() > 0);
    }
}
