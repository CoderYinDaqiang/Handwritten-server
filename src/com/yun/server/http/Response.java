package com.yun.server.http;

import javax.servlet.http.Cookie;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *  响应行:
 *  协议版本  状态码  原因短语
 *  HTTP/1.1  200   OK
 *
 *  响应头:
 * Location: http://www.cskaoyan.com/指示新的资源的位置，一般和302、307状态码搭配使用
 * Server: apache tomcat 指示服务器的类型
 * Content-Encoding: gzip 服务器发送的数据采用的编码类型
 * Content-Length: 80 告诉浏览器正文的长度
 * Content-Language: zh-cn服务发送的文本的语言
 * Content-Type: text/html;  服务器发送的内容的MIME类型
 * Last-Modified: Tue, 11 Jul 2000 18:23:51 GMT文件的最后修改时间
 * Refresh: 1;url=http://www.cskaoyan.com指示客户端刷新频率。单位是秒
 * Content-Disposition: attachment; filename=aaa.zip指示客户端保存文件
 * Set-Cookie: SS=Q0=5Lb_nQ; path=/search服务器端发送的Cookie
 * Expires: 0
 * Cache-Control: no-cache (1.1)
 * Connection: close/Keep-Alive
 * Date: Tue, 11 Jul 2000 18:23:51 GMT
 *
 * 空行
 *
 * 响应体
 * */
public class Response extends BaseResponse{

    private int status;

    private Map<String , String> responseHeaders;

    private OutputStream outputStream;

    private PrintWriter writer;

    private StringWriter stringWriter;

    private byte[] responseBody;

    public Response(OutputStream outputStream) {
        this.responseHeaders = new HashMap<String, String>();
        this.outputStream = outputStream;
        this.status = 200;
        this.setHeader("Content-Type", "text/html;charset=utf-8");
        //真正去写的时StringWriter
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter,true);
    }

    public void responde(){
        String responseLine = "HTTP/1.1 " + this.status + "\r\n";
        String responseHeaders = "";
        Set<String> keySet = this.responseHeaders.keySet();
        for (String key : keySet) {
            String value = this.responseHeaders.get(key);
            responseHeaders = responseHeaders + key + ":" + value + "\r\n";
        }
        getResposneBody();

        try {
            byte[] responseLineHeader = (responseLine + responseHeaders + "\r\n").getBytes("utf-8");
            byte[] responsebyte = new byte[responseLineHeader.length + this.responseBody.length];
            // A  begin B begin    length of A copy to B
            System.arraycopy(responseLineHeader,0,responsebyte,0,responseLineHeader.length);
            System.arraycopy(this.responseBody,0,responsebyte,responseLineHeader.length,this.responseBody.length);
            this.outputStream.write(responsebyte,0,responsebyte.length);
            this.outputStream.flush();
            this.outputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getResposneBody() {
        // responseBody==null illustrates that mainProcessLogic don't write any stream.
        if(this.responseBody == null){
            try {
                this.responseBody = this.stringWriter.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
    public PrintWriter getWriter() {
        return writer;
    }

    public void setContentType(String mime){
        this.responseHeaders.put("Content-Type", mime);
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setHeader(String key, String value) {
        this.responseHeaders.put(key, value);
    }

    @Override
    public void addCookie(Cookie cookie){
        String name = cookie.getName();
        String value = cookie.getValue();
        this.responseHeaders.put("Set-Cookie", name + "=" + value);
    }
}
